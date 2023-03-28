package org.qiunet.cross.actor.message;

import org.qiunet.flash.handler.context.response.push.IChannelMessage;

/***
 * Cross 回传必要信息接口
 *
 * @author qiunet
 * 2023/3/28 17:12
 */
public interface IBroadcastNecessaryInfo<T> extends IChannelMessage<T> {
	/**
	 * 是否是需要flush消息
	 * @return
	 */
	boolean isFlush();

	/**
	 * 是否是kcp消息
	 * @return
	 */
	boolean isKcp();
}
