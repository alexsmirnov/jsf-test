/**
 * 
 */
package org.jboss.test.faces.staging;

import java.util.regex.Pattern;

import javax.servlet.ServletContext;

/**
 * Class to represent web server resources directory path.
 * @author asmirnov
 * 
 */
public class ServerResourcePath {
	
	private static final Pattern SLASH = Pattern.compile("/+");

	public static final ServerResourcePath WEB_INF = new ServerResourcePath("/WEB-INF/");

	public static final ServerResourcePath META_INF = new ServerResourcePath("/META-INF/");
	public static final ServerResourcePath WEB_XML = new ServerResourcePath("/WEB-INF/web.xml");
	public static final ServerResourcePath FACES_CONFIG = new ServerResourcePath("/WEB-INF/faces-config.xml");

	private final int pathIndex;
	
	private final String[] pathElements;

	/**
	 * Private constructor for next sub - path.
	 * @param pathElements
	 */
	private ServerResourcePath(String[] pathElements, int pathIndex) {
		this.pathElements = pathElements;
		this.pathIndex = pathIndex;
	}

	/**
	 * Create path from string representation. Path have to started with training slash, as required for
	 * {@link ServletContext#getResource(String)} 
	 * @param path 
	 */
	public ServerResourcePath(String path) {
	    this(splitPath(path), 1);
	}

	private static void checkPath(String path) {
        if (null == path) {
            throw new NullPointerException();
        }
        
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException(path);
        }
	}

    private static String[] splitPath(String path) {
        checkPath(path);
        
        return SLASH.split(path);
    }
	
	/**
	 * Method to detect last element in the path.
	 * @return true for a last element in the path.
	 */
	public boolean hasNextPath() {
		return pathIndex < pathElements.length - 1;
	}

	/**
	 * Name of the next element ( directory or file ) name. 
	 * For the "/foo/bar/baz" it should be "foo/" ,  /bar/baz : "bar/" , "/" : null.
	 * @return name of the next element or null if it is last element in the chain ( file ).
	 */
	public String getFileName() {
	    if (pathIndex < pathElements.length) {
	        return pathElements[pathIndex];
	    } else {
	        return null;
	    }
	}

	/**
	 * Create next path of the path chain.
	 * Path /foo/bar/baz should be converted to /bar/baz , /bar/baz -> /baz -> / ( empty path )
	 * @return next subdirectory path or null.
	 */
	public ServerResourcePath getNextPath() {
	    if (hasNextPath()) {
	        return new ServerResourcePath(pathElements, pathIndex + 1);
	    } else {
	        return null;
	    }
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = pathIndex; i < pathElements.length; i++) {
            str.append("/");
            str.append(pathElements[i]);
        }

		if (str.length() == 0) {
		    str.append("/");
		}
		
		return str.toString();
	}
}
