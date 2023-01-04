package org.qiunet.flash.handler.netty.server.hook;

import java.util.function.Consumer;

/***
 *  默认的hook实现
 *
 * @author qiunet
 * 2021/11/20 23:43
 */
public class DefaultHook implements Hook {

	private Consumer<String> cmdConsumer;

	public DefaultHook() {
		this(null);
	}

	public DefaultHook(Consumer<String> cmdConsumer) {
		this.cmdConsumer = cmdConsumer;
	}

	@Override
	public void custom(String msg) {
		if (this.cmdConsumer != null) {
			this.cmdConsumer.accept(msg);
		}
	}
}
