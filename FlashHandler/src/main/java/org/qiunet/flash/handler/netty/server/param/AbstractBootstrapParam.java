package org.qiunet.flash.handler.netty.server.param;

import org.qiunet.flash.handler.common.player.IMessageActor;
import org.qiunet.flash.handler.context.header.IProtocolHeaderType;
import org.qiunet.flash.handler.netty.server.param.adapter.IStartupContext;

/**
 * Created by qiunet.
 * 17/11/22
 */
public abstract class AbstractBootstrapParam {
	/**
	 * 可以自定义协议头
	 */
	protected IProtocolHeaderType protocolHeaderType;
	/***
	 * 接收端口
	 */
	protected int port;
	/**
	 * 服务名
	 */
	protected String serverName;
	/***
	 * 客户端每5秒一次心跳
	 * 读超时处理.
	 */
	protected int readIdleCheckSeconds = 60 * 10;
	/**
	 * 是否检验crc
	 * 一般测试时候使用
	 */
	protected boolean encryption = true;
	/**
	 * 最大接受的数据长度
	 */
	protected int maxReceivedLength = 1024 * 1024;

	protected IStartupContext<? extends IMessageActor<?>> startupContext;

	public IProtocolHeaderType getProtocolHeaderType() {
		return protocolHeaderType;
	}

	public int getMaxReceivedLength() {
		return maxReceivedLength;
	}

	public int getPort() {
		return port;
	}

	public String getServerName() {
		return serverName;
	}

	public boolean isEncryption() {
		return encryption;
	}

	public int getReadIdleCheckSeconds() {
		return readIdleCheckSeconds;
	}

	public IStartupContext<? extends IMessageActor<?>> getStartupContext() {
		return startupContext;
	}

	/***
	 * 使用build模式 set和 get 分离. 以后有有顺序的构造时候也可以不动
	 * */
	public abstract class SuperBuilder<P extends AbstractBootstrapParam, B extends SuperBuilder<P, B>> {
		/***
		 * 启动需要的上下文对象
		 * @param protocolHeaderType
		 * @return
		 */
		public B setProtocolHeaderType(IProtocolHeaderType protocolHeaderType) {
			AbstractBootstrapParam.this.protocolHeaderType = protocolHeaderType;
			return (B) this;
		}

		public B setStartupContext(IStartupContext<? extends IMessageActor<?>> startupContext) {
			AbstractBootstrapParam.this.startupContext = startupContext;
			return (B) this;
		}

		public B setMaxReceivedLength(int maxReceivedLength) {
			AbstractBootstrapParam.this.maxReceivedLength = maxReceivedLength;
			return (B) this;
		}

		public B setEncryption(boolean encryption) {
			AbstractBootstrapParam.this.encryption = encryption;
			return (B) this;
		}
		public B setPort(int port) {
			AbstractBootstrapParam.this.port = port;
			return (B)this;
		}

		public B setReadIdleCheckSeconds(int readIdleCheckSeconds) {
			AbstractBootstrapParam.this.readIdleCheckSeconds = readIdleCheckSeconds;
			return (B)this;
		}

		public B setServerName(String serverName) {
			AbstractBootstrapParam.this.serverName = serverName;
			return (B)this;
		}

		/**
		 * 构造build
		 * @return
		 */
		public P build(){
			if (port == 0) throw new NullPointerException("Must set port for Listener! ");
			return newParams();
		}
		/**
		 * 得到一个新的param
		 * @return
		 */
		protected abstract P newParams();
	}
}
