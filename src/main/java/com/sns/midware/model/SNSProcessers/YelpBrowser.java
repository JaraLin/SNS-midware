package com.sns.midware.model.SNSProcessers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sns.midware.utils.Netway;

public class YelpBrowser {

	private boolean isLogin;
	private String reviewLink;
	private Netway nt = new Netway(true);

	private List<YelpReview> reviews = new ArrayList<YelpReview>();

	public YelpBrowser() {
		nt.setTimeout(30);
		Properties header = new Properties();
		header.setProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
		header.setProperty("Content-Type", "application/x-www-form-urlencoded");

		header.setProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

		header.setProperty("Host", "biz.yelp.com");
		header.setProperty("Origin", "https://biz.yelp.com");
		header.setProperty("Referer:", "https://biz.yelp.com/");

		header.setProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		// header.setProperty("Accept-Encoding", "gzip,deflate,sdch");
		header.setProperty("Accept-Language",
				"en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
		header.setProperty("Cache-Control", "max-age=0");
		header.setProperty("Connection", "keep-alive");
		nt.setHeader(header);

	}

	public boolean login(String email, String password) {
		Map<String, String> loginPros = createLoginProperties("https://biz.yelp.com");
		loginPros.put("email", email);
		loginPros.put("password", password);

		String url = "https://biz.yelp.com/login";

		// nt.retainCookies(new String[]{"bse"});

		String resp = nt.postPage(url, loginPros);
		// nt.getPage("https://biz.yelp.com/r2r/vl8k7RPKnyHyPRO4XUpWCQ/");
		System.out.println(resp);

		// FIXME check if login successfully.
		// TODO set reviewLink

		return true;
	}

	public String getContent(String url) {
		return null;
	}

	private Map createLoginProperties(String authUrl) {
		String resp = nt.getPage(authUrl);

		String loginForm = getByRegex(resp,
				"<form action=\"/login\" id=\"login-form\"[\\s\\S]+?>([\\s\\S]+?)</form>");

		// <form action="/login" id="login-form" method="POST"
		// name="login_form">
		System.out.println(loginForm);
		Map props = getTextFiledOfForm(loginForm, "hidden");
		return props;
		// loginByUserId(props,"zhangyu0182", "98111223");

	}

	private static String getByRegex(String src, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(src);
		while (m.find()) {
			// System.out.println(m.group(1));
			return m.group(1);
		}
		return null;
	}

	private Map getTextFiledOfForm(String formString, String type) {
		Pattern p = Pattern
				.compile("<input type=\""
						+ type
						+ "\"[\\s\\S]*? name=\"([\\S]+?)\"[\\s\\S]+?value=\"([\\s\\S]*?)\">");
		Matcher m = p.matcher(formString);
		HashMap<String, String> props = new LinkedHashMap<String, String>();
		while (m.find()) {
			System.out.println(m.group(1) + "=" + m.group(2));
			props.put(m.group(1), m.group(2));
		}
		return props;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public String getReviewLink() {
		return reviewLink;
	}

	public void setReviewLink(String reviewLink) {
		this.reviewLink = reviewLink;
	}

	public String clickReviewLink() {
		String reviewLink = this.getReviewLink();
		return this.getContent(reviewLink);
	}

	public void sendPrivateMessage(String reviewId, String msg) {
		YelpReview review = this.getReviewById(reviewId);
		String privateLink = review.getPrivateMsgLink();
		String msgPage = nt.getPage(privateLink);
		
		String msgForm = getByRegex(msgPage,
				"<form action=\"/login\" id=\"response_form\"[\\s\\S]+?>([\\s\\S]+?)</form>");

		String actionUrl = "";  //TODO get action from response_form. 

		System.out.println(msgForm);
		Map props = getTextFiledOfForm(msgForm, "hidden");
		
		props.put("message",msg);
		String resp = nt.postPage(actionUrl, props);
		
		
	}

	public void addPublicComment(String reviewId, String msg) {
		// TODO Auto-generated method stub

	}

	private YelpReview getReviewById(String reviewId) {
		for (YelpReview review : this.reviews) {
			if (review.getReviewId().equalsIgnoreCase(reviewId)) {
				return review;
			}
		}
		//Should try to get one from website?
		return null;
	}

}
