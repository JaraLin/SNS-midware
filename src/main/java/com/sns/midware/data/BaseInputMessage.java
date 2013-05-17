package com.sns.midware.data;

public abstract class BaseInputMessage{
	private String sessoinId;
	private String accessToken;
	private String method;
	private String type;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	//Generally, only reply message need destination, but for Foursquare, all mesage must have the field, and it should be a venuId.
	private String destination;
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSessoinId() {
		return sessoinId;
	}

	public void setSessoinId(String sessoinId) {
		this.sessoinId = sessoinId;
	}
}
