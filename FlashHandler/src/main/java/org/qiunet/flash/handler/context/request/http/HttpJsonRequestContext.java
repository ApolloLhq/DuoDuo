package org.qiunet.flash.handler.context.request.http;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import org.qiunet.flash.handler.common.message.MessageContent;
import org.qiunet.flash.handler.context.request.http.json.IResultResponse;
import org.qiunet.flash.handler.context.response.push.DefaultBytesMessage;
import org.qiunet.flash.handler.netty.server.config.ServerBootStrapConfig;
import org.qiunet.utils.json.JsonUtil;

/**
 * 把请求解析为json对象
 * Created by qiunet.
 * 17/11/21
 */
public class HttpJsonRequestContext<RequestData, ResponseData extends IResultResponse> extends AbstractHttpRequestContext<RequestData, ResponseData> {

	public HttpJsonRequestContext(MessageContent content, Channel channel, ServerBootStrapConfig config, HttpRequest request) {
		this.init(content, channel, config, request);
	}

	public void init(MessageContent content, Channel channel, ServerBootStrapConfig config, HttpRequest request) {
		super.init(content, channel, config, request);
	}

	@Override
	protected DefaultBytesMessage getResponseDataMessage(ResponseData data) {
		return new DefaultBytesMessage(getHandler().getProtocolID(), JsonUtil.toJsonString(data).getBytes(CharsetUtil.UTF_8));
	}

	@Override
	protected String contentType() {
		return "application/json; charset=UTF-8";
	}

}
