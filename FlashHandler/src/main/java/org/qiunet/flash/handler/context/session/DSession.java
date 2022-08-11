package org.qiunet.flash.handler.context.session;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.Attribute;
import io.netty.util.concurrent.GenericFutureListener;
import org.qiunet.flash.handler.common.enums.ServerConnType;
import org.qiunet.flash.handler.context.response.push.IChannelMessage;
import org.qiunet.flash.handler.context.sender.IChannelMessageSender;
import org.qiunet.flash.handler.context.session.config.DSessionConfig;
import org.qiunet.flash.handler.context.session.config.DSessionConnectParam;
import org.qiunet.flash.handler.context.session.future.DMessageContentFuture;
import org.qiunet.flash.handler.netty.server.constants.CloseCause;
import org.qiunet.flash.handler.netty.server.constants.ServerConstants;
import org.qiunet.utils.exceptions.CustomException;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Session 的 父类
 * Created by qiunet.
 * 17/11/26
 */
public class DSession extends BaseSession implements IChannelMessageSender {
	/**
	 * 如果是使用DSession 连接, 连接成功前. 发送的消息存储这里
	 */
	private final ConcurrentLinkedQueue<DMessageContentFuture> queue = new ConcurrentLinkedQueue<>();
	/**
	 * 锁. 防止连接时候. 发送信息错误放置.
	 */
	private final ReentrantLock sessionLock = new ReentrantLock();
	/**
	 * 连接中标志
	 */
	private final AtomicBoolean connecting = new AtomicBoolean();
	/**
	 * 配置
	 */
	private DSessionConfig sessionConfig = DSessionConfig.DEFAULT_CONFIG;
	/**
	 * 写次数计数
	 */
	private final AtomicInteger counter = new AtomicInteger();
	/**
	 * 判断是否已经在计时flush
	 */
	private final AtomicBoolean flushScheduling = new AtomicBoolean();
	/**
	 * 如果是作为客户端的DSession, 这里是连接参数
	 */
	private DSessionConnectParam connectParam;
	/**
	 * 绑定的kcp session
	 */
	protected KcpSession kcpSession;

	protected DSession(){}
	/**
	 * 作为客户端. 也可以先使用连接参数. 构造一个DSession. 先发送消息.
	 * 消息在连接前缓存
	 * @param connectParam
	 */
	public DSession(DSessionConnectParam connectParam) {
		this.connectParam = connectParam;
		this.connect();
	}

	public DSession(Channel channel) {
		this.setChannel(channel);
	}

	/**
	 * 连接
	 */
	private void connect() {
		Preconditions.checkNotNull(connectParam);
		if (! connecting.compareAndSet(false, true)) {
			return;
		}
		GenericFutureListener<ChannelFuture> listener = f -> {
			if (! f.isSuccess()) {
				throw new CustomException("Tcp Connect fail!");
			}
			try {
				sessionLock.lock();
				DMessageContentFuture msg;
				while ((msg = queue.poll()) != null) {
					if (msg.isCancelled()) {
						continue;
					}

					ChannelFuture future1 = this.doSendMessage(msg.getMessage(), false);
					DMessageContentFuture finalMsg = msg;
					future1.addListener(f1 -> {
						if (f1.isSuccess()) {
							finalMsg.setSuccess();
						}else {
							finalMsg.setFailure(future1.cause());
						}
					});
				}
				this.flush0();
				connecting.set(false);
			}finally {
				sessionLock.unlock();
			}
		};

		ChannelFuture connectFuture = connectParam.connect();
		connectFuture.channel().attr(ServerConstants.SESSION_KEY).set(this);
		this.setChannel(connectFuture.channel());
		connectFuture.addListener(listener);
	}

	/**
	 * 设置 session 的参数
	 */
	public DSession sessionConfig(DSessionConfig config) {
		Preconditions.checkState(config.isDefault_flush() || (config.getFlush_delay_ms() >= 5 && config.getFlush_delay_ms() < 10000));
		this.sessionConfig = config;
		return this;
	}

	/**
	 * flush
	 */
	private synchronized void flush0(){
		counter.set(0);
		channel.flush();
	}


	@Override
	public void flush() {
		this.flush0();
	}

	@Override
	public ChannelFuture sendMessage(IChannelMessage<?> message) {
		return this.sendMessage(message, sessionConfig.isDefault_flush());
	}

	@Override
	public ChannelFuture sendMessage(IChannelMessage<?> message, boolean flush) {
		if (connecting.get()) {
			try {
				sessionLock.lock();
				if (connecting.get()) {
					DMessageContentFuture contentFuture = new DMessageContentFuture(channel, message);
					this.queue.add(contentFuture);
					return contentFuture;
				}
			}finally {
				sessionLock.unlock();
			}
		}
		return this.doSendMessage(message, flush);
	}

	public ChannelFuture doSendMessage(IChannelMessage<?> message, boolean flush) {
		if (flush) {
			return this.realSendMessage(message, true);
		}

		ChannelFuture future;
		synchronized (this) {
			future = this.realSendMessage(message, false);
		}

		if (counter.incrementAndGet() >= 10) {
			this.flush0();
			return future;
		}

		if (flushScheduling.compareAndSet(false, true)) {
			// 不取消future 也没有损失.
			channel.eventLoop().schedule(this::flush0, sessionConfig.getFlush_delay_ms(), TimeUnit.MILLISECONDS);
			this.flushScheduling.set(false);
		}
		return future;
	}

	@Override
	public boolean isKcpSessionPrepare() {
		return this.kcpSession != null && this.kcpSession.isActive();
	}

	@Override
	public void bindKcpSession(KcpSession kcpSession) {
		Attribute<ServerConnType> attr = this.channel.attr(ServerConstants.HANDLER_TYPE_KEY);
		if (attr.get() != ServerConnType.TCP && attr.get() != ServerConnType.WS) {
			throw new CustomException("Not support!");
		}
		if (this.kcpSession != null) {
			this.closeListeners.remove("CloseKcpSession");
			this.kcpSession.close(CloseCause.LOGIN_REPEATED);
		}
		this.kcpSession = kcpSession;
		this.addCloseListener("CloseKcpSession", (session, cause) -> {
			logger.debug("Close kcp session!");
			this.kcpSession.close(cause);
		});
	}

	@Override
	public ChannelFuture sendKcpMessage(IChannelMessage<?> message) {
		if (this.kcpSession == null || ! this.kcpSession.isActive()) {
			logger.warn("Not bind kcp session or session inactive!");
			return this.sendMessage(message, true);
		}
		return this.kcpSession.sendKcpMessage(message);
	}

	public KcpSession getKcpSession() {
		return kcpSession;
	}
}
