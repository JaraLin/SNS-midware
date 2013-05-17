package com.sns.midware.exceptions;

import com.sns.midware.config.ResponseCodes;

public class AuthFailException extends SNSMidwareBaseException {
	public AuthFailException(int errCode, String msg) {
		super(errCode, msg);
	}

	public AuthFailException(String msg) {
		super(ResponseCodes.AuthFail, msg);
	}

	private static final long serialVersionUID = 1L;

}
