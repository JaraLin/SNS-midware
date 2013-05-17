package com.sns.midware.model.SNSProcessers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import com.sns.midware.config.ResponseCodes;
import com.sns.midware.data.AcessTokenVerifyMessage;
import com.sns.midware.data.EventMessage;
import com.sns.midware.data.GetDataMessage;
import com.sns.midware.data.PublicCommentMessage;
import com.sns.midware.data.ReplyMessage;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.data.response.SuccessResponseMessage;
import com.sns.midware.exceptions.InvalidAccessTokenException;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.exceptions.UnknownExceptioin;
import com.sns.midware.model.SNSRequester;
import com.sz.util.json.JsonConvertException;
import com.sz.util.json.JsonConverter;

public class TwitterRequester extends AbstractSNSRequester implements SNSRequester {
	private static Map<String, RequestToken> pendingRequestTokens = new HashMap<String, RequestToken>();

	Logger log =  Logger.getLogger(getClass());
	@Override
	public JSONObject login(JSONObject req) throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {
	 * 	 type: "twitter",
	 *   method:"publicReply",
	 *   accessToken:$accessToken,
	 *   messageText:$messageText,
	 *   destination:$twitterName
	 * }
	 */
	@Override
	public ResponseMessage publicReply(ReplyMessage req) throws SNSMidwareBaseException {
		String accessToken = req.getAccessToken();
		String destination = req.getDestination();
		String msg = req.getMessageText();
		if (StringUtils.isNotEmpty(destination)){
			msg = "@" + destination + " " + msg;
		}
		boolean result = runWithAccessToken(accessToken, msg);

		return new SuccessResponseMessage();
	}

	@Override
	public ResponseMessage publicComment(PublicCommentMessage req) throws SNSMidwareBaseException {
		String accessToken = req.getAccessToken();
		String msg = req.getMessageText();

		boolean result = runWithAccessToken(accessToken, msg);

		return new SuccessResponseMessage();
	}

	@Override
	public ResponseMessage publishEvent(EventMessage req) throws SNSMidwareBaseException {
		String accessToken = req.getAccessToken();
		String msg = req.getMessageText();
		String imageUrl = req.getImageUrl();
		msg += " " + imageUrl;
		boolean result = runWithAccessToken(accessToken, msg);
		return new SuccessResponseMessage();
	}

	@Override
	public JSONObject getData(GetDataMessage req) throws SNSMidwareBaseException {
		List<Properties> resultJson= new ArrayList<Properties>();
		String accessToken = req.getAccessToken();
		Twitter twitter = getTwitterInstance(accessToken);
		try {
			String name=twitter.getScreenName();
			Query query = new Query("#" + name);
			QueryResult result;
			result = twitter.search(query);
			for (Tweet tweet : result.getTweets()) {
				Properties p = new Properties();
				p.setProperty(tweet.getFromUser(), tweet.getText());
				resultJson.add(p);
				System.out.println(tweet.getFromUser() + ":" + tweet.getText());
			}
		} catch (TwitterException e) {
			log.error(" Gate Data # error occure",e);
		}
		
		try {
			ResponseList<Status> mentions=twitter.getMentions();
			for (Status status : mentions) {
				String from =status.getUser().getName();
				String text = status.getText();
				
				Properties p = new Properties();
				p.setProperty(from, text);
				resultJson.add(p);
			}
		} catch (TwitterException e) {
			log.error(" Gate Data @ error occure",e);
		}
		
		try {
			return JsonConverter.object2Json(resultJson);
		} catch (JsonConvertException e) {
			throw new UnknownExceptioin(e);
		}
	}
	private String consumerToken = "W5WmuiEoA2sj7yQueLg";
	private String consumerSecret = "waxQmJSviTPyTLElPVqbgc6jQDEA1DGRbiQNos36hx4";
//	private String accessToken = "550906494-EWYldLAYVoAWtnxBm2hyLjqbN6L5vmVjTA9EqkcL";
//	private String accessSecret = "lEKTw24Rx9M2vYfTyJwDq20Mzoy4kN4zzR46FG15U9o";

//	public String getAccessToken() {
//		return accessToken;
//	}
//
//	public void setAccessToken(String accessToken) {
//		this.accessToken = accessToken;
//	}

	public String getConsumerToken() {
		return consumerToken;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerToken(String consumerToken) {
		this.consumerToken = consumerToken;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	@Deprecated
	public String[] runOAuth(String consumerKey, String consumerPwd)
			throws TwitterException, IOException {
		Twitter twitter = new TwitterFactory().getInstance();
		// twitter.setOAuthConsumer(consumerKey, consumerPwd);
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out
					.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out
					.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = br.readLine();
			try {
				if (pin.length() > 0) {
					accessToken = twitter
							.getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}
		}
		// persist to the accessToken for future reference.
		Status status = null;
		try {
			status = twitter.updateStatus("The first post..");
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Successfully updated the status to ["
				+ status.getText() + "].");
		return new String[] { accessToken.getToken(),
				accessToken.getTokenSecret() };
	}

	private boolean runWithAccessToken(String accessToken,String msg) throws SNSMidwareBaseException {
		Twitter twitter = getTwitterInstance(accessToken);
		Status status;
		try {
			status = twitter.updateStatus(msg);
			System.out.println("Successfully updated the status to [" + status.getText() + "].");
			return true;
		} catch (TwitterException te) {
			if (401 == te.getStatusCode()) {
				throw new InvalidAccessTokenException();
			} else {
				log.error(te);
				throw new UnknownExceptioin(te);
			}
		} catch (Exception te){
			log.error(te);
			throw new UnknownExceptioin(te);
		}
	}

	private Twitter getTwitterInstance(String accessTokenMix) throws InvalidAccessTokenException {
		if (StringUtils.isBlank(accessTokenMix)){
			throw new InvalidAccessTokenException();
		}
		//The accessToken acutally include accesstoken and accessSecret
		String[] temp = accessTokenMix.split("\\|\\|");
		
		if (temp.length!=2){
			log.info("accessToken=" + accessTokenMix + " ,lenth=" + temp.length);
			throw new InvalidAccessTokenException();
		}
		
		String accessToken = temp[0];
		String accessSecret = temp[1];
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerToken)
				.setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		return twitter;
	}
	
	@Override
	public JSONObject tokenVerify(AcessTokenVerifyMessage req) throws SNSMidwareBaseException {
		String key = req.getVerifyKey();
		String pin = req.getVerifyCode();
		RequestToken requestToken = pendingRequestTokens.remove(key);
		if (requestToken==null){
			throw new SNSMidwareBaseException(ResponseCodes.INVALID_VERIFY_KEY,"The verifyKey is incorrect.");
		}
		AccessToken accessToken= null;
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(consumerToken, consumerSecret);
			if (pin.length() > 0) {
				accessToken = twitter
						.getOAuthAccessToken(requestToken, pin);
			} else {
				accessToken = twitter.getOAuthAccessToken();
			}
		} catch (TwitterException te) {
			if (401 == te.getStatusCode()) {
				System.out.println("Unable to get the access token.");
			} else {
				te.printStackTrace();
			}
		}
		String accessTokenMix = accessToken.getToken() + "||" + accessToken.getTokenSecret();
		JSONObject result = new JSONObject();
		try {
			result.put("code", ResponseCodes.SUCCESS);
			result.put("accessToken", accessTokenMix);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public JSONObject createOAuthUrl(){
		 Twitter twitter = new TwitterFactory().getInstance();
         twitter.setOAuthConsumer(consumerToken, consumerSecret);
        try {
			RequestToken requestToken = twitter.getOAuthRequestToken();
			String tokenkey = requestToken.getToken();  //I"m not sure what it is. (It's correct.)
			pendingRequestTokens.put(tokenkey, requestToken);
			// tokenkey also need return to Client.
			JSONObject result= new JSONObject();
			result.put("verifyKey", tokenkey);
			result.put("authUrl", requestToken.getAuthorizationURL());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
        
	}

//	public String getAccessSecret() {
//		return accessSecret;
//	}
//
//	public void setAccessSecret(String accessSecret) {
//		this.accessSecret = accessSecret;
//	}
}
