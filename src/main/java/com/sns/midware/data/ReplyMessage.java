package com.sns.midware.data;

/**
 * { type: $type, method:"publicReply", accessToken:$accessToken,
 * messageText:$messageText, destination:$destination }
 */
public class ReplyMessage extends BaseInputMessage {
	private String messageText;

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

}
