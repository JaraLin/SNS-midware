package com.sns.midware.resource;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

// The Java class will be hosted at the URI path "/helloworld"
@Path("helloworld")
public class HelloWorldResource {
	Logger log =  Logger.getLogger(getClass());
	// The Java method will process HTTP GET requests
	@GET
	// The Java method will produce content identified by the MIME Media
	// type "text/plain"
	@Produces("text/plain")
	@Path("aa")
	public String getClichedMessage(@QueryParam("username") String userName) {
		// Return some cliched textual content
		log.info("Hello World " + userName);
		return "Hello World " + userName;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("foo")
	public GenericEntity<List<String>> stringlist() {
		List<String> list = Arrays.asList("test", "as");

		return new GenericEntity<List<String>>(list) {
		};
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("getJson")
	public Object postBackJson() {
		JSONObject myObject = new JSONObject();
		try {
			myObject.put("name", "Agamemnon");
			myObject.put("age", 32);
		} catch (JSONException ex) {
//			LOGGER.log(Level.SEVERE, "Error ...", ex);
		}
		return myObject;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("postJson")
	public Object recieveBackJson(JSONObject myObject) {
		return myObject;
	}
}
