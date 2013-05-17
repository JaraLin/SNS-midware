package com.sns.midware.model.SNSProcessers;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.*;

import twitter4j.auth.RequestToken;

import com.sns.midware.config.ResponseCodes;
import com.sns.midware.data.AcessTokenVerifyMessage;
import com.sns.midware.data.EventMessage;
import com.sns.midware.data.GetDataMessage;
import com.sns.midware.data.PublicCommentMessage;
import com.sns.midware.data.ReplyMessage;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.exceptions.UnknownExceptioin;
import com.sns.midware.model.SNSRequester;

public class FacebookRequestNew extends AbstractSNSRequester implements
		SNSRequester {
	private static final String NETWORK_NAME = "Facebook";
	private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
	private static final Token EMPTY_TOKEN = null;

	private static Map<String, OAuthService> pendingRequestTokens = new HashMap<String, OAuthService>();

	private static String apiKey = "your_app_id";
	private static String apiSecret = "your_api_secret";

	private OAuthService service;

	public FacebookRequestNew() {
		service = new ServiceBuilder().provider(FacebookApi.class)
				.apiKey(apiKey).apiSecret(apiSecret)
				.callback("http://www.example.com/oauth_callback/").build();

	}

	@Override
	public JSONObject login(JSONObject req) throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseMessage publicReply(ReplyMessage req)
			throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public JSONObject getData(GetDataMessage req)
			throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject createOAuthUrl() throws SNSMidwareBaseException {
		String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
		String verifyKey = service.getRequestToken().toString();
		pendingRequestTokens.put(verifyKey, service);
		// tokenkey also need return to Client.
		try {
			JSONObject result = new JSONObject();
			result.put("verifyKey", verifyKey);
			result.put("authUrl", authorizationUrl);
			return result;
		} catch (JSONException e) {

			e.printStackTrace();
		}
		throw new UnknownExceptioin("Don't support the method createOAuthUrl.");
	}

	@Override
	public JSONObject tokenVerify(AcessTokenVerifyMessage req)
			throws SNSMidwareBaseException {
		String verifyKey = req.getVerifyKey();
		String verifyCode = req.getVerifyCode();

		Verifier verifier = new Verifier(verifyCode);
		System.out.println();

		// Trade the Request Token and Verfier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		OAuthService service = pendingRequestTokens.remove(verifyKey);
		if (service==null){
			throw new SNSMidwareBaseException(ResponseCodes.INVALID_VERIFY_KEY,"The verifyKey is incorrect.");
		}
		
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		
		String accessTokenMix = accessToken.getToken() + "||" + accessToken.getSecret();
		JSONObject result = new JSONObject();
		try {
			result.put("code", ResponseCodes.SUCCESS);
			result.put("accessToken", accessTokenMix);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

}
