package com.sns;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sns.midware.utils.Netway;

public class YelpTester {
	Netway nt = new Netway(true);
	
	public YelpTester(){
		nt.setTimeout(30);
		Properties header = new Properties();
		header.setProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
		header.setProperty("Content-Type","application/x-www-form-urlencoded");

		header.setProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

		header.setProperty("Host", "biz.yelp.com");
		header.setProperty("Origin", "https://biz.yelp.com");
		header.setProperty("Referer:", "https://biz.yelp.com/");
		
		header.setProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
//		header.setProperty("Accept-Encoding", "gzip,deflate,sdch");
		header.setProperty("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
		header.setProperty("Cache-Control", "max-age=0");
		header.setProperty("Connection", "keep-alive");
		nt.setHeader(header);
		
	}
	
	public void login (String email, String password){
		Map<String,String> loginPros= createLoginProperties("https://biz.yelp.com") ;
		String v =loginPros.get("csrftok");
//		loginPros.clear();
//		loginPros.setProperty("csrftok", v);
		loginPros.put("email", email);
		loginPros.put("password", password);
		
		String url = "https://biz.yelp.com/login";
		
//		nt.retainCookies(new String[]{"bse"});
		
		String resp =nt.postPage(url, loginPros);
		
		nt.getPage("https://biz.yelp.com/r2r/vl8k7RPKnyHyPRO4XUpWCQ/");
		System.out.println(resp);
	}
	
	public static void main(String[] args) {
		YelpTester yt = new YelpTester();

		yt.login("info@chapagrill.net", "hootspa");
		
	}
	
	public  Map createLoginProperties(String authUrl) {
		String resp = nt.getPage(authUrl);

		String loginForm = getByRegex(
				resp,
				"<form action=\"/login\" id=\"login-form\"[\\s\\S]+?>([\\s\\S]+?)</form>");
		
//		<form action="/login" id="login-form" method="POST" name="login_form">
		System.out.println(loginForm);
		Map props = getTextFiledOfForm(loginForm, "hidden");
		return props;
		// loginByUserId(props,"zhangyu0182", "98111223");

	}
	
	public static String getByRegex(String src, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(src);
		while (m.find()) {
			// System.out.println(m.group(1));
			return m.group(1);
		}
		return null;
	}

	private  Map getTextFiledOfForm(String formString, String type) {
		Pattern p = Pattern.compile("<input type=\"" + type
				+ "\"[\\s\\S]*? name=\"([\\S]+?)\"[\\s\\S]+?value=\"([\\s\\S]*?)\">");
		Matcher m = p.matcher(formString);
		HashMap<String,String> props = new LinkedHashMap<String,String>();
		while (m.find()) {
			System.out.println(m.group(1) + "=" + m.group(2));
			props.put(m.group(1), m.group(2));
		}
		return props;
	}
}
