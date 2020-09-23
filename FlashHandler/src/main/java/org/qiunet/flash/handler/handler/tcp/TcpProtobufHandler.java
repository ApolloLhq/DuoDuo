package org.qiunet.flash.handler.handler.tcp;

import com.baidu.bjf.remoting.protobuf.Codec;
import org.qiunet.flash.handler.common.enums.DataType;
import org.qiunet.flash.handler.common.player.IPlayerActor;
import org.qiunet.flash.handler.context.request.data.pb.IpbRequestData;
import org.qiunet.utils.async.LazyLoader;
import org.qiunet.utils.protobuf.ProtobufDataManager;

import java.io.IOException;

/**
 * Created by qiunet.
 * 17/7/21
 */
public abstract class TcpProtobufHandler<P extends IPlayerActor, RequestData extends IpbRequestData> extends BaseTcpHandler<P, RequestData> {
	private LazyLoader<Codec<RequestData>> codec = new LazyLoader<>(() -> ProtobufDataManager.getCodec(getRequestClass()));

	@Override
	public RequestData parseRequestData(byte[] bytes){
		try {
			return codec.get().decode(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public DataType getDataType() {
		return DataType.PROTOBUF;
	}
}
