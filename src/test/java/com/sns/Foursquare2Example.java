package com.sns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.log4j.lf5.util.StreamUtils;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

import com.sns.midware.utils.Netway;
//steven's accessToken: LYDFHMUQQ43LNWAQLIQGQGOGANQQVEH5DDTUOX4YGUCGG1CN
//my accessToken=HHWPUBM2UBI0FCCAECZ2FCO253OLF5WCZGQDSXWWCUDIPIBR
public class Foursquare2Example
{
  private static final String PROTECTED_RESOURCE_URL = "https://api.foursquare.com/v2/lists/self/tips?v=20121104&oauth_token=";
  private static final Token EMPTY_TOKEN = null;

  static String apiKey = "BFMTKX0JQ5WSRBCT4NLIXLYTIOV41J52FTTZVWPBZUIY421F";
  static String apiSecret = "Y3SJYFRE5V2OW0YJLFGXVCF34YF2CMNDBMKIMY5MKFRUPQX1";
  public static void main(String[] args)
  {
//    testOAuthFlow();
//    requestViaAccessToken("HHWPUBM2UBI0FCCAECZ2FCO253OLF5WCZGQDSXWWCUDIPIBR");
    
    addPostTo("HHWPUBM2UBI0FCCAECZ2FCO253OLF5WCZGQDSXWWCUDIPIBR","509866ece4b01bdce5efb284");
  }

private static void testOAuthFlow() {
	// Replace these with your own api key and secret
    OAuthService service = new ServiceBuilder()
                                  .provider(Foursquare2Api.class)
                                  .apiKey(apiKey)
                                  .apiSecret(apiSecret)
                                  .callback("http://mybeautiful.iteye.com")
                                  .build();
    Scanner in = new Scanner(System.in);

    System.out.println("=== Foursquare2's OAuth Workflow ===");
    System.out.println();

    // Obtain the Authorization URL
    System.out.println("Fetching the Authorization URL...");
    String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
    System.out.println("Got the Authorization URL!");
    System.out.println("Now go and authorize Scribe here:");
    System.out.println(authorizationUrl);
    System.out.println("And paste the authorization code here");
    System.out.print(">>");
    Verifier verifier = new Verifier(in.nextLine());
    System.out.println();
    
    // Trade the Request Token and Verfier for the Access Token
    System.out.println("Trading the Request Token for an Access Token...");
    Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
    System.out.println("Got the Access Token!");
    System.out.println("(if your curious it looks like this: " + accessToken + " )");
    System.out.println();

    // Now let's go and ask for a protected resource!
    System.out.println("Now we're going to access a protected resource...");
    OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL + accessToken.getToken());
    service.signRequest(accessToken, request);
    Response response = request.send();
    System.out.println("Got it! Lets see what we found...");
    System.out.println();
    System.out.println(response.getCode());
    System.out.println(response.getBody());

    System.out.println();
    System.out.println("Thats it man! Go and build something awesome with Scribe! :)");
}
  
  private static void requestViaAccessToken(String accessTokenStr){
	    OAuthService service = new ServiceBuilder()
        .provider(Foursquare2Api.class)
        .apiKey(apiKey)
        .apiSecret(apiSecret)
        .callback("http://mybeautiful.iteye.com")
        .build();
	    
	  Token accessToken =new Token(accessTokenStr, null);
	  OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL + accessToken.getToken());
	    service.signRequest(accessToken, request);
	    Response response = request.send();
	    System.out.println("Got it! Lets see what we found...");
	    System.out.println();
	    System.out.println(response.getCode());
	    System.out.println(response.getBody());

	    System.out.println();
	    System.out.println("Thats it man! Go and build something awesome with Scribe! :)");

  }
  
  private static void addPostTo(String accessTokenStr,String id){
	  Token accessToken =new Token(accessTokenStr, null);
	  String url="https://api.foursquare.com/v2/photos/add?v=20121104&oauth_token=";
	  url += accessToken.getToken();
	  Netway nt = new Netway(true);
	  
	  Properties pdata = new Properties();
	  pdata.put("tipId", id);
	  pdata.put("postText", "This is a good place..");
	  pdata.put("photo", new File("C:/Users/Cyber/Desktop/images/add.jpg"));
	  
	  nt.uploadFile(url, pdata);
		
		
	    OAuthService service = new ServiceBuilder()
        .provider(Foursquare2Api.class)
        .apiKey(apiKey)
        .apiSecret(apiSecret)
        .callback("http://mybeautiful.iteye.com")
        .build();
	    
	  OAuthRequest request = new OAuthRequest(Verb.POST, url + accessToken.getToken());
	  byte[] payload=loadPhoto("C:/Users/Cyber/Desktop/images/add.jpg");
	request.addPayload(payload);
	request.addHeader("Content-Type", "image/jpeg");
	
	request.addBodyParameter("tipId", id);
	request.addBodyParameter("photo", "");
	    service.signRequest(accessToken, request);
	    Response response = request.send();
	    System.out.println("Got it! Lets see what we found...");
	    System.out.println();
	    System.out.println(response.getCode());
	    System.out.println(response.getBody());

	    System.out.println();
	    System.out.println("Thats it man! Go and build something awesome with Scribe! :)");
	  
  }

private static byte[] loadPhoto(String imageName) {
	try {
		InputStream is = new FileInputStream(imageName);
		return StreamUtils.getBytes(is);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
}