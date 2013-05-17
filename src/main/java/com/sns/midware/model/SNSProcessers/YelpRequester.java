package com.sns.midware.model.SNSProcessers;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sns.midware.data.EventMessage;
import com.sns.midware.data.GetDataMessage;
import com.sns.midware.data.PublicCommentMessage;
import com.sns.midware.data.ReplyMessage;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.data.response.SuccessResponseMessage;
import com.sns.midware.exceptions.AuthFailException;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.model.Action;
import com.sns.midware.model.SNSRequester;

public class YelpRequester extends AbstractSNSRequester  implements SNSRequester {
	private YelpBrowser browser = new YelpBrowser();
	
	@Override
	@Action("Login")
	public ResponseMessage login(JSONObject req) throws SNSMidwareBaseException {
		try {
			String email = req.getString("email");
			String password = req.getString("passwrod");
			boolean result=browser.login(email, password);
			if(result){
				return new SuccessResponseMessage(
						"login successfully");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		throw new AuthFailException("Email or password is incorrect!");
	}

	@Override
	public ResponseMessage publicReply(ReplyMessage req) throws SNSMidwareBaseException {
		String reviewId =req.getDestination();
		String msg = req.getMessageText();
		this.browser.sendPrivateMessage(reviewId,msg);

		return new SuccessResponseMessage(
				"Publish Message successfully");
	}

	@Override
	public ResponseMessage privateReply(ReplyMessage req) throws SNSMidwareBaseException {
		String reviewId =req.getDestination();
		String msg = req.getMessageText();
		this.browser.addPublicComment(reviewId,msg);

		return new SuccessResponseMessage(
				"Publish Message successfully");
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

	@Override
	public	JSONObject getData(GetDataMessage req) throws SNSMidwareBaseException {
		String reviewMainPage = this.browser.clickReviewLink();
		return null;
	}

}
