package com.sns.midware.config;

import java.util.*;
import javax.ws.rs.core.*;
import com.sun.jersey.api.core.*;

/**
 * Registers URI extensions for some common media types. This lets clients
 * specify the desired response format right in the URI like
 * http://site.com/whatever.xml instead of http://site.com/whatever with an
 * Accept:application/xml header.
 */
public class UriExtensionsConfig extends PackagesResourceConfig {
	private Map<String, MediaType> mediaTypeMap;

	public UriExtensionsConfig() {
		super(); //dd
	}

	public UriExtensionsConfig(Map<String, Object> props) {
		super(props);  //test111
	}

	public UriExtensionsConfig(String[] paths) {
		super(paths);  //test123
	}

	@Override
	public Map<String, MediaType> getMediaTypeMappings() {
		if (mediaTypeMap == null) {
			mediaTypeMap = new HashMap<String, MediaType>();
			mediaTypeMap.put("json", MediaType.APPLICATION_JSON_TYPE);
			mediaTypeMap.put("xml", MediaType.APPLICATION_XML_TYPE);
			mediaTypeMap.put("txt", MediaType.TEXT_PLAIN_TYPE);
			mediaTypeMap.put("html", MediaType.TEXT_HTML_TYPE);
			mediaTypeMap.put("xhtml", MediaType.APPLICATION_XHTML_XML_TYPE);
			MediaType jpeg = new MediaType("image", "jpeg");
			mediaTypeMap.put("jpg", jpeg);
			mediaTypeMap.put("jpeg", jpeg);
			mediaTypeMap.put("zip", new MediaType("application",
					"x-zip-compressed"));
		}
		return mediaTypeMap;
	}
}
