package org.qiunet.flash.handler.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.qiunet.flash.handler.common.message.MessageContent;
import org.qiunet.flash.handler.context.header.IProtocolHeader;
import org.qiunet.flash.handler.util.ChannelUtil;
import org.qiunet.utils.logger.LoggerType;
import org.slf4j.Logger;

import java.util.List;

/**
 * 消息的解析
 * Created by qiunet.
 * 17/8/13
 */
public class TcpSocketServerDecoder extends ByteToMessageDecoder implements IMessageDecoder {
	private final Logger logger = LoggerType.DUODUO_FLASH_HANDLER.getLogger();
	private final int maxReceivedLength;
	private final boolean encryption;
	public TcpSocketServerDecoder(int maxReceivedLength, boolean encryption) {
		this.encryption = encryption;
		this.maxReceivedLength = maxReceivedLength;
	}
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (! ctx.channel().isActive()) {
			return;
		}

		IProtocolHeader protocolHeader = ChannelUtil.getProtocolHeader(ctx.channel());
		boolean connectReq = ChannelUtil.isConnectReq(ctx.channel());
		if (! in.isReadable(getHeaderLength(protocolHeader, connectReq))) return;
		in.markReaderIndex();

		// header里面的问题 不打印错误信息了. 仅仅本地调试时候打印
		IProtocolHeader.ProtocolHeader header = getHeader(protocolHeader, in, ctx.channel(), connectReq);
		if (header.getLength() < 0 || header.getLength() > maxReceivedLength) {
			logger.error("Invalid message, length is error! length is : {}",  header.getLength());
			ctx.channel().close();
			return;
		}

		if (! in.isReadable(header.getLength())) {
			in.resetReaderIndex();
			return;
		}

		if (! header.isValidMessage()) {
			logger.error("Invalid message, message is error! {}", header);
			ctx.channel().close();
			return;
		}

		MessageContent content = MessageContent.valueOf(header, in.readRetainedSlice(header.getLength()));
		if (encryption && ! header.validEncryption(content.byteBuffer())) {
			ctx.channel().close();
			content.release();
			return;
		}

		out.add(content);
	}
}
