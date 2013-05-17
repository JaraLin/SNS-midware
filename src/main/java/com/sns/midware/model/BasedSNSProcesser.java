package com.sns.midware.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sns.midware.config.ResponseCodes;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.exceptions.InvalidAccessTokenException;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.exceptions.UnknownExceptioin;
import com.sz.util.json.JsonConvertException;
import com.sz.util.json.JsonConverter;

public class BasedSNSProcesser implements ISNSProcesser {
	Logger log =  Logger.getLogger(getClass());
	
	private SNSRequester requester;

	public SNSRequester getRequester() {
		return requester;
	}

	public void setRequester(SNSRequester requester) {
		this.requester = requester;
	}

	private String name;
	
	private Method type;

	@Deprecated
	private JSONObject realReq;
	
	private JSONObject realResponse;
	
	@Deprecated
	private JSONObject rawRequest; // can by propertie or JsonObject?
	private JSONObject rawResponse;

	public BasedSNSProcesser() {
	}
	
	public BasedSNSProcesser(SNSRequester requester) {
		this.requester = requester;
	}

	public void preProcess() {
		// TODO get realReq;
		this.realReq = this.rawRequest;
	}

	public void postProcess() {
		// TODO change rawResponse to get realResponse;
		this.realResponse = this.rawResponse;
	}

	public Object request() throws SNSMidwareBaseException {
		this.preProcess();
		
		try {
			Method m = this.getAction();
			Object resp = m.invoke(this.requester, this.requestMessage);
			
			JSONObject jsonResp=null;
			if (resp instanceof ResponseMessage){
				jsonResp = JsonConverter.object2Json(resp);
			}else{
				jsonResp = (JSONObject)resp;
			}
			this.setRawResponse(jsonResp);
		} catch (IllegalAccessException e) {
			throw new UnknownExceptioin(e);
		} catch (IllegalArgumentException e) {
			throw new UnknownExceptioin(e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			log.error("error occur,", cause);
			if (cause instanceof InvalidAccessTokenException){
//				ResponseMessage resp = new ResponseMessage();
//				resp.setCode(ResponseCodes.INVALID_ACCESS_TOKEN);
				JSONObject authUrl = this.requester.createOAuthUrl();
				try {
					authUrl.put("code", ResponseCodes.INVALID_ACCESS_TOKEN);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
//				resp.getAttached().setProperty("authURL", authUrl );
				return authUrl;
			}
			throw new UnknownExceptioin(cause);
		} catch (JsonConvertException e) {
			throw new UnknownExceptioin(e);
		}
		
		this.postProcess();

		return getResponse();
	}

	private JSONObject getResponse() {
		return this.getRealResponse();
	}

	public Method getAction() {
		return type;
	}

	public void setAction(Method action) {
		this.type = action;
	}

	public JSONObject getRealReq() {
		return realReq;
	}

	public void setRealReq(JSONObject realReq) {
		this.realReq = realReq;
	}

	public JSONObject getRealResponse() {
		return realResponse;
	}

	public void setRealResponse(JSONObject realResponse) {
		this.realResponse = realResponse;
	}

	public JSONObject getRawRequest() {
		return rawRequest;
	}

	public void setRawRequest(JSONObject rawRequest) {
		this.rawRequest = rawRequest;
	}

	public JSONObject getRawResponse() {
		return rawResponse;
	}

	public void setRawResponse(JSONObject rawResponse) {
		this.rawResponse = rawResponse;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	private Object requestMessage;
	
	@Override
	public void setRequestMessage(Object messsage) {
		this.requestMessage=messsage;
		
	}

	@Override
	public boolean isReadyToPool() {
		return false;
	}


}
