package com.sns.midware.model;

import java.lang.reflect.Method;

import org.codehaus.jettison.json.JSONObject;

import com.sns.midware.data.BaseInputMessage;
import com.sns.midware.exceptions.SNSMidwareBaseException;

public interface ISNSProcesser {
	public String getName();

	public void preProcess();

	public void postProcess();

	public Object request() throws SNSMidwareBaseException;

	public void setAction(Method action);

	@Deprecated
	public void setRawRequest(JSONObject rawResponse);

	//It will accept both BaseInputMessagen and JSONObject.
	public void setRequestMessage(Object messsage);
	
	public SNSRequester getRequester();
	
	public boolean isReadyToPool();
}
