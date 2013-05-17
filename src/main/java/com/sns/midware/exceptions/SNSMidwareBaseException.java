package com.sns.midware.exceptions;

public class SNSMidwareBaseException extends Exception {

	private static final long serialVersionUID = 1L;

	private int errorCode = 0;
	
	public SNSMidwareBaseException(int errCode) {
		this.setErrorCode(errCode);
	}

	public SNSMidwareBaseException(int errCode, String msg) {
		super(msg);
		this.setErrorCode(errCode);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
