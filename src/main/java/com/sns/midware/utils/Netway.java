package com.sns.midware.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Netway {
	private static Log log = LogFactory.getLog(Netway.class.getName());
	private HttpClient httpClient = new HttpClient();

	private String serverURL = "";
	
	private boolean isPage=false;
	
	private boolean userWebProxy;
	private String webProxyUrl;

	private Properties defautlHeaders = new Properties();

	public Netway(){
		init();
	}
	private void init() {
//		System.setProperty("use.webproxy","true");
//		System.setProperty("webproxy.url","http://blooming-moon-5662.herokuapp.com/");
		boolean _useWebProxy= Boolean.valueOf(System.getProperty("use.webproxy"));
		String _webProxyServer = System.getProperty("webproxy.url");
		if (StringUtils.isEmpty(_webProxyServer)){
			_useWebProxy=false;
		}
		this.setUserWebProxy(_useWebProxy);
		this.setWebProxyUrl(_webProxyServer);
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
	}

	public Netway (String server){
		this.setServerURL(server);
		init();
	}
	
	public Netway (boolean isPage){
		this.isPage=isPage;
		init();
	}
	
	public void setHeader(Properties head) {
		defautlHeaders = head;
	}

	public String postPage(String page) {
		PostMethod postMethod = createPostMethod(page);
		return this.post(postMethod);
	}
	
	public String getPage(String page){
		return getPage(page, new Properties());
	}	
	public String getPage(String page,Charset charSet){
		return getPage(page, new Properties(), charSet);
	}
	
	

	public String getPage(String page, Properties header) {
		return getPage(page, header, null);
	}
	
	public String getPage(String page, Properties header, Charset charSet) {
		String url = this.getURL(page);
		GetMethod postMethod = new GetMethod(url);
		// defautlHeaders
		Properties h = new Properties();
		if (defautlHeaders != null) {
			h.putAll(defautlHeaders);
		}
		if (header != null) {
			h.putAll(header);
		}
		addHeadProperties(postMethod, h);
		 printCookie();
		log.debug("add head properties end.");
		String post = getResponseAsString(postMethod);
		if (post == null) {
			return "";
		}
		log.debug("get response . size=" + post.length());
		return post;
	}

	private void addHeadProperties(HttpMethod postMethod, Properties h) {
		for (Enumeration e = h.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = h.getProperty(key);
			if (value == null || value.equals("")) {
				continue;
			}
			postMethod.addRequestHeader(key, value);
		}
	}

	private PostMethod createPostMethod(String page) {
		String url = this.getURL(page);
		PostMethod postMethod = new PostMethod(url);
		addHeadProperties(postMethod, this.defautlHeaders);
		return postMethod;
	}

	private String getURL(String page) {
		String url = "";
		if(this.isPage){
			url= page;
		}else{
			url= "http://" + this.serverURL + "/" + page;
		}
		if (this.isUserWebProxy()){
			String encodePage = Encrypt.encrypt(url);
			url = this.getWebProxyUrl() + "?purl=" + encodePage;
		}
		return url;
	}

	
	public String postPage(String page, Map<String,String> pdata) {
		PostMethod postMethod = createPostMethod(page);
		NameValuePair[] data = new NameValuePair[pdata.size()];
		Set keys = pdata.keySet();
		int i = 0;
		for (Iterator<String> iter= keys.iterator();iter.hasNext();) {
			String k = (String) iter.next();
			String v = (String) pdata.get(k);
			data[i] = new NameValuePair(k, v);
			i++;
		}
		postMethod.setRequestBody(data);
		postMethod.getRequestHeaders();
		return this.post(postMethod);
	}
	
	public String postPage(String page, Properties pdata, String body) {
		PostMethod postMethod = createPostMethod(page);
		if (pdata!=null){
			NameValuePair[] data = new NameValuePair[pdata.size()];
			Enumeration keys = pdata.keys();
			int i = 0;
			for (; keys.hasMoreElements();) {
				String k = (String) keys.nextElement();
				String v = (String) pdata.get(k);
				data[i] = new NameValuePair(k, v);
				i++;
			}
			postMethod.setRequestBody(data);
		}
		if (body!=null) {
			try {
				postMethod.setRequestEntity(new StringRequestEntity(body,"text/html","utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return this.post(postMethod);
	}
	
	public void retainCookies(String[] cookieNames) {
		Cookie[] cookies = httpClient.getState().getCookies();
		ArrayList<Cookie> retainCookies = new ArrayList<Cookie>();
		for (Cookie cookie : cookies) {
			if (ArrayUtils.contains(cookieNames, cookie.getName())) {
				retainCookies.add(cookie);
			}
		}
		httpClient.getState().clearCookies();
		httpClient.getState().addCookies(retainCookies.toArray(new Cookie[0]));
	}

	public void removeCookies(String[] cookieNames) {
		Cookie[] cookies = httpClient.getState().getCookies();
		ArrayList<Cookie> retainCookies = new ArrayList<Cookie>();
		for (Cookie cookie : cookies) {
			if (Arrays.binarySearch(cookieNames, cookie.getName()) < 0) {
				retainCookies.add(cookie);
			}
		}
		httpClient.getState().clearCookies();
		httpClient.getState().addCookies(retainCookies.toArray(new Cookie[0]));
	}

	public void addCookie(String domain,String name,String value,String path){
		Cookie cookie =  new Cookie(domain,name,value,path, null, false);
		httpClient.getState().addCookie(cookie);
	}
	
	private String post(PostMethod postMethod) {

		String result = null;
		try {
			log.debug("httpclient send out the request.");
			int statusCode = httpClient.executeMethod(postMethod);
			 printCookie();
			// 301 or 302
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY 
					|| statusCode == HttpStatus.SC_SEE_OTHER
					) {
				Header locationHeader = postMethod
						.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					
					System.out.println("The page was redirected to:" + location);
					location = getRedirectUrl(postMethod);
					System.out.println("The page was redirected to real url:" + location);
					return this.getPage(location);
				} else {
					System.err.println("Location field value is null.");
				}
			}
			log.debug("httpclient request complete.");
			 result = postMethod.getResponseBodyAsString();
//			result = postMethod.getResponseBody();
			log.debug("httpclient retrieve the response as byte");
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		log.debug("httpclient response the result.");
		System.out.println(result);
		return result;

	}
	private void printCookie() {
		Cookie[] cookies = httpClient.getState().getCookies();
		 for (Cookie cookie : cookies) {
			System.out.println( cookie.getName() + "    :" + cookie.getValue()  + ", " + cookie.getDomain());
		}
	}

	private String getRedirectUrl(PostMethod postMethod) {
		Header locationHeader = postMethod
				.getResponseHeader("location");
		String location = locationHeader.getValue();
		
		if (location.toLowerCase().startsWith("http")){
			return location;
		}
		try {
			URI methodUri = postMethod.getURI();
			
			String protocol="http";
			if (methodUri.getURI().toLowerCase().startsWith("https://")){
				protocol="https";
			}
			String rootUrl= protocol + "://" + methodUri.getHost();
			if (location.startsWith("/")){
				//Add the host to the header;
				return rootUrl + location;
			}else if (location.startsWith("../")){
					int count=StringUtils.countMatches(location, "../");
					String urlStr=methodUri.getURI();
					for (int i = 0; i <= count; i++) {
						int index = urlStr.lastIndexOf("/");
						urlStr = urlStr.substring(0, index);
					}
					location = location.replace("../", "");
					return urlStr + "/" + location;
			}else {
				if (location.startsWith("./")) {
					location = location.substring(2);
				}
				location = rootUrl+ methodUri.getCurrentHierPath() +"/" + location;
				return location;
			}
			
		} catch (URIException e) {
			e.printStackTrace();
		}
		
		return location;
		
	}
	public String uploadFile(String page, Properties pdata) {
		String response="";
		try {
			PostMethod mPost = createPostMethod(page);
			this.httpClient.getHttpConnectionManager().getParams()
					.setConnectionTimeout(2 * 60 * 1000);
			httpClient.getParams().setSoTimeout(30 * 60 * 1000);

			Part[] parts = new Part[pdata.size()];
			Enumeration keys = pdata.keys();
			int i = 0;
			for (; keys.hasMoreElements();) {
				String k = (String) keys.nextElement();
				Object v = pdata.get(k);
				if (v instanceof String) {
					parts[i] = new StringPart(k, (String) v);

				} else if (v instanceof File) {
					parts[i] = new FilePart(k, (File) v);
				} else {
					// Do Nothing;
				}
				i++;
			}

			mPost.setRequestEntity(new MultipartRequestEntity(parts, mPost
					.getParams()));

			int statusCode1 = httpClient.executeMethod(mPost);

			response=mPost.getResponseBodyAsString();
			System.out.println("statusLine>>>" + mPost.getStatusLine());
			System.out.println("Body>>>" + response);
			// mPost.releaseConnection();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private String getResponseAsString(HttpMethod postMethod) {
		String result = null;
		try {
			log.debug("httpclient send out the request.");
			int statusCode = httpClient.executeMethod(postMethod);
			// 301 or 302
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = postMethod
						.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					System.out
							.println("The page was redirected to:" + location);
				} else {
					System.err.println("Location field value is null.");
				}
			}
			log.debug("httpclient request complete.");
			// result = postMethod.getResponseBodyAsString();
			result = postMethod.getResponseBodyAsString();
			log.debug("httpclient retrieve the response as byte");
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		log.debug("httpclient response the result.");
		System.out.println(result);
		return result;

	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;

	}

	public String getServerURL() {
		return serverURL;
	}
	
	public void setProxy(String server,int port){
		httpClient.getHostConfiguration().setProxy(server, port);
	}

	public void setTimeout(int sec) {
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(sec * 1000);
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(sec * 1000);
	}
	public boolean isUserWebProxy() {
		return userWebProxy;
	}
	public void setUserWebProxy(boolean userWebProxy) {
		this.userWebProxy = userWebProxy;
	}
	public String getWebProxyUrl() {
		return webProxyUrl;
	}
	public void setWebProxyUrl(String webProxyUrl) {
		this.webProxyUrl = webProxyUrl;
	}
}
