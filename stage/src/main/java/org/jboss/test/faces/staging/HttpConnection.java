package org.jboss.test.faces.staging;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.test.faces.TestException;

public abstract class HttpConnection {

    private final Map<String, String[]> requestParameters = new HashMap<String, String[]>();
    private HttpMethod method = HttpMethod.GET;

    /**
     * Parse 'application/x-www-form-urlencoded' string with parameters name/value pairs,
     * as it expected after a form submit.
     * @param queryString URL query string or POST content.
     */
    /* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#parseFormParameters(java.lang.String)
     */
    public void parseFormParameters(String queryString) {
        String[] queryParams = queryString.split("&");
        for (int i = 0; i < queryParams.length; i++) {
                String par = queryParams[i];
                int eqIndex = par.indexOf('=');
                if (eqIndex >= 0) {
                    // decode url-decoded values.
                    String name = decode(par.substring(0, eqIndex));
                    String value = decode(
                            par.substring(eqIndex + 1));
                    addRequestParameter(name, value);
                } else {
                    addRequestParameter(decode(par), null);
                }
        }
    }
    
    protected String decode(String value){
        if(null != getRequestCharacterEncoding()){
        try {
            return URLDecoder.decode(value,
                getRequestCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return URLDecoder.decode(value);
        }
        } else {
            return URLDecoder.decode(value);
        }
    }

    protected abstract String getRequestCharacterEncoding();

    /**
     * @return the finished
     */
    public abstract boolean isFinished();

    /**
     * @return the started
     */
    public abstract boolean isStarted();

    /**
     * Execute this connection request on the associated servlet or filter chain.
     * @throws TestException if any errors were during execution.
     */
    public abstract void execute();

    /**
     * Finish request to the this connection, inform server listeners about request status.
     */
    public abstract void finish();

    /**
     * Start request to the this connection, inform server listeners about request status.
     * No request parameters changes allowed after connection start.
     */
    public abstract void start();

    /**
     * Get content of the response as String. 
     * @return content of the response writer or String created from the ServletOutputStream with current response encoding.
     * @throws TestException
     * 	          if has an unsupported encoding.
     */
    public abstract String getContentAsString();

    /**
     * Get content of the response as byte array.
     * @return content of the ServletOutputStream or convert String, collected by response writer, with current response encoding.
     * @throws TestException
     * 	          if response has unsupported encoding.
     */
    public abstract byte[] getResponseBody();

    /**
     * @return encoding used to write response.
     */
    public abstract String getResponseCharacterEncoding();

    /**
     * @return content type ( eg 'text/html' ) of the response.
     */
    public abstract String getResponseContentType();

    /**
     * @return HTTP status code of the response.
     */
    public abstract int getResponseStatus();

    /**
     * @return HTTP error message.
     */
    public abstract String getErrorMessage();

    /**
     * Get HTTP response headers.
     * @return headers name-values map.
     */
    public abstract Map<String, String[]> getResponseHeaders();

    /**
     * Set charset for the request body.
     * @param charset
     * @throws UnsupportedEncodingException
     */
    public abstract void setRequestCharacterEncoding(String charset) throws UnsupportedEncodingException;

    /**
     * Set HTTP POST/PUT methods uploading content.
     * @param body
     */
    public abstract void setRequestBody(String body);

    /**
     * Set HTTP request content type ( eg 'application/x-www-form-urlencoded' or 'text/xml' ).
     * @param contentType
     */
    public abstract void setRequestContentType(String contentType);

    public abstract long getResponseContentLength();

    public abstract HttpServletResponse getResponse();

    public abstract HttpServletRequest getRequest();

    public abstract void addRequestHeaders(Map<String, String> headers);

    public void addRequestParameter(String name, String value) {
    //		checkNotStarted();
    		String[] values = getRequestParameters().get(name);
    		if (null == values) {
    			values = new String[1];
    		} else {
    			String[] newValues = new String[values.length + 1];
    			System.arraycopy(values, 0, newValues, 0, values.length);
    			values = newValues;
    		}
    		values[values.length - 1] = value;
    		getRequestParameters().put(name, values);
    	}

    protected String getRequestQueryString() {
        StringBuilder queryString = new StringBuilder();
    	for (Map.Entry<String, String[]> entry : getRequestParameters().entrySet()) {
            if(null !=entry.getValue()){
                for (String value : entry.getValue()) {
                    if(queryString.length()>0){
                        queryString.append("&");
                    }
                    queryString.append(encode(entry.getKey()));
                    if(null != value){
                        queryString.append("=").append(encode(value));
                    }
                }
            }
        };
        return queryString.length()>0?queryString.toString():null;
    }

    private String encode(String entry) {
        try {
            return URLEncoder.encode(entry, getRequestCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return URLEncoder.encode(entry);
        }
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the requestParameters
     */
    protected Map<String, String[]> getRequestParameters() {
        return requestParameters;
    }

    /**
     * Get request HTTP methos ( GET, POST etc ).
     * @return the method
     */
    public HttpMethod getRequestMethod() {
    	return method;
    }

    public void setRequestMethod(HttpMethod method) {
    //		checkNotStarted();
    		this.method = method;
    	}

}