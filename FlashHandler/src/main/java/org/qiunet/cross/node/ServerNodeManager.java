package org.qiunet.cross.node;

import org.qiunet.data.util.ServerType;

import java.util.List;
import java.util.Map;

/***
 *
 *
 * @author qiunet
 * 2020-10-21 17:21
 */
public class ServerNodeManager {
	/**
	 * 获得当前的serverId
	 * @return
	 */
	public static int getCurrServerId(){
		return getCurrServerInfo().getServerId();
	}

	/**
	 * 得到当前的ServerInfo
	 * @return
	 */
	public static ServerInfo getCurrServerInfo() {
		return ServerNodeManager0.instance.getCurrServerInfo();
	}
	/**
	 * 获得当前的serverType
	 * @return
	 */
	public static ServerType getCurrServerType(){
		return getCurrServerInfo().getServerType();
	}

	/**
	 * 获得serverInfo
	 * @param serverId
	 * @return
	 */
	public static ServerInfo getServerInfo(int serverId) {
		return ServerNodeManager0.instance.getServerInfo(serverId);
	}

	/**
	 * 获得指定type里面的指定id的serverInfo
	 * @param serverType  服务器类型
	 * @param groupId 服务器组id
	 * @return
	 */
	public static List<ServerInfo> getServerInfos(ServerType serverType, int groupId) {
		return ServerNodeManager0.instance.getServerInfos(serverType, groupId);
	}
	/**
	 * 获得指定type里面的指定组id的 数量
	 * @param serverType 服务器类型
	 * @param groupId 服务器组id
	 * @return
	 */
	public static long getServerCount(ServerType serverType, int groupId) {
		return ServerNodeManager0.instance.getServerCount(serverType, groupId);
	}
	/**
	 * 获得serverNode
	 * 是没有办法获得ServerInfo的情况.
	 * @param serverId
	 * @return
	 */
	public static ServerNode getNode(int serverId, String host, int port) {
		return ServerNodeManager0.instance.getNode(serverId, host, port);
	}
	/**
	 * 获得一个serverNode
	 * @param serverId
	 * @return
	 */
	public static ServerNode getNode(int serverId) {
		return ServerNodeManager0.instance.getNode(serverId);
	}

	/**
	 * 每个组在线的人数
	 * @return
	 */
	public static Map<Integer, Integer> groupOnlineUserCounts(){
		return ServerNodeManager0.instance.groupOnlineUserCounts();
	}
}
