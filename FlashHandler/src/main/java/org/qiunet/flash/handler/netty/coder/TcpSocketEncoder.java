package org.qiunet.flash.handler.netty.coder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.qiunet.flash.handler.context.response.push.IChannelMessage;
import org.qiunet.flash.handler.util.ChannelUtil;

import java.util.List;

/**
 * Created by qiunet.
 * 17/8/13
 */
public class TcpSocketEncoder extends MessageToMessageEncoder<IChannelMessage<?>> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IChannelMessage<?> msg, List<Object> out) throws Exception {
		if (! ctx.channel().isActive()) {
			return;
		}

		out.add(ChannelUtil.messageContentToByteBuf(msg, ctx.channel()));
	}
}
