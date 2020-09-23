package org.qiunet.test.testcase.LongConn;

import org.qiunet.flash.handler.common.message.MessageContent;
import org.qiunet.flash.handler.context.request.data.pb.IpbRequestData;
import org.qiunet.test.robot.IRobot;

/**
 * Created by qiunet.
 * 17/12/8
 */
public abstract class LongConnProtobufTestCase<RequestData extends IpbRequestData, Robot extends IRobot> extends BaseLongConnTestCase<Robot> {
	@Override
	public MessageContent buildRequest(Robot robot) {
		RequestData requestData = requestBuild(robot);
		MessageContent content = new MessageContent(getRequestID(), requestData.toByteArray());
		return content;
	}

	protected abstract RequestData requestBuild(Robot robot);
}
