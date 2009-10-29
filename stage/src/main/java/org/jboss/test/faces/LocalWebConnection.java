/**
 * 
 */
package org.jboss.test.faces;

import java.io.IOException;

import org.apache.commons.httpclient.NameValuePair;
import org.jboss.test.faces.staging.HttpMethod;
import org.jboss.test.faces.staging.StagingConnection;
import org.jboss.test.faces.staging.StagingServer;

import com.gargoylesoftware.htmlunit.FormEncodingType;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;

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
	public WebResponse getResponse(WebRequestSettings settings)
			throws IOException {
		StagingConnection connection = localServer.getConnection(settings.getUrl());
		// Propagate web request settings to the local connection.
		for (NameValuePair param : settings.getRequestParameters()) {
			connection.addRequestParameter(param.getName(), param.getValue());
		}
		HttpMethod httpMethod = HttpMethod.valueOf(settings.getHttpMethod().toString());
		connection.setRequestMethod(httpMethod);
		connection.setRequestCharacterEncoding(settings.getCharset());
		String body = settings.getRequestBody();
		String contentType = settings.getEncodingType().getName();
		connection.setRequestBody(body);
		connection.setRequestContentType(contentType);
		connection.addRequestHeaders(settings.getAdditionalHeaders());
		// HtmlUnit uses request parameters map for the form submit, but does not parse
		// XMLHttpRequest content.
		if(null != body && FormEncodingType.URL_ENCODED.getName().equals(contentType)){
			connection.parseFormParameters(body);
		}
		long startTime = System.currentTimeMillis();
		connection.execute();
		return new LocalWebResponse(settings,connection,System.currentTimeMillis()-startTime);
	}
}