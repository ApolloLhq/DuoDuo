package org.qiunet.flash.handler.netty.server.config.adapter;

import io.jpower.kcp.netty.KcpException;
import io.netty.channel.ChannelFuture;
import org.qiunet.cross.actor.CrossPlayerActor;
import org.qiunet.cross.node.ServerNode;
import org.qiunet.cross.node.ServerNodeManager;
import org.qiunet.flash.handler.common.player.IMessageActor;
import org.qiunet.flash.handler.common.player.PlayerActor;
import org.qiunet.flash.handler.context.request.data.IChannelData;
import org.qiunet.flash.handler.context.session.ISession;
import org.qiunet.flash.handler.context.status.StatusResultException;
import org.qiunet.flash.handler.netty.server.config.adapter.message.ServerCloseRsp;
import org.qiunet.flash.handler.netty.server.config.adapter.message.ServerExceptionResponse;
import org.qiunet.flash.handler.netty.server.config.adapter.message.StatusTipsRsp;
import org.qiunet.flash.handler.netty.server.constants.ServerConstants;
import org.qiunet.utils.async.LazyLoader;
import org.qiunet.utils.logger.LoggerType;

import java.io.IOException;

/***
 *
 * 长连接情况下, 需要根据给定参数给出业务自己的session 和playerActor的实例对象
 *
 * @author qiunet
 * 2020/3/8 09:31
 **/
public interface IStartupContext<T extends IMessageActor<T>> {
	LazyLoader<IChannelData> SERVER_EXCEPTION_MESSAGE = new LazyLoader<>(ServerExceptionResponse::new);
	/**
	 * 默认的cross 启动上下文
	 */
	IStartupContext<CrossPlayerActor> DEFAULT_CROSS_START_CONTEXT = CrossPlayerActor::new;
	/**
	 * 默认的cross server node 启动上下文
	 */
	IStartupContext<ServerNode> DEFAULT_SERVER_NODE_START_CONTEXT = ServerNode::new;
	/**
	 * 默认对玩家的服务启动上下文
	 */
	IStartupContext<PlayerActor> SERVER_STARTUP_CONTEXT = new IStartupContext<>() {
		@Override
		public PlayerActor buildMessageActor(ISession session) {
			return new PlayerActor(session);
		}

		@Override
		public boolean userServerValidate(ISession session) {
			if (ServerNodeManager.isServerClosed()) {
				session.sendMessage(ServerCloseRsp.valueOf());
				return false;
			}
			return true;
		}
	};

	/**
	 * 构造MessageActor
	 * http情况不会调用.
	 * @param session
	 * @return
	 */
	T buildMessageActor(ISession session);

	/***
	 * 出现异常
	 * @param cause
	 * @return
	 */
	default ChannelFuture exception(ISession session, Throwable cause){
		IChannelData message;
		if (cause instanceof StatusResultException) {
			message = StatusTipsRsp.valueOf(((StatusResultException) cause));
		} else {
			if (cause instanceof KcpException || cause instanceof IOException) {
				LoggerType.DUODUO_FLASH_HANDLER.error("ChannelHandler异常: {}", cause.getMessage());
			}else {
				LoggerType.DUODUO_FLASH_HANDLER.error("ChannelHandler异常", cause);
			}
			message = SERVER_EXCEPTION_MESSAGE.get();
		}

		IMessageActor messageActor = session.getAttachObj(ServerConstants.MESSAGE_ACTOR_KEY);
		if (messageActor instanceof CrossPlayerActor) {
			// 在cross平台. 如果是玩家抛出的异常. 直接发送给客户端
			return messageActor.sendMessage(message);
		}
		return session.sendMessage(message);
	}

	/**
	 * 服务可用性校验
	 * 不可用. 触发 {@link ServerCloseRsp}
	 * @return true 可用 false 不可用
	 */
	default boolean userServerValidate(ISession session) { return true;}
}
