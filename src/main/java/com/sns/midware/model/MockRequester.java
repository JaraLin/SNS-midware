package com.sns.midware.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sns.midware.data.EventMessage;
import com.sns.midware.data.GetDataMessage;
import com.sns.midware.data.PublicCommentMessage;
import com.sns.midware.data.ReplyMessage;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.data.response.SuccessResponseMessage;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.model.SNSProcessers.AbstractSNSRequester;

public class MockRequester extends AbstractSNSRequester  implements SNSRequester {

	@Override
	public JSONObject login(JSONObject req) {
		JSONObject json = new JSONObject();
		try {
			json.put("code", "0");
			json.put("message", "Login successfully");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public ResponseMessage publicReply(ReplyMessage req) {
		return new SuccessResponseMessage("publishMessage successfully");
	}

	@Override
	public JSONObject getData(GetDataMessage req) {
		JSONObject json = new JSONObject();
		try {
			json.put("code", "0");
			json.put("message", "getData successfully");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public ResponseMessage privateReply(ReplyMessage req)
			throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseMessage publicComment(PublicCommentMessage req)
			throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseMessage publishEvent(EventMessage req)
			throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

}
