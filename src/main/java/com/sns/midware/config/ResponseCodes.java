package com.sns.midware.config;

public interface ResponseCodes {
	public final int SUCCESS = 200;
	public final int requestInvalid = 101;
	public final int AuthFail = 102;

	public final int METHOD_DONT_IMPLEMENT = 103;

	public final int UnknowError = 999;
	public final int INVALID_ACCESS_TOKEN = 401;
	public final int INVALID_VERIFY_KEY = 402;
}
