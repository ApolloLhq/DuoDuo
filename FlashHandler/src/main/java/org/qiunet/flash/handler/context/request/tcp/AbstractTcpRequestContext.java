package org.qiunet.flash.handler.context.request.tcp;

import io.netty.channel.Channel;
import org.qiunet.flash.handler.common.message.MessageContent;
import org.qiunet.flash.handler.common.player.IMessageActor;
import org.qiunet.flash.handler.context.request.BaseRequestContext;

import java.net.InetSocketAddress;

/**
 * tcp udp的上下文
 * Created by qiunet.
 * 17/7/19
 */
abstract class AbstractTcpRequestContext<RequestData, P extends IMessageActor> extends BaseRequestContext<RequestData> implements ITcpRequestContext<RequestData, P> {
	protected P messageActor;
	protected AbstractTcpRequestContext(MessageContent content, Channel channel, P messageActor) {
		super(content, channel);
		this.messageActor = messageActor;
	}

	@Override
	public String getRemoteAddress() {
		String ip = "";
		if (channel.remoteAddress() instanceof InetSocketAddress) {
			ip = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
		}
		return ip;
	}

	@Override
	public Channel channel() {
		return channel;
	}
}
