package com.sns.midware.data.response;

import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "code", "message" ,"attached" })
public class ResponseMessage {
	private int code;
	private String message;
	
	private Properties attached;
	
	public ResponseMessage(){
		
	}
	
	public ResponseMessage(int code,String text){
		this.setCode(code);
		this.setMessage(text);
	}


	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Properties getAttached() {
		return attached;
	}

	public void setAttached(Properties attached) {
		this.attached = attached;
	}
	
	

}
