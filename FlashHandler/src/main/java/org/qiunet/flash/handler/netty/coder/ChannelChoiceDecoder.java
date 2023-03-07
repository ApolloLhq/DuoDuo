package org.qiunet.flash.handler.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.ScheduledFuture;
import org.qiunet.flash.handler.context.header.IProtocolHeader;
import org.qiunet.flash.handler.netty.handler.FlushBalanceHandler;
import org.qiunet.flash.handler.netty.server.http.handler.HttpServerHandler;
import org.qiunet.flash.handler.netty.server.idle.NettyIdleCheckHandler;
import org.qiunet.flash.handler.netty.server.param.ServerBootStrapParam;
import org.qiunet.flash.handler.netty.server.tcp.handler.TcpServerHandler;
import org.qiunet.utils.logger.LoggerType;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 消息的解析
 * Created by qiunet.
 * 17/8/13
 */
public class ChannelChoiceDecoder extends ByteToMessageDecoder {
	private static final Logger logger = LoggerType.DUODUO_FLASH_HANDLER.getLogger();
	private final ServerBootStrapParam param;

	private static final byte[] POST_BYTES = {'P', 'O', 'S', 'T'};
	private static final byte[] GET_BYTES = {'G', 'E', 'T', ' '};
	private static final byte[] HEAD_BYTES = {'H', 'E', 'A', 'D'};
	private ScheduledFuture<?> closeFuture;

	private ChannelChoiceDecoder(ServerBootStrapParam param) {
		this.param = param;
	}

	public static ChannelChoiceDecoder valueOf(ServerBootStrapParam param){
		return new ChannelChoiceDecoder(param);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.closeFuture = ctx.channel().eventLoop().schedule(() -> {
			// 关闭那些只连接. 不发送任何协议的客户端
			ctx.channel().close();
		}, 20, TimeUnit.SECONDS);
		super.channelRegistered(ctx);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		ChannelPipeline pipeline = ctx.channel().pipeline();

		if (param.isBanHttpServer() || this.equals(IProtocolHeader.MAGIC_CONTENTS, in)) {
			pipeline.addLast("TcpSocketEncoder", new TcpSocketEncoder());
			pipeline.addLast("TcpSocketDecoder", new TcpSocketDecoder(param.getMaxReceivedLength(), param.isEncryption()));
			pipeline.addLast("IdleStateHandler", new IdleStateHandler(param.getReadIdleCheckSeconds(), 0, 0));
			pipeline.addLast("NettyIdleCheckHandler", new NettyIdleCheckHandler());
			pipeline.addLast("TcpServerHandler", new TcpServerHandler(param));
			pipeline.addLast("FlushBalanceHandler", new FlushBalanceHandler());
			pipeline.remove(ChannelChoiceDecoder.class);
			ctx.fireChannelActive();
		}else if (this.equals(POST_BYTES, in) || this.equals(GET_BYTES, in) || this.equals(HEAD_BYTES, in)){
			pipeline.addLast("HttpServerCodec" ,new HttpServerCodec());
			pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(param.getMaxReceivedLength()));
			pipeline.addLast("HttpServerHandler", new HttpServerHandler(param));
			pipeline.remove(ChannelChoiceDecoder.class);
		}else {
			logger.debug("Invalidate connection!");
			ctx.close();
		}

		closeFuture.cancel(true);
	}


	/**
	 * 对比数组. 只要符合origin即可
	 * @param origin
	 * @param in
	 * @return
	 */
	private boolean equals(byte [] origin, ByteBuf in) {
		if (in.readableBytes() < origin.length) {
			return false;
		}

		for (int i = 0; i < origin.length; i++) {
			if (in.getByte(i) != origin[i]) {
				return false;
			}
		}
		return true;
	}
}
