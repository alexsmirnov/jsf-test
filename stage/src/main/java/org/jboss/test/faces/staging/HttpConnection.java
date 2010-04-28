package org.jboss.test.faces.staging;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.test.faces.TestException;

public interface HttpConnection {

    /**
     * Parse 'application/x-www-form-urlencoded' string with parameters name/value pairs,
     * as it expected after a form submit.
     * @param queryString URL query string or POST content.
     */
    public void parseFormParameters(String queryString);

    /**
     * @return the finished
     */
    public boolean isFinished();

    /**
     * @return the started
     */
    public boolean isStarted();

    /**
     * Execute this connection request on the associated servlet or filter chain.
     * @throws TestException if any errors were during execution.
     */
    public void execute();

    /**
     * Finish request to the this connection, inform server listeners about request status.
     */
    public void finish();

    /**
     * Start request to the this connection, inform server listeners about request status.
     * No request parameters changes allowed after connection start.
     */
    public void start();

    /**
     * Set request HTTP methos ( GET, POST etc ).
     * @param method
     *            the method to set
     */
    public void setRequestMethod(HttpMethod method);

    /**
     * Append additional request parameter.
     * @param name
     * @param value
     */
    public void addRequestParameter(String name, String value);

    /**
     * Get content of the response as String. 
     * @return content of the response writer or String created from the ServletOutputStream with current response encoding.
     * @throws TestException
     * 	          if has an unsupported encoding.
     */
    public String getContentAsString();

    /**
     * Get content of the response as byte array.
     * @return content of the ServletOutputStream or convert String, collected by response writer, with current response encoding.
     * @throws TestException
     * 	          if response has unsupported encoding.
     */
    public byte[] getResponseBody();

    /**
     * List of the {@link Cookie} used by the request or response ( There are same cookies for both request and response ). 
     * @return the cookies
     */
    public List<Cookie> getCookies();

    /**
     * @return encoding used to write response.
     */
    public String getResponseCharacterEncoding();

    /**
     * @return content type ( eg 'text/html' ) of the response.
     */
    public String getResponseContentType();

    /**
     * @return HTTP status code of the response.
     */
    public int getResponseStatus();

    /**
     * @return HTTP error message.
     */
    public String getErrorMessage();

    /**
     * Set request Query string. This method does not parse query string, {@link #parseFormParameters(String)} should be used.
     * @param queryString
     *            the queryString to set
     */
    public void setQueryString(String queryString);

    /**
     * Get HTTP response headers.
     * @return headers name-values map.
     */
    public Map<String, String[]> getResponseHeaders();

    /**
     * Set charset for the request body.
     * @param charset
     * @throws UnsupportedEncodingException
     */
    public void setRequestCharacterEncoding(String charset) throws UnsupportedEncodingException;

    /**
     * Set HTTP POST/PUT methods uploading content.
     * @param body
     */
    public void setRequestBody(String body);

    /**
     * Set HTTP request content type ( eg 'application/x-www-form-urlencoded' or 'text/xml' ).
     * @param contentType
     */
    public void setRequestContentType(String contentType);

    public long getResponseContentLength();

    public abstract HttpServletResponse getResponse();

    public abstract HttpServletRequest getRequest();

    public abstract void addRequestHeaders(Map<String, String> headers);

}