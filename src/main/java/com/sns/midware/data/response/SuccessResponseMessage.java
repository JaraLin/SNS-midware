package com.sns.midware.data.response;

import com.sns.midware.config.ResponseCodes;

public class SuccessResponseMessage extends ResponseMessage {
	private static final String message="operatioin successfully!";
	public SuccessResponseMessage(){
		this(message);
	}
	public SuccessResponseMessage(String message){
		super(ResponseCodes.SUCCESS,message);
	}
}
