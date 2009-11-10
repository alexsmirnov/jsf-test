/**
 * 
 */
package org.jboss.test.faces.staging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Level;
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
		try {
            return new URL("urn",null,0,"", new URLStreamHandler() {
                
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(u) {
                        
                        @Override
                        public void connect() throws IOException {
                        }
                        
                        @Override
                        public Object getContent() throws IOException {
                            return contentBytes;
                        }
                        
                        @Override
                        public InputStream getInputStream() throws IOException {
                            return getAsStream();
                        }
                    };
                }
            });
        } catch (MalformedURLException e) {
            log.log(Level.WARNING,"Malformed StringContentResourceURL",e);
            return null;
        }
	}

}
