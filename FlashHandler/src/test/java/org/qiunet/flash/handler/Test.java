package org.qiunet.flash.handler;

import org.qiunet.flash.handler.bootstrap.error.DefaultErrorMessage;
import org.qiunet.flash.handler.bootstrap.hook.MyHook;
import org.qiunet.flash.handler.interceptor.DefaultUdpInterceptor;
import org.qiunet.flash.handler.netty.server.BootstrapServer;
import org.qiunet.flash.handler.netty.server.hook.Hook;
import org.qiunet.flash.handler.netty.server.param.UdpBootstrapParams;

/**
 * 用来debug 跟踪内部测试用
 * Created by qiunet.
 * 17/12/15
 */
public class Test {
	private static Hook hook = new MyHook();
	public static void main(String[] args) {
		BootstrapServer.createBootstrap(hook).udpListener(
				UdpBootstrapParams.custom()
						.setUdpInterceptor(new DefaultUdpInterceptor())
						.setErrorMessage(new DefaultErrorMessage())
						.setPort(8888)
						.setCrc(true)
					.build()
		).await();
	}
}
