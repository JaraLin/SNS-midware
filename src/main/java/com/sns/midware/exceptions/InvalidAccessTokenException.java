package com.sns.midware.exceptions;

import com.sns.midware.config.ResponseCodes;

public class InvalidAccessTokenException extends SNSMidwareBaseException {

	public InvalidAccessTokenException() {
		super(ResponseCodes.INVALID_ACCESS_TOKEN,"The accessToken is invalid.");
		// TODO Auto-generated constructor stub
	}

}
