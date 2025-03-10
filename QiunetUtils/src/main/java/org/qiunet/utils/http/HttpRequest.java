package org.qiunet.utils.http;

import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import okhttp3.*;
import org.qiunet.utils.exceptions.CustomException;
import org.qiunet.utils.logger.LoggerType;
import org.qiunet.utils.prometheus.PrometheusRegistry;
import org.qiunet.utils.thread.ThreadPoolManager;
import org.slf4j.Logger;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/***
 *
 *
 * @author qiunet
 * 2020-04-20 17:39
 ***/
public abstract class HttpRequest<B extends HttpRequest> {
	protected static final Logger logger = LoggerType.DUODUO_HTTP.getLogger();
	protected static final OkHttpClient client = new OkHttpClient.Builder()
		.connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
		.eventListener(
			OkHttpMetricsEventListener.builder(PrometheusRegistry.registry(), "okhttp.requests")
					.uriMapper(request -> request.url().encodedPath())
					.build()
		).dispatcher(new Dispatcher(ThreadPoolManager.NORMAL))
		.build();

	protected String url;

	protected Charset charset = StandardCharsets.UTF_8;

	protected HttpRequest(String url) {
		this.url = url;
	}

	protected Headers.Builder headerBuilder = new Headers.Builder()
		.add("Accept-Charset", "UTF-8");

	/**
	 * 返回client
	 * @return
	 */
	public static OkHttpClient _client() {
		return client;
	}

	public static PostHttpRequest post(String url) {
		return new PostHttpRequest(url);
	}

	public static GetHttpRequest get(String url) {
		return new GetHttpRequest(url);
	}

	public B charset(Charset charset) {
		this.header("Accept-Charset", charset.toString());
		this.charset = charset;
		return (B) this;
	}

	public B header(String name, String val) {
		this.headerBuilder.add(name, val);
		return (B) this;
	}

	/**
	 * 每次请求关闭 connect
	 * @return
	 */
	public B closeConnectAlive() {
		this.header("Connection", "close");
		return (B) this;
	}

	public B header(Map<String, String> headerMap) {
		headerMap.forEach((key, val) -> this.headerBuilder.add(key ,val));
		return (B) this;
	}

	/**
	 * 异步执行请求
	 * @param callBack
	 */
	public void asyncExecutor(IHttpCallBack callBack) {
		Request request = buildRequest();
		client.newCall(request).enqueue(callBack);
	}
	/**
	 * 执行请求
	 * @return
	 */
	public <T> T executor(IResultSupplier<T> supplier) {
		Request request = buildRequest();
		try {
			Response response = client.newCall(request).execute();
			if (! response.isSuccessful()) {
				throw new CustomException("Request: {} Fail, StatusCode {}", request, response.code());
			}
			return supplier.result(response);
		} catch (Exception e) {
			throw new CustomException(e, "http client send request error!");
		}
	}
	/**
	 * 执行请求
	 * @return
	 */
	public String executor() {
		return executor(IResultSupplier.STRING_SUPPLIER);
	}

	protected abstract Request buildRequest();
}
