package org.qiunet.flash.handler.context.request.persistconn;


import io.netty.channel.Channel;
import org.qiunet.flash.handler.common.player.IMessageActor;
import org.qiunet.utils.pool.ThreadScopeObjectPool;

/**
 * Created by qiunet.
 * 17/12/2
 */
class FacadePersistConnRequest<RequestData, P extends IMessageActor<P>> implements IPersistConnRequest<RequestData> {
	private static final ThreadScopeObjectPool<FacadePersistConnRequest> POOL = new ThreadScopeObjectPool<>(FacadePersistConnRequest::new);

	private FacadePersistConnRequest() {}

	private AbstractPersistConnRequestContext<RequestData, P> context;
	static <RequestData, P extends IMessageActor<P>> FacadePersistConnRequest<RequestData, P> valueOf(AbstractPersistConnRequestContext<RequestData, P> context) {
		FacadePersistConnRequest request = POOL.get();
		request.context = context;
		return request;
	}

	void recycle(){
		this.context = null;
		POOL.recycle(this);
	}
	@Override
	public RequestData getRequestData() {
		return context.getRequestData();
	}

	@Override
	public String getRemoteAddress() {
		return context.getRemoteAddress();
	}

	@Override
	public Object getAttribute(String key) {
		return context.getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object val) {
		context.setAttribute(key, val);
	}

	@Override
	public Channel channel() {
		return context.channel();
	}

}
