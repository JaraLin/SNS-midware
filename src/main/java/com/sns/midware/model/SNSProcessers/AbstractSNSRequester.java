package com.sns.midware.model.SNSProcessers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.sns.midware.config.ResponseCodes;
import com.sns.midware.data.AcessTokenVerifyMessage;
import com.sns.midware.data.ReplyMessage;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.exceptions.NoImplementedMethodException;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.exceptions.UnknownExceptioin;
import com.sns.midware.model.SNSRequester;

public abstract class AbstractSNSRequester implements SNSRequester {
	@Override
	public JSONObject createOAuthUrl() throws SNSMidwareBaseException {
		ApplicationSetting appSetting= getAppSetting();
		if (appSetting==null){
			throw new UnknownExceptioin("Don't support the method createOAuthUrl." );
		}
		return createOAuthUrl(appSetting);
	}
	
	
	@Override
	public ResponseMessage publicReply(ReplyMessage req) throws SNSMidwareBaseException {
		throw new NoImplementedMethodException();
	}

	@Override
	public ResponseMessage privateReply(ReplyMessage req) throws SNSMidwareBaseException {
		throw new NoImplementedMethodException();
	}
	
	
	private static final Token EMPTY_TOKEN = null;
	private static Map<String, OAuthService> pendingRequestTokens = new HashMap<String, OAuthService>();
	protected JSONObject createOAuthUrl(ApplicationSetting appSetting) throws SNSMidwareBaseException {
		ServiceBuilder servivceBulder= new ServiceBuilder().provider(appSetting.getApi())
				.apiKey(appSetting.getApiKey()).apiSecret(appSetting.getApiSecret());
		
		if (StringUtils.isNotEmpty(appSetting.getScope())){
			servivceBulder.scope(appSetting.getScope());
		}
		
		OAuthService service = servivceBulder
				.callback(appSetting.getCallbackUrl()).build();
		
		String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
		String verifyKey = createRadomKey();
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
		
		String accessTokenMix = accessToken.getToken();
		if (StringUtils.isNotEmpty(accessToken.getSecret())) {
			accessTokenMix += "||" + accessToken.getSecret();
		}
		JSONObject result = new JSONObject();
		try {
			result.put("code", ResponseCodes.SUCCESS);
			result.put("accessToken", accessTokenMix);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String createRadomKey(){
		return System.currentTimeMillis() + "";
		
	}
	
	protected ApplicationSetting getAppSetting(){
		return null;
	}
}
