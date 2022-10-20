package org.qiunet.flash.handler.netty.server.http.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.qiunet.flash.handler.netty.server.param.ServerBootStrapParam;

/**
 * Created by qiunet.
 * 17/11/11
 */
public class NettyHttpServerInitializer extends ChannelInitializer<SocketChannel> {
	private final ServerBootStrapParam param;
	public NettyHttpServerInitializer(ServerBootStrapParam param) {
		this.param = param;
	}
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();

	}
}
