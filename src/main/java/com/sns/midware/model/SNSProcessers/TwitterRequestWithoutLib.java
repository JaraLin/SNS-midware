package com.sns.midware.model.SNSProcessers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.codehaus.jettison.json.JSONObject;

import com.sns.midware.data.EventMessage;
import com.sns.midware.data.GetDataMessage;
import com.sns.midware.data.PublicCommentMessage;
import com.sns.midware.data.ReplyMessage;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.exceptions.NoImplementedMethodException;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.model.SNSRequester;
import com.sns.midware.utils.Netway;

public class TwitterRequestWithoutLib extends AbstractSNSRequester   implements SNSRequester {
	private static String updateStatusURL="https://api.twitter.com/1.1/statuses/update.json";

	private Netway nt = new Netway(true);
	public TwitterRequestWithoutLib(){
		nt.setTimeout(30);
		Properties head = new Properties();
		head.setProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
		this.nt.setHeader(head);
	}
	
	@Override
	public JSONObject login(JSONObject req) throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseMessage publicReply(ReplyMessage req) throws SNSMidwareBaseException {
		String posturl = updateStatusURL;
		
		String accessToken = req.getAccessToken();
		String destination = req.getDestination();
		String msg=req.getMessageText();
		
		Map<String,String> pdata = new HashMap<String,String>();
		pdata.put("status", msg);
	//	pdata.setProperty("access_token ", accessToken);
		
		
		Properties head= new Properties();
		head.setProperty("Authorization", "OAuth oauth_consumer_key=\"W5WmuiEoA2sj7yQueLg\", oauth_nonce=\"0511780317f981ba5f0d3f1d984fe658\", oauth_signature=\"b83IK7jgNuSo%2F2tLTozbmw%2Bkduc%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1352020097\", oauth_token=\"550906494-EWYldLAYVoAWtnxBm2hyLjqbN6L5vmVjTA9EqkcL\", oauth_version=\"1.0\"");
		nt.setHeader(head);
		String likeResult = nt.postPage(posturl, pdata);
		return null;
	}

	@Override
	public ResponseMessage privateReply(ReplyMessage req) throws SNSMidwareBaseException {
		throw new NoImplementedMethodException();
	}

	@Override
	public ResponseMessage publicComment(PublicCommentMessage req) throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseMessage publishEvent(EventMessage req) throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getData(GetDataMessage req) throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

}
