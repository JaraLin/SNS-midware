package com.sns.midware.model;

import org.codehaus.jettison.json.JSONObject;

import com.sns.midware.data.AcessTokenVerifyMessage;
import com.sns.midware.data.EventMessage;
import com.sns.midware.data.GetDataMessage;
import com.sns.midware.data.PublicCommentMessage;
import com.sns.midware.data.ReplyMessage;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.exceptions.SNSMidwareBaseException;

/*
 * It will be implemeted by the specifed SNS, such as twitter, facebook
 */
public interface SNSRequester {
	Object login(JSONObject req) throws SNSMidwareBaseException;

	@Action("publicReply")
	ResponseMessage publicReply(ReplyMessage req) throws SNSMidwareBaseException;
	
	@Action("privateReply")
	ResponseMessage privateReply(ReplyMessage req) throws SNSMidwareBaseException;
	
	@Action("publicComment")
	ResponseMessage publicComment(PublicCommentMessage req) throws SNSMidwareBaseException;
	
	@Action("event")
	ResponseMessage publishEvent(EventMessage req) throws SNSMidwareBaseException;

	@Action("getData")
	JSONObject getData(GetDataMessage req) throws SNSMidwareBaseException;
	
	@Action("tokenVerify")
	JSONObject tokenVerify(AcessTokenVerifyMessage req) throws SNSMidwareBaseException;

	@Action("createOAuthUrl")
	JSONObject createOAuthUrl()  throws SNSMidwareBaseException;
}
