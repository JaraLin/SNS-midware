package com.sns.midware.model.SNSProcessers;

public class YelpReview {
	private String reviewId;
	private String privateMsgLink;
	private String publicMsgLink;
	private String content;

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public String getPrivateMsgLink() {
		return privateMsgLink;
	}

	public void setPrivateMsgLink(String privateMsgLink) {
		this.privateMsgLink = privateMsgLink;
	}

	public String getPublicMsgLink() {
		return publicMsgLink;
	}

	public void setPublicMsgLink(String publicMsgLink) {
		this.publicMsgLink = publicMsgLink;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
