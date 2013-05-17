package com.sns.midware.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlRootElement
@XmlType(propOrder = { "code", "message" })
public class StatusResponse {
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private int code;
	private String message;

	public StatusResponse() {

	}

	public StatusResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("code", getCode());
			json.put("message", getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

}
