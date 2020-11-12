package org.qiunet.flash.handler.context.request;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import org.qiunet.flash.handler.common.message.MessageContent;
import org.qiunet.flash.handler.handler.IHandler;
import org.qiunet.flash.handler.handler.mapping.RequestHandlerMapping;
import org.qiunet.utils.logger.LoggerType;
import org.slf4j.Logger;

import java.util.Map;

/**
 * Created by qiunet.
 * 17/11/20
 */
public abstract class BaseRequestContext<RequestData> implements IRequestContext<RequestData> {
	protected Logger logger = LoggerType.DUODUO_FLASH_HANDLER.getLogger();

	protected Channel channel;
	protected MessageContent messageContent;
	protected IHandler<RequestData> handler;
	private Map<String , Object> attributes;
	// 请求数据. 必须在这里获取. 然后release MessageContent
	private RequestData requestData;

	protected BaseRequestContext(MessageContent content, Channel channel) {
		this.channel = channel;
		this.messageContent = content;
		this.handler = RequestHandlerMapping.getInstance().getHandler(content);
		try {
			this.requestData = getHandler().parseRequestData(messageContent.byteBuffer());
		}finally {
			messageContent.release();
		}
	}

	@Override
	public RequestData getRequestData() {
		return requestData;
	}

	@Override
	public IHandler<RequestData> getHandler() {
		return handler;
	}

	@Override
	public Object getAttribute(String key) {
		return attributes == null ? null : attributes.get(key);
	}

	@Override
	public void setAttribute(String key, Object val) {
		if (attributes == null) {
			attributes = Maps.newConcurrentMap();
		}
		attributes.put(key, val);
	}
}
