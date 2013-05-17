package com.sns.midware.model.SNSProcessers;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.sns.midware.config.ConstValues;
import com.sns.midware.config.ResponseCodes;
import com.sns.midware.data.EventMessage;
import com.sns.midware.data.GetDataMessage;
import com.sns.midware.data.PublicCommentMessage;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.data.response.SuccessResponseMessage;
import com.sns.midware.exceptions.InvalidAccessTokenException;
import com.sns.midware.exceptions.InvalidRequestException;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.exceptions.UnknownExceptioin;
import com.sns.midware.model.Action;
import com.sns.midware.model.SNSRequester;
import com.sns.midware.utils.Netway;
import com.sns.midware.utils.RequestFieldFetcher;

/**
 * https://api.foursquare.com/v2/venues/managed?v=20121104&oauth_token=
 * Can get all Venues of the current users.
 * Refer to: https://developer.foursquare.com/docs/venues/managed
 * 
 * Add Photo, Refer to https://api.foursquare.com/v2/photos/add
 */
public class FoursquareRquester extends AbstractSNSRequester  implements SNSRequester {

	@Override
	public JSONObject login(JSONObject req) throws SNSMidwareBaseException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Add Tip? not sure
	 * Refer to: https://developer.foursquare.com/docs/tips/add
	 * 
	 */
	@Override
	public ResponseMessage publicComment(PublicCommentMessage req)
			throws SNSMidwareBaseException {
		String accessToken = req.getAccessToken();
		String message = req.getMessageText();
		String id = req.getDestination();
		String url = "https://api.foursquare.com/v2/tips/add";
		Properties bodyParameters = new Properties();
		bodyParameters.setProperty("venueId", id);
		bodyParameters.setProperty("text", message);
		JSONObject respJson = getResponse(url, accessToken, null, bodyParameters,Verb.POST);
		return new SuccessResponseMessage();
	}

	// Tip with image, need double check it.
	@Override
	public ResponseMessage publishEvent(EventMessage req) throws SNSMidwareBaseException {
		  String accessToken =req.getAccessToken();
		  String id = req.getDestination();
		  String url="https://api.foursquare.com/v2/photos/add?v=20121104&oauth_token=";
		  url += accessToken;
		  
		  /*
		  
		 // The following code can add a photo.
		  Netway nt = new Netway(true);
		  Properties pdata = new Properties();
		  pdata.put("tipId", id);
		  pdata.put("postText", "This is a good place..");
		  pdata.put("photo", new File("C:/Users/Cyber/Desktop/images/add.jpg"));
		  */
		return null;
	}

	/**
	 * Refer to: https://developer.foursquare.com/docs/venues/venues
	 * Example: https://api.foursquare.com/v2/venues/4b181f60f964a52052cd23e3?oauth_token=1M0FM2KCGIZBTBIC1CPWWMI5FJGN1NRGG224IJWLNBT0JU3C&v=20121104
	 * Can get 
	 * stats: {
        checkinsCount: 562
        usersCount: 240
        tipCount: 10
       }
     * and Comments (Tips in Foursquare)  
	 */
	@Override
	public JSONObject getData(GetDataMessage req) throws SNSMidwareBaseException {
		String accessTokenStr = req.getAccessToken();
		String venueId= req.getDestination();
		if(StringUtils.isEmpty(venueId)){
			throw new InvalidRequestException("Miss the field [destination].");
		}
		String url = "https://api.foursquare.com/v2/venues/" + venueId;
		JSONObject respJson =getResponse(url,accessTokenStr,null,null,Verb.GET);
		
		return respJson;
	}
	
	static String apiKey = "BFMTKX0JQ5WSRBCT4NLIXLYTIOV41J52FTTZVWPBZUIY421F";
	static String apiSecret = "Y3SJYFRE5V2OW0YJLFGXVCF34YF2CMNDBMKIMY5MKFRUPQX1";
	private static String callbackUrl = "http://mybeautiful.iteye.com";  //Callback must same as the setting in Foursquare!
	
	protected ApplicationSetting getAppSetting(){
		ApplicationSetting as = new ApplicationSetting();
		as.setApi(Foursquare2Api.class);
		as.setApiKey(apiKey);
		as.setApiSecret(apiSecret);
		as.setCallbackUrl(callbackUrl);
		return as;
	}
	
	
	@Action("getVenues")
	public JSONObject getVenues(JSONObject req) throws SNSMidwareBaseException {
		String accessTokenStr = RequestFieldFetcher.getFieldValue(req,ConstValues.REQUEST_SNS_ACCESSTOKEN_KEY );
		String url = "https://api.foursquare.com/v2/venues/managed" ;
		JSONObject respJson = getResponse(url, accessTokenStr, null, null,Verb.GET);
		try {

			JSONArray venues =respJson.getJSONObject("response").getJSONArray("venues");
			
			JSONObject result = new JSONObject();
			JSONArray resultVenues = new JSONArray();
			for (int i = 0; i < venues.length(); i++) {
				JSONObject json = venues.getJSONObject(i);
				String id =json.getString("id");
				String name=json.getString("name");
				
				JSONObject theVenue = new JSONObject();
				theVenue.put("id", id);
				theVenue.put("name", name);
				
				resultVenues.put(theVenue);
			}
			result.put("venues", resultVenues);
			result.put("code", ResponseCodes.SUCCESS);
			return result;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnknownExceptioin(e);
		}
		
	}
	
	private JSONObject getResponse(String url,String accessTokenStr, Properties queryParameters,Properties bodyParameters, Verb httpMethod) throws SNSMidwareBaseException {
		OAuthService service = new ServiceBuilder()
				.provider(Foursquare2Api.class).apiKey(apiKey)
				.apiSecret(apiSecret).callback(callbackUrl)
				.build();

		Token accessToken = new Token(accessTokenStr, null);
		
		if(url.contains("?")){
			url+="&";
		}else{
			url+="?";
		}
		
		url += "v=20121104&oauth_token=" + accessToken.getToken();
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		
		if(queryParameters!=null){
			for ( Enumeration e =queryParameters.keys(); e.hasMoreElements();) {
				String key = (String)e.nextElement();
				String value = queryParameters.getProperty(key);
				if (StringUtils.isEmpty(value)){
					continue;
				}
				request.addQuerystringParameter(key, value);
			}
		}
		
		if(bodyParameters!=null){
			for ( Enumeration e =bodyParameters.keys(); e.hasMoreElements();) {
				String key = (String)e.nextElement();
				String value = bodyParameters.getProperty(key);
				if (StringUtils.isEmpty(value)){
					continue;
				}
				request.addBodyParameter(key, value);
				
			}
		}
		
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println(response.getCode());
		String resp = response.getBody();
		
		try {
			JSONObject respJson = new JSONObject(resp);
			
			int responseCode = response.getCode();
			
			if (responseCode==401) {
				throw new InvalidAccessTokenException();
			}
			
			if (responseCode!=200) {
				throw new SNSMidwareBaseException(responseCode,"Error got from Foursquare:" + respJson.toString());
			}
			return respJson;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnknownExceptioin(e);
		}
		
	}

}
