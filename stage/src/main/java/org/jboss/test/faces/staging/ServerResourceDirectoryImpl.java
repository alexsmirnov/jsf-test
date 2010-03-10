/**
 * 
 */
package org.jboss.test.faces.staging;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;

/**
 * Directory-like resource for a virtual web application content.
 * 
 * @author asmirnov
 * 
 */
public class ServerResourceDirectoryImpl implements ServerResourceDirectory {

    /**
     * Directory content.
     */
    private final DirectoryMap<ServerResource, ServerResourceDirectory> children = 
        new DirectoryMap<ServerResource, ServerResourceDirectory>(this, StagingDirectoryMapAdapter.INSTANCE);

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.staging.ServerResource#getAsStream()
     */
    public InputStream getAsStream() {
        // can't read directory.
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.staging.ServerResource#getPaths()
     */
    public Set<String> getPaths() {
        return children.getResourceNames();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.staging.ServerResource#getURL()
     */
    public URL getURL() {
        // Directory don't have url.
        return null;
    }

    public ServerResourceDirectory addDirectory(ServerResourcePath path) {
        return children.addDirectory(path);
    }

    public ServerResourceDirectory addDirectory(String fileName) {
        return children.addDirectory(fileName);
    }

    public void addResource(ServerResourcePath path, ServerResource resource) {
        children.addResource(path, resource);
    }

    public ServerResource getResource(ServerResourcePath path) {
        return children.getResource(path);
    }

}
