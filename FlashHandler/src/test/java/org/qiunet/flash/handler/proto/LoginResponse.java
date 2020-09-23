package org.qiunet.flash.handler.proto;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import org.qiunet.flash.handler.common.annotation.SkipDebugOut;
import org.qiunet.flash.handler.context.request.data.pb.IpbResponseData;
import org.qiunet.flash.handler.context.request.data.pb.PbResponse;

/***
 *
 *
 * @author qiunet
 * 2020-09-22 12:31
 */
@SkipDebugOut
@ProtobufClass
@PbResponse(1000001)
public class LoginResponse implements IpbResponseData {

	private String testString;

	public static LoginResponse valueOf(String testString) {
		LoginResponse response = new LoginResponse();
		response.testString = testString;
		return response;
	}

	public String getTestString() {
		return testString;
	}
}
