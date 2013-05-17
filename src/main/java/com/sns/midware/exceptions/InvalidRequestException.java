package com.sns.midware.exceptions;

import com.sns.midware.config.ResponseCodes;

public class InvalidRequestException extends SNSMidwareBaseException {
	private static final long serialVersionUID = 1L;

	public InvalidRequestException(String msg) {
		super(ResponseCodes.requestInvalid,msg);
	}
}
