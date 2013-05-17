package com.sns.midware.exceptions;

import com.sns.midware.config.ResponseCodes;

public class NoImplementedMethodException extends SNSMidwareBaseException {
	public NoImplementedMethodException() {
		super(ResponseCodes.METHOD_DONT_IMPLEMENT,"The method is not supproted now!");
		
	}

	private static final long serialVersionUID = 1L;
}
