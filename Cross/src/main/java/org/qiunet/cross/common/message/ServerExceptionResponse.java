package org.qiunet.cross.common.message;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import org.qiunet.flash.handler.context.request.data.pb.IpbResponseData;
import org.qiunet.flash.handler.context.request.data.pb.PbResponse;

/***
 *
 *
 * @author qiunet
 * 2020-09-25 16:59
 */
@PbResponse(500)
@ProtobufClass(description = "服务器异常")
public class ServerExceptionResponse implements IpbResponseData {
}
