package org.qiunet.flash.handler.context.session;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.qiunet.flash.handler.common.MessageHandler;
import org.qiunet.flash.handler.common.player.IMessageActor;
import org.qiunet.flash.handler.context.response.push.BaseByteBufMessage;
import org.qiunet.flash.handler.context.response.push.IChannelMessage;
import org.qiunet.flash.handler.context.sender.IChannelMessageSender;
import org.qiunet.flash.handler.context.session.config.DSessionConfig;
import org.qiunet.flash.handler.netty.server.constants.CloseCause;
import org.qiunet.flash.handler.netty.server.constants.ServerConstants;
import org.qiunet.flash.handler.util.ChannelUtil;
import org.qiunet.utils.exceptions.CustomException;
import org.qiunet.utils.logger.LoggerType;
import org.slf4j.Logger;

import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;

/***
 *
 * @author qiunet
 * 2022/4/26 15:13
 */
abstract class BaseSession implements ISession {

	protected static final Logger logger = LoggerType.DUODUO_FLASH_HANDLER.getLogger();
	/**
	 * 配置
	 */
	protected DSessionConfig sessionConfig = DSessionConfig.DEFAULT_CONFIG;

	protected Channel channel;

	protected void setChannel(Channel channel) {
		if (channel != null) {
			// 测试可能为null
			channel.closeFuture().addListener(f -> this.close(CloseCause.CHANNEL_CLOSE));
		}
		this.channel = channel;
	}
	/**
	 * 设置 session 的参数
	 */
	public ISession sessionConfig(DSessionConfig config) {
		Preconditions.checkState(config.isDefault_flush() || (config.getFlush_delay_ms() >= 5 && config.getFlush_delay_ms() < 3000));
		this.sessionConfig = config;
		return this;
	}
	/**
	 * session是否是活跃的.
	 * @return
	 */
	@Override
	public boolean isActive() {
		return channel != null && channel.isActive();
	}

	@Override
	public void flush() {
		channel.flush();
	}

	@Override
	public Channel channel() {
		return channel;
	}

	@Override
	public String getIp() {
		return ChannelUtil.getIp(channel);
	}
	@Override
	public <T> T getAttachObj(AttributeKey<T> key) {
		return channel.attr(key).get();
	}

	@Override
	public <T> void attachObj(AttributeKey<T> key, T obj) {
		channel.attr(key).set(obj);
	}

	private final AtomicBoolean closed = new AtomicBoolean();
	public void close(CloseCause cause) {
		if (! closed.compareAndSet(false, true)) {
			// 避免多次调用close. 多次调用监听.
			return;
		}

		IMessageActor attachObj = getAttachObj(ServerConstants.MESSAGE_ACTOR_KEY);
		if (attachObj == null || attachObj.msgExecuteIndex() == null) {
			if (attachObj != null) {
				attachObj.destroy();
			}
			this.closeChannel(cause);
			return;
		}

		if (((MessageHandler) attachObj).isDestroyed()
		|| ((MessageHandler<?>) attachObj).inSelfThread()) {
			// 直接执行.
			this.closeSession(cause);
		}else {
			attachObj.addMessage(p -> {
				this.closeSession(cause);
			});
		}
	}

	/**
	 * 关闭session
	 * @param cause
	 */
	private void closeSession(CloseCause cause) {
		try {
			IMessageActor attachObj = getAttachObj(ServerConstants.MESSAGE_ACTOR_KEY);
			closeListeners.forEach((name, cl) -> {
				try {
					cl.close(this, cause);
				}catch (Exception e) {
					logger.error("close session exception: ", e);
				}
			});

			// 没有loginSuccess的那种
			if (closeListeners.isEmpty()) {
				attachObj.destroy();
			}
		}finally {
			this.closeChannel(cause);
		}
	}
	/**
	 * 关闭channel
	 * @param cause
	 */
	private void closeChannel(CloseCause cause) {
		if (channel == null) {
			return;
		}
		logger.info("Session [{}] close by cause [{}]", this, cause.getDesc());
		if ((channel.isActive() || channel.isOpen())) {
			logger.info("Session [{}] closed", this);
			this.flush();
		}
		channel.close();
	}

	@Override
	public ChannelFuture sendMessage(IChannelMessage<?> message) {
		return this.sendMessage(message, true);
	}

	@Override
	public ChannelFuture sendMessage(IChannelMessage<?> message, boolean flush) {
		return this.doSendMessage(message, flush);
	}

	/**
	 * 发送message
	 * @param message
	 * @param flush
	 * @return
	 */
	protected ChannelFuture doSendMessage(IChannelMessage<?> message, boolean flush) {
		return this.realSendMessage(message, flush);
	}

	private static final GenericFutureListener<? extends Future<? super Void>> listener = f -> {
		// ClosedChannelException 不打印了
		if (! f.isSuccess() && ! (f.cause() instanceof ClosedChannelException)) {
			logger.error("channel send message error:", f.cause());
		}
	};

	/**
	 * 发送消息在这里
	 * @param message
	 * @param flush
	 * @return
	 */
	private ChannelFuture realSendMessage(IChannelMessage<?> message, boolean flush) {
		ChannelPromise promise = channel.newPromise();
		promise.addListener(listener);
		channel.eventLoop().execute(() -> {
			this.realSendMessage0(promise, message, flush);
		});
		return promise;
	}
	private void realSendMessage0(ChannelPromise promise, IChannelMessage<?> message, boolean flush) {
		IMessageActor messageActor = getAttachObj(ServerConstants.MESSAGE_ACTOR_KEY);
		if (! this.channel.isOpen()) {

			if (logger.isDebugEnabled() && message.debugOut()) {
				String identityDesc = messageActor == null ? channel.id().asShortText() : messageActor.getIdentity();
				logger.debug("[{}] discard [{}({})] message: {}", identityDesc, channel.attr(ServerConstants.HANDLER_TYPE_KEY).get(), channel.id().asShortText(), message._toString());
			}

			if (message instanceof BaseByteBufMessage && ((BaseByteBufMessage<?>) message).isByteBufPrepare()) {
					((BaseByteBufMessage<?>) message).getByteBuf().release();
			}
			message.recycle();
			return;
		}

		if ( logger.isInfoEnabled() && messageActor != null && message.debugOut()) {
			logger.info("[{}] [{}({})] >>> {}", messageActor.getIdentity(), channel.attr(ServerConstants.HANDLER_TYPE_KEY).get(), channel.id().asShortText(), message._toString());
		}
		if (flush) {
			this.channel.writeAndFlush(message, promise);
		}else {
			this.channel.write(message, promise);
		}
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",", "[", "]");
		if (channel != null) {
			boolean isServer = channel.hasAttr(ServerConstants.HANDLER_PARAM_KEY);
			sj.add(isServer ? "Server": "Client");
			sj.add("Type = "+channel.attr(ServerConstants.HANDLER_TYPE_KEY).get());
			IMessageActor messageActor = getAttachObj(ServerConstants.MESSAGE_ACTOR_KEY);
			if (messageActor != null) {
				sj.add(messageActor.getIdentity());
			}
			sj.add("ID = " + channel.id().asShortText());
			if (isServer) {
				sj.add("Ip = " + getIp());
			}
		}
		return sj.toString();
	}

	@Override
	public void addCloseListener(String name, SessionCloseListener listener, boolean check) {
		if (this.closeListeners.containsKey(name)) {
			if (check) {
				throw new CustomException("close listener {} repeated!", name);
			}else {
				logger.error("add close listener {} repeated", name);
			}
		}
		this.closeListeners.put(name, listener);
	}

	@Override
	public void clearCloseListener(){
		this.closeListeners.clear();
	}

	protected final Map<String, SessionCloseListener> closeListeners = Maps.newConcurrentMap();

	@Override
	public IChannelMessageSender getSender() {
		return this;
	}
}
