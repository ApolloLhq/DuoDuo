package org.qiunet.flash.handler.context.request.data;

import io.netty.buffer.ByteBuf;
import org.qiunet.flash.handler.common.annotation.SkipDebugOut;
import org.qiunet.flash.handler.common.protobuf.ProtobufDataManager;
import org.qiunet.flash.handler.context.response.push.DefaultProtobufMessage;
import org.qiunet.utils.string.ToString;

/***
 * requestData and responseData 的父类接口.
 *
 * @author qiunet
 * 2020-09-21 16:07
 */
public interface IChannelData {
	/**
	 * 转换为ByteBuf 最终记得要release
	 * @return
	 */
	default ByteBuf toByteBuf() {
		return ProtobufDataManager.encodeToByteBuf(this);
	}
	/**
	 * 转换为byte[]
	 * @return
	 */
	default byte[] toByteArray(){
		return ProtobufDataManager.encodeToByteArray(this);
	}
	/**
	 * 构造一个IResponseMessage
	 * @return
	 */
	default DefaultProtobufMessage buildChannelMessage(){
		return DefaultProtobufMessage.valueOf(protocolId(), this);
	}

	/**
	 * channel data to bytebuf 后.
	 * 会调用该方法. 如果需要回收的东西.
	 * 可以写这个里面
	 */
	default void recycle(){}
	/**
	 * 跳过交互输出, 输出内容为 {@link #_toString()}
	 * @return true 打印 false 跳过
	 */
	default boolean debugOut() {
		return ! getClass().isAnnotationPresent(SkipDebugOut.class);
	}

	/**
	 * 打印该对象
	 * @return 对象有效字符串.
	 */
	default String _toString(){
		return ToString.toString(this);
	}

	/**
	 * 得到protocolId
	 * @return
	 */
	default int protocolId() {
		return ChannelDataMapping.protocolId(getClass());
	}
}
