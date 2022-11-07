package org.qiunet.flash.handler.context.response.push;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.qiunet.flash.handler.common.annotation.SkipDebugOut;
import org.qiunet.flash.handler.common.player.IMessageActor;
import org.qiunet.flash.handler.common.player.IRobot;
import org.qiunet.flash.handler.context.header.IProtocolHeader;
import org.qiunet.flash.handler.context.header.IProtocolHeaderType;
import org.qiunet.flash.handler.netty.server.constants.ServerConstants;
import org.qiunet.flash.handler.util.ChannelUtil;
import org.qiunet.utils.logger.LoggerType;
import org.qiunet.utils.string.IDataToString;
import org.qiunet.utils.string.ToString;
import org.slf4j.Logger;

import java.nio.ByteBuffer;

/**
 * 对外响应的编码消息
 * @author qiunet.
 * 17/12/11
 */
public interface IChannelMessage<T> {
	Logger logger = LoggerType.DUODUO_FLASH_HANDLER.getLogger();
	/***
	 * 得到消息的内容
	 * @return
	 */
	T getContent();

	/***
	 * 转换bytes
	 * @return
	 */
	ByteBuffer byteBuffer();
	/**
	 * 得到消息id
	 * @return
	 */
	int getProtocolID();

	/**
	 * 回收
	 */
	default void recycle(){}
	/**
	 * 是否需要打印输出
	 * @return
	 */
	default boolean needLogger(){
		return ! this.getContent().getClass().isAnnotationPresent(SkipDebugOut.class);
	}

	/**
	 * 转logger 格式字符串
	 * @return
	 */
	default String toStr() {
		T content = this.getContent();
		if (IDataToString.class.isAssignableFrom(content.getClass())) {
			return ((IDataToString) content)._toString();
		}
		return ToString.toString(content);
	}

	/**
	 * 获得 添加 protocol header 的 ByteBuf
	 * @param channel
	 * @return
	 */
	default ByteBuf withHeaderByteBuf(Channel channel) {
		IProtocolHeaderType adapter = ChannelUtil.getProtocolHeaderAdapter(channel);
		IProtocolHeader header = adapter.outHeader(this.getProtocolID(), this);

		ByteBuf byteBuf = Unpooled.wrappedBuffer(header.headerByteBuf(), Unpooled.wrappedBuffer((ByteBuffer) this.byteBuffer().rewind()));

		this.loggerChannelMessage(channel);

		header.recycle();
		this.recycle();
		return byteBuf;
	}

	/**
	 * 打印消息
	 * @param channel
	 */
	default void loggerChannelMessage(Channel channel) {
		if (logger.isInfoEnabled()) {
			IMessageActor<?> messageActor = channel.attr(ServerConstants.MESSAGE_ACTOR_KEY).get();
			if (messageActor != null && ( this.needLogger() || messageActor instanceof IRobot)) {
				logger.info("[{}] [{}({})] >>> {}", messageActor.getIdentity(), channel.attr(ServerConstants.HANDLER_TYPE_KEY).get(), channel.id().asShortText(), this.toStr());
			}
		}
	}
	/**
	 * 获得不添加 protocol header 的ByteBuf
	 * @return
	 */
	default ByteBuf withoutHeaderByteBuf() {
		this.recycle();
		return Unpooled.wrappedBuffer(byteBuffer());
	}
}
