/**
 * 
 */
package org.jboss.test.faces.htmlunit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.jboss.test.faces.staging.HttpMethod;
import org.jboss.test.faces.staging.StagingConnection;
import org.jboss.test.faces.staging.StagingServer;

import com.gargoylesoftware.htmlunit.FormEncodingType;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * This implementation of the HtmlUnit {@link WebConnection} execute http requests on the local
 * staging server instead of the real network connection. 
 * @author asmirnov
 *
 */
public final class LocalWebConnection implements WebConnection {
	/**
	 * test server instance
	 */
	private final StagingServer localServer;

	/**
	 * @param localServer
	 */
	public LocalWebConnection(StagingServer localServer) {
		this.localServer = localServer;
	}

	/* (non-Javadoc)
	 * @see com.gargoylesoftware.htmlunit.WebConnection#getResponse(com.gargoylesoftware.htmlunit.WebRequestSettings)
	 */
	public WebResponse getResponse(WebRequest request)
			throws IOException {
		StagingConnection connection = localServer.getConnection(request.getUrl());
		// Propagate web request request to the local connection.
		for (NameValuePair param : request.getRequestParameters()) {
			connection.addRequestParameter(param.getName(), param.getValue());
		}
		HttpMethod httpMethod = HttpMethod.valueOf(request.getHttpMethod().toString());
		connection.setRequestMethod(httpMethod);
		connection.setRequestCharacterEncoding(request.getCharset());
		String body = request.getRequestBody();
		String contentType = request.getEncodingType().getName();
		connection.setRequestBody(body);
		connection.setRequestContentType(contentType);
		connection.addRequestHeaders(request.getAdditionalHeaders());
		// HtmlUnit uses request parameters map for the form submit, but does not parse
		// XMLHttpRequest content.
		if(null != body && FormEncodingType.URL_ENCODED.getName().equals(contentType)){
			connection.parseFormParameters(body);
		}
		long startTime = System.currentTimeMillis();
		connection.execute();
	      ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>(10);
	        for (Entry<String, String[]> entry : connection
	                .getResponseHeaders().entrySet()) {
	            for (String value : entry.getValue()) {
	                headers.add(new NameValuePair(entry.getKey(), value));
	            }
	        }
	        long contentLength = connection.getResponseContentLength();
	        if(contentLength>=0){
	            headers.add(new NameValuePair("Content-Length", String.valueOf(contentLength)));
	        }

		WebResponseData responseData = new WebResponseData(connection.getResponseBody(),connection.getResponseStatus(),connection.getErrorMessage(),headers);
		return new WebResponse(responseData,request,System.currentTimeMillis()-startTime);
		
	}

}