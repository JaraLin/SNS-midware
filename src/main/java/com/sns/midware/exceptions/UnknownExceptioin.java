package com.sns.midware.exceptions;

import com.sns.midware.config.ResponseCodes;

public class UnknownExceptioin extends SNSMidwareBaseException {
	public UnknownExceptioin(String msg) {
		super(ResponseCodes.UnknowError, msg);
	}

	public UnknownExceptioin(Throwable e) {
		super(ResponseCodes.UnknowError,e.getMessage());
		
	}

	public UnknownExceptioin() {
		this("Unknow exception occur.");
	}

	private static final long serialVersionUID = 1L;

}
