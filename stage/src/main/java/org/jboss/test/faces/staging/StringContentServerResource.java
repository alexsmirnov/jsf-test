/**
 * 
 */
package org.jboss.test.faces.staging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;


/**
 * This class represents resource with known content.
 * 
 * @author asmirnov
 */
public class StringContentServerResource extends AbstractServerResource {

	/**
	 * 
	 */
	private final byte[] contentBytes;

	private static final Logger log = ServerLogger.RESOURCE.getLogger();

	/**
	 * @param name
	 * @param classpath
	 */
	public StringContentServerResource(String content) {
		this.contentBytes = content.getBytes();
	}

	/* (non-Javadoc)
	 * @see org.jboss.test.faces.staging.AbstractServerResource#getAsStream()
	 */
	@Override
	public InputStream getAsStream() throws IOException {
		return new ByteArrayInputStream(contentBytes);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.test.faces.staging.ServerResource#getURL()
	 */
	public URL getURL() {
		// TODO Auto-generated method stub
		return null;
	}

}
