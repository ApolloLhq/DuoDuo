package org.qiunet.flash.handler.context.request.http;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.qiunet.flash.handler.common.annotation.SkipDebugOut;
import org.qiunet.flash.handler.common.message.MessageContent;
import org.qiunet.flash.handler.context.request.BaseRequestContext;
import org.qiunet.flash.handler.handler.http.IHttpHandler;
import org.qiunet.flash.handler.handler.http.ISyncHttpHandler;
import org.qiunet.flash.handler.handler.http.async.HttpAsyncTask;
import org.qiunet.flash.handler.handler.http.async.IAsyncHttpHandler;
import org.qiunet.flash.handler.netty.bytebuf.ByteBufFactory;
import org.qiunet.flash.handler.netty.server.param.HttpBootstrapParams;
import org.qiunet.flash.handler.util.ChannelUtil;
import org.qiunet.utils.exceptions.CustomException;
import org.qiunet.utils.string.StringUtil;
import org.qiunet.utils.string.ToString;

import java.util.*;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author qiunet
 *         Created on 17/3/17 14:28.
 */
abstract class AbstractHttpRequestContext<RequestData, ResponseData> extends BaseRequestContext<RequestData> implements IHttpRequestContext<RequestData, ResponseData> {
	private HttpRequest request;
	protected HttpBootstrapParams params;
	private QueryStringDecoder queryStringDecoder;

	public AbstractHttpRequestContext(MessageContent content, Channel channel, HttpBootstrapParams params, HttpRequest request)  {
		super(content, channel);
		this.request = request;
		this.params = params;
	}

	@Override
	public IHttpHandler<RequestData> getHandler() {
		return (IHttpHandler<RequestData>) handler;
	}

	private Map<String ,List<String>> parameters(){
		if (queryStringDecoder == null) queryStringDecoder = new QueryStringDecoder(request.uri());
		return queryStringDecoder.parameters();
	}
	@Override
	public List<String> getParametersByKey(String key) {
		return this.parameters().get(key);
	}

	@Override
	public void handlerRequest() throws Exception {
		if (logger.isInfoEnabled() && ! getRequestData().getClass().isAnnotationPresent(SkipDebugOut.class)) {
			logger.info("HTTP <<< {}", ToString.toString(getRequestData()));
		}

		IHttpHandler<RequestData> handler = getHandler();
		try {
			if ( handler.isAsync()) {
				this.processAsyncHttp((IAsyncHttpHandler<RequestData, ResponseData>) handler);
			}else {
				this.processSyncHttp(((ISyncHttpHandler<RequestData, ResponseData>) handler));
			}
		} catch (Exception e) {
			logger.error("HttpProtobufRequestContext Exception: ", e);
		}
	}

	@Override
	public String getParameter(String key) {
		List<String> ret = this.parameters().get(key);
		if (ret != null && !ret.isEmpty()) {
			return ret.get(0);
		}
		return null;
	}

	@Override
	public String getUriPath() {
		if (messageContent.isUriPathMsg()) {
			return messageContent.getUriPath();
		}
		return params.getGameURIPath();
	}

	@Override
	public boolean otherRequest() {
		return messageContent.isUriPathMsg();
	}

	@Override
	public String getRemoteAddress() {
		return ChannelUtil.getIp(request.headers(), channel);
	}

	@Override
	public HttpVersion getProtocolVersion() {
		return request.protocolVersion();
	}

	@Override
	public String getHttpHeader(String name) {
		return request.headers().get(name);
	}

	@Override
	public List<String> getHttpHeadersByName(String name) {
		return request.headers().getAll(name);
	}

	@Override
	public void response(ResponseData responseData) {
		if (responseData == null){
			throw new NullPointerException("ResponseData can not be null");
		}

		if (logger.isInfoEnabled() && ! responseData.getClass().isAnnotationPresent(SkipDebugOut.class)) {
			logger.info("HTTP >>> {}", ToString.toString(responseData));
		}

		boolean keepAlive = HttpUtil.isKeepAlive(request);
		byte [] data = getResponseDataBytes(responseData);
		// 不能使用pooled的对象. 因为不清楚什么时候release
		ByteBuf content;
		if (getUriPath().equals(params.getGameURIPath())) {
			// 不是游戏业务. 不写业务头.
			content = ChannelUtil.messageContentToByteBuf(new MessageContent(messageContent.getProtocolId(), data), channel);
		}else {
			content = ByteBufFactory.getInstance().alloc(data);
		}

		FullHttpResponse response = new DefaultFullHttpResponse(
				HTTP_1_1, OK,  content);
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"*");			//允许headers自定义
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"GET, POST");
//		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS,"true");	//响应报头指示的请求的响应是否可以暴露于该页面。当true值返回时它可以被暴露

		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType());
		if (keepAlive) {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}
		// 下面的 `writeAndFlush(Unpooled.EMPTY_BUFFER)` 会flush
		channel.writeAndFlush(response);

		if (! keepAlive) {
			channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}

	/***
	 * 得到contentType
	 * @return
	 */
	protected abstract String contentType();
	/***
	 * 得到responseData的数组数据
	 * @param responseData
	 * @return
	 */
	protected abstract byte[] getResponseDataBytes(ResponseData responseData);

	private Map<String, Cookie> cookieMap;
	private Map<String, Cookie> cookies(){
		if (cookieMap != null) return cookieMap;
		String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
		if (StringUtil.isEmpty(cookieString)) {
			cookieMap = Collections.emptyMap();
		}else {
			Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
			cookieMap = new HashMap<>();
			for (Cookie cookie : cookies) {
				cookieMap.put(cookie.name(), cookie);
			}
		}
		return cookieMap;
	}
	@Override
	public Set<Cookie> getCookieSet() {
		return new HashSet<>(cookies().values());
	}

	@Override
	public Cookie getCookieByName(String name) {
		return cookies().get(name);
	}

	/**
	 * 处理异步http
	 * @param handler
	 * @throws Exception
	 */
	private void processAsyncHttp(IAsyncHttpHandler<RequestData, ResponseData> handler) throws Exception {
		FacadeHttpRequest<RequestData, ResponseData> request = new FacadeHttpRequest<>(this);
		HttpAsyncTask<ResponseData> task = handler.handler(request);
		Preconditions.checkNotNull(task);
		task.onComplete((r, ex) -> {
			if (ex != null) {
				throw new CustomException(ex, "process async http exception");
			}
			if (r == null) {
				throw new NullPointerException("Response data can not be null!");
			}
			this.response(r);
		});
	}
	/**
	 * 处理同步http
	 * @param handler
	 * @throws Exception
	 */
	private void processSyncHttp(ISyncHttpHandler<RequestData, ResponseData> handler) throws Exception {
		FacadeHttpRequest<RequestData, ResponseData> request = new FacadeHttpRequest<>(this);
		ResponseData data = handler.handler(request);

		if (data == null) {
			throw new NullPointerException("Response data can not be null!");
		}
		this.response(data);
	}
}
