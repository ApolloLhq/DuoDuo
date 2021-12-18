package org.qiunet.flash.handler.context.request.http;

import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import org.qiunet.flash.handler.context.request.IRequest;

import java.util.List;
import java.util.Set;

/**
 * Created by qiunet.
 * 17/11/21
 */
public interface IHttpRequest<RequestData> extends IRequest<RequestData> {
	/***
	 * 是外部的请求. 内部使用的protocolId区别
	 * 外部请求使用uri
	 * @return
	 */
	boolean otherRequest();
	/***
	 * 得到UriPath
	 * @return
	 */
	String getUriPath();
	/***
	 * 得到get请求的参数
	 * @param key
	 * @return
	 */
	default String getParameter(String key) {
		List<String> ret = this.getParametersByKey(key);
		if (ret != null && !ret.isEmpty()) {
			return ret.get(0);
		}
		return null;
	}

	/**
	 * 得到get请求的参数. 返回是一个数组
	 * @param key
	 * @return
	 */
	List<String> getParametersByKey(String key);

	/**
	 * 得到指定header 的值
	 * @param name
	 * @return
	 */
	String getHttpHeader(String name);
	/**
	 * 得到指定header 的值
	 * @param name
	 * @return
	 */
	List<String> getHttpHeadersByName(String name);
	/***
	 * 得到协议使用的http版本
	 * @return
	 */
	HttpVersion getProtocolVersion();

	/**
	 * 得到所有的cookie
	 * @return 没有返回一个empty set
	 */
	Set<Cookie> getCookieSet();

	/**
	 * 得到一个cookie
	 * @param name cookie的名称
	 * @return 没有 返回null
	 */
	Cookie getCookieByName(String name);
}
