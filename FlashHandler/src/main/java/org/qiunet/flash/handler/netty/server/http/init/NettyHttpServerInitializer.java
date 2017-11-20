package org.qiunet.flash.handler.netty.server.http.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import org.qiunet.flash.handler.netty.server.http.handler.HttpServerHandler;
import org.qiunet.flash.handler.param.HttpBootstrapParams;

/**
 * Created by qiunet.
 * 17/11/11
 */
public class NettyHttpServerInitializer extends ChannelInitializer<SocketChannel> {
	private final SslContext sslCtx;
	private HttpBootstrapParams params;
	public NettyHttpServerInitializer(SslContext sslCtx, HttpBootstrapParams params) {
		this.sslCtx = sslCtx;
		this.params = params;
	}
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		p.addLast(new HttpRequestDecoder());
		// Uncomment the following line if you don't want to handle HttpChunks.
//        p.addLast(new HttpObjectAggregator(1048576));
		p.addLast(new HttpResponseEncoder());
		// Remove the following line if you don't want automatic content compression.
//		p.addLast(new HttpContentCompressor());

		p.addLast(new HttpServerHandler(params));
	}
}
