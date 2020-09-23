package org.qiunet.tests.server.handler.player;

import org.qiunet.flash.handler.common.annotation.RequestHandler;
import org.qiunet.flash.handler.context.request.http.IHttpRequest;
import org.qiunet.flash.handler.context.request.http.json.JsonRequest;
import org.qiunet.flash.handler.context.response.json.JsonResponse;
import org.qiunet.flash.handler.handler.http.HttpJsonHandler;

/**
 * Created by qiunet.
 * 18/1/30
 */
@RequestHandler(ID = 1005, desc = "")
public class PlayerInfoHttpJsonHandler extends HttpJsonHandler {

	@Override
	protected JsonResponse handler0(IHttpRequest<JsonRequest> request) {
		JsonResponse response = new JsonResponse();
		response.addAttribute("token", request.getRequestData().getString("token"));
		return response;
	}
}
