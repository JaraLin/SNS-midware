package com.sns.midware.utils;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sns.midware.exceptions.InvalidRequestException;

public class RequestFieldFetcher {
	public static String getFieldValue(JSONObject request, String fieldName)
			throws InvalidRequestException {
		try {
			String value = request.getString(fieldName);
			if (StringUtils.isEmpty(value)){
				throw new InvalidRequestException("The field " + fieldName + " is Missing");
			}
			return value;
		} catch (JSONException e) {
			e.printStackTrace();
			throw new InvalidRequestException("The field " + fieldName + " is Missing");
		}
	}
}
