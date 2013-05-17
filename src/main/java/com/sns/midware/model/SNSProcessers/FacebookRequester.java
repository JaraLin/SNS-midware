package com.sns.midware.model.SNSProcessers;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.scribe.builder.api.FacebookApi;

import com.sns.midware.config.ResponseCodes;
import com.sns.midware.data.EventMessage;
import com.sns.midware.data.GetDataMessage;
import com.sns.midware.data.PublicCommentMessage;
import com.sns.midware.data.ReplyMessage;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.data.response.SuccessResponseMessage;
import com.sns.midware.exceptions.AuthFailException;
import com.sns.midware.exceptions.InvalidAccessTokenException;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.exceptions.UnknownExceptioin;
import com.sns.midware.model.SNSRequester;
import com.sns.midware.utils.Netway;

public class FacebookRequester extends AbstractSNSRequester implements SNSRequester {
	Logger log =  Logger.getLogger(getClass());
	
	private String appId = "372225349482699";

	private String url = "https://graph.facebook.com";

	private int retry = 3;

	private Netway nt = new Netway(true);
	
	public FacebookRequester(){
		nt.setTimeout(30);
		Properties head = new Properties();
		head.setProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
		this.nt.setHeader(head);
	}

	@Override
	public JSONObject login(JSONObject req) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {
	 * 	 type: "facebook",
	 *   method:"publicReply",
	 *   accessToken:$accessToken,
	 *   messageText:$messageText,
	 *   destination:$destination
	 * }
	 */
	@Override
	public ResponseMessage publicReply(ReplyMessage req)
			throws SNSMidwareBaseException {
		
		String accessToken = req.getAccessToken();
		String destination = req.getDestination();
		String msg=req.getMessageText();
		postMsgToWall(accessToken, destination, msg);
		// doesn't throw any exception, means publish successfully.
		return new SuccessResponseMessage(
				"Publish Message successfully");
	}


	private boolean postMsgToWall(String accessToken, String destination,
			String msg) throws AuthFailException, UnknownExceptioin, InvalidAccessTokenException {
		HashMap<String,String> pdata = new HashMap<String,String>();
		pdata.put("access_token", accessToken);
		pdata.put("message", msg);
		String posturl = this.url + "/" + destination + "/feed";
		for (int i = 0; i < retry; i++) {
			try {
				String json = nt.postPage(posturl, pdata);
				log.info("Response for facebook:" + json);
				JSONObject jsonObject = new JSONObject(json);
				
				if (!jsonObject.has("error")) {
					return true;
				}
				JSONObject errObj = (JSONObject) jsonObject.get("error");
				
				if (!errObj.has("code")) {
					throw new UnknownExceptioin("Publish fail,and no errorCode returned from Facebook.");
				}
				int errCode = errObj.getInt("code");
				if (errCode == 190) {
					// the accesscode is invalid
					throw new InvalidAccessTokenException();
				} else {
					throw new UnknownExceptioin("Facebook return errorCode:"
							+ errObj.toString());
				}
			} catch (JSONException e) {
				log.error("error occur", e);
				throw new UnknownExceptioin(e);
//			} catch (Exception e) {
//				log.error("error occur", e);
//				if (i == retry - 1) {
//					throw new UnknownExceptioin(e);
//				}
			}
		}
		return false;
	}

	/**
	 * Refer to https://developers.facebook.com/docs/reference/dialogs/send/ , seems don't 
	 * https://www.facebook.com/dialog/send?app_id=123050457758183&
			name=People%20Argue%20Just%20to%20Win&
			link=http://www.nytimes.com/2011/06/15/arts/people-argue-just-to-win-scholars-assert.html&
			redirect_uri=http://www.example.com/response
		FIXME Must popup a dialog, can't do it quitely.
	 */
	@Override
	public ResponseMessage privateReply(ReplyMessage req)
			throws SNSMidwareBaseException {
		String sendUrl ="https://www.facebook.com/dialog/send";
		/*
		Properties pdata = new Properties();
		pdata.setProperty("access_token", accessToken);
		pdata.setProperty("name", msg);
		pdata.setProperty("link", msg); //required.
		pdata.setProperty("description", msg);
		*/
		return null;
	}

	/**
	 * Publich message to my wall.
	 * 	
	 * {
	 * 	 type: "facebook",
	 *   method:"publicReply",
	 *   accessToken:$accessToken,
	 *   messageText:$messageText,
	 *   destination:"me"
	 * }
	 *
	 */
	@Override
	public ResponseMessage publicComment(PublicCommentMessage req)
			throws SNSMidwareBaseException {
		String accessToken = req.getAccessToken(); 
		String destination = "me";
		String msg=req.getMessageText();
		postMsgToWall(accessToken, destination, msg);
		return new SuccessResponseMessage(
				"Publish Message successfully");
	}

	@Override
	public ResponseMessage publishEvent(EventMessage req)
			throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}
	

	/**
	 * likes: https://graph.facebook.com/me/likes?access_token=$access_token
	 * friends: https://graph.facebook.com/me/friends?access_token=$access_token
	 * feeds (wall): https://graph.facebook.com/me/feed?access_token=$access_token
	 */
	@Override
	public JSONObject getData(GetDataMessage req) throws SNSMidwareBaseException {
		String accessToken = req.getAccessToken(); 
		String posturl = this.url + "/me/likes?access_token=" + accessToken;
		String likeResult = nt.getPage(posturl);
		System.out.println(likeResult);
		
		int likeNum= getDataLength(likeResult);
		
		String friendsurl = this.url + "/me/friends?access_token=" + accessToken;
		String friendsResult = nt.getPage(friendsurl);
		System.out.println(friendsResult);
		int friendsNum= getDataLength(friendsResult);
		
		String feedurl = this.url + "/me/feed?access_token=" + accessToken;
		String feedResult = nt.getPage(feedurl);
		System.out.println(feedResult);
		int feedNum= getDataLength(feedResult);
		
		JSONObject result = new JSONObject();
		try {
			result.put("likes", likeNum);
			result.put("friends", friendsNum);
			result.put("feeds", feedNum);
			result.put("code",ResponseCodes.SUCCESS);
		} catch (JSONException e) {
			log.error("error occur when compose getData response in facebook.", e);
			throw new UnknownExceptioin(e);
		}
		return result;
	}
	
	
	private int getDataLength(String response){
		int likeNum=0;
		try {
			JSONObject likeJson = new JSONObject(response);
			
			likeNum = likeJson.getJSONArray("data").length();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return likeNum;
	}
	
	

	private static String apiKey = "505617472785544";
	private static String apiSecret = "6998cb5f546d9c514ddb49d4285be067";
	private static String callbackUrl = "http://xchange.com/";
	protected ApplicationSetting getAppSetting(){
		ApplicationSetting as = new ApplicationSetting();
		as.setApi(FacebookApi.class);
		as.setApiKey(apiKey);
		as.setApiSecret(apiSecret);
		as.setCallbackUrl(callbackUrl);
		as.setScope("read_stream,publish_stream,offline_access,read_mailbox,status_update,user_online_presence,user_photo_video_tags,user_status,read_requests,manage_notifications");
		return as;
	}
	
}
