package com.sns.midware.model.SNSProcessers;

import org.scribe.builder.api.Api;

public class ApplicationSetting {
	private String apiKey;
	private String apiSecret;
	private Class<? extends Api> api;
	private String callbackUrl;
	
	private String scope;

	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getApiSecret() {
		return apiSecret;
	}
	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}
	public Class<? extends Api> getApi() {
		return api;
	}
	public void setApi(Class<? extends Api> api) {
		this.api = api;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
}
