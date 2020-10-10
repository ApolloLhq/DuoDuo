package org.qiunet.flash.handler.context.request.data.pb;


/***
 * protobuf的响应接口
 *
 * @author qiunet
 * 2020-09-21 15:17
 */
public interface IpbResponseData extends IpbChannelData {
	/**
	 * 得到protocolId
	 * @return
	 */
	default int getProtocolId() {
		return PbResponseDataMapping.protocolId(getClass());
	}
}
