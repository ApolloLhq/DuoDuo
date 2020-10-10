package org.qiunet.flash.handler.netty.server.param.adapter;

import org.qiunet.flash.handler.common.player.IMessageActor;
import org.qiunet.flash.handler.context.response.push.DefaultProtobufMessage;
import org.qiunet.flash.handler.context.session.DSession;

/***
 *
 * 长连接情况下, 需要根据给定参数给出业务自己的session 和playerActor的实例对象
 *
 * @author qiunet
 * 2020/3/8 09:31
 **/
public interface IStartupContext<T extends IMessageActor<T>> {
	/**
	 * 构造MessageActor
	 * http情况不会调用.
	 * @param session
	 * @return
	 */
	T buildMessageActor(DSession session);
	/***
	 *  没有找到handler 404
	 * @return
	 */
	DefaultProtobufMessage getHandlerNotFound();

	/***
	 * 出现异常
	 * @param cause
	 * @return
	 */
	DefaultProtobufMessage exception(Throwable cause);
}
