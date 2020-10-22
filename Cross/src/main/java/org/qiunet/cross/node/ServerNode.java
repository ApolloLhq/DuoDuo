package org.qiunet.cross.node;

import org.qiunet.flash.handler.common.IMessage;
import org.qiunet.flash.handler.common.message.MessageContent;
import org.qiunet.flash.handler.common.player.AbstractMessageActor;
import org.qiunet.flash.handler.common.player.IMessageActor;
import org.qiunet.flash.handler.context.request.data.pb.IpbChannelData;
import org.qiunet.flash.handler.context.session.DSession;
import org.qiunet.flash.handler.handler.IHandler;
import org.qiunet.flash.handler.handler.mapping.RequestHandlerMapping;
import org.qiunet.flash.handler.netty.client.param.TcpClientParams;
import org.qiunet.flash.handler.netty.client.tcp.NettyTcpClient;
import org.qiunet.flash.handler.netty.server.constants.ServerConstants;
import org.qiunet.utils.string.StringUtil;

/***
 * 单独启动tcp连接, 提供其它服务公用的一个actor
 * 一个服务与一个服务之间只会有一个 连接.不会存在多个.
 *
 * @author qiunet
 * 2020-10-09 11:13
 */
public class ServerNode extends AbstractMessageActor<ServerNode> {


	private int serverId;

	public ServerNode(DSession session) {
		super(session);
	}


	static ServerNode valueOf(ServerInfo serverInfo) {
		NettyTcpClient tcpClient = new NettyTcpClient(TcpClientParams.custom()
			.setAddress(serverInfo.getHost(), serverInfo.getPort())
			.build(), ServerNode::handlerMessage);

		ServerNode node = new ServerNode(tcpClient.getSession());
		node.serverId = serverInfo.getServerId();

		node.send(ServerNodeAuthRequest.valueOf(ServerNodeManager.getCurrServerId()).buildResponseMessage());
		return node;

	}

	private static void handlerMessage(DSession session, MessageContent data) {
		IMessageActor messageActor = session.getAttachObj(ServerConstants.MESSAGE_ACTOR_KEY);
		IHandler handler = RequestHandlerMapping.getInstance().getHandler(data);
		IMessage message = handler.getHandlerType().createRequestContext(data, session.channel(), handler, messageActor);
		messageActor.addMessage(message);
	}


	@Override
	public void addMessage(IMessage<ServerNode> msg) {
		// 是一个服务和另一个服务公用一个channel.
		// 由业务自己实现线程的安全. 一般CommMessageHandler  roomHandler等 重新addMessage 一遍.
		this.runMessage(msg);
	}

	/**
	 * 必须设置 serverId
	 *
	 * @param serverId
	 */
	public void auth(int serverId) {
		this.serverId = serverId;
		ServerNodeManager0.instance.addNode(this);
	}

	/**
	 * 获得serverId
	 * @return
	 */
	public int getServerId() {
		return serverId;
	}

	/**
	 * 向服务器发起一个请求
	 * @param message
	 */
	public void writeMessage(IpbChannelData message) {
		this.send(message.buildResponseMessage());
	}

	@Override
	protected String getIdent() {
		return StringUtil.format( "ServerNode[{0}]", serverId);
	}

	@Override
	public long getId() {
		return getServerId();
	}

}
