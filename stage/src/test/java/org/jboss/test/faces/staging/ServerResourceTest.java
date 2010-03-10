/**
 * 
 */
package org.jboss.test.faces.staging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.jboss.test.faces.staging.ClasspathServerResource;
import org.jboss.test.faces.staging.ServerResource;
import org.jboss.test.faces.staging.ServerResourceDirectoryImpl;
import org.jboss.test.faces.staging.ServerResourcePath;
import org.junit.Test;

/**
 * @author asmirnov
 * 
 */
public class ServerResourceTest {

	private class MockResource implements ServerResource {

		public void addResource(ServerResourcePath path, ServerResource resource) {
		}

		public InputStream getAsStream() throws IOException {
			return null;
		}

		public Set<String> getPaths() {
			return null;
		}

		public ServerResource getResource(ServerResourcePath path) {
			if (null != path && path.hasNextPath()) {
				return this;
			}
			return null;
		}

		public URL getURL() {
			return null;
		}

	}

	/**
	 * Test method for
	 * {@link org.jboss.test.faces.staging.ServerResourceDirectoryImpl#addResource(org.jboss.test.faces.staging.ServerResourcePath, org.jboss.test.faces.staging.ServerResource)}
	 * .
	 */
	@Test
	public void testAddResource() {
		ServerResourceDirectoryImpl root = new ServerResourceDirectoryImpl();
		MockResource webXml = new MockResource();
		root.addResource(ServerResourcePath.WEB_XML, webXml);
		assertEquals(1, root.getPaths().size());
		MockResource facesConfig = new MockResource();
		root.addResource(ServerResourcePath.FACES_CONFIG, facesConfig);
		assertEquals(1, root.getPaths().size());
		ServerResource webInf = root.getResource(ServerResourcePath.WEB_INF);
		assertNotNull(webInf);
		assertEquals(2, webInf.getPaths().size());
		assertSame(webXml, webInf
				.getResource(new ServerResourcePath("/web.xml")));
		assertSame(facesConfig, webInf.getResource(new ServerResourcePath(
				"/faces-config.xml")));
	}
	
	@Test
    public void testAddDirectory() throws Exception {
        ServerResourceDirectoryImpl root = new ServerResourceDirectoryImpl();
        ServerResource directory = root.addDirectory(new ServerResourcePath("/foo/bar"));
        assertSame(directory, root.getResource(new ServerResourcePath("/foo//bar/")));
        
        ServerResource bazDirectory = root.addDirectory(new ServerResourcePath("/foo/bar/baz"));
        assertSame(directory, root.getResource(new ServerResourcePath("/foo//bar/")));
        assertSame(bazDirectory, root.getResource(new ServerResourcePath("/foo//bar/baz")));
    }

	/**
	 * Test method for
	 * {@link org.jboss.test.faces.staging.ServerResourceDirectoryImpl#getResource(org.jboss.test.faces.staging.ServerResourcePath)}
	 * .
	 */
	@Test
	public void testGetResource() {
		ServerResourceDirectoryImpl root = new ServerResourceDirectoryImpl();
		MockResource webXml = new MockResource();
		root.addResource(ServerResourcePath.WEB_XML, webXml);
		ServerResource webInf = root.getResource(ServerResourcePath.WEB_INF);
		assertNotNull(webInf);
		assertNull(root.getResource(new ServerResourcePath("/foo")));
		assertNull(root.getResource(new ServerResourcePath("/foo/baz")));
		assertEquals(1, root.getPaths().size());
		assertNull(root.getResource(new ServerResourcePath(
				"/WEB-INF/web.xml/foo")));
		assertSame(webXml, webInf.getResource(new ServerResourcePath(
				"/web.xml")));
	}

	/**
	 * Test method for
	 * {@link org.jboss.test.faces.staging.ServerResourceDirectoryImpl#getResource(org.jboss.test.faces.staging.ServerResourcePath)}
	 * .
	 */
	@Test
	public void testGetResourceRoot() {
		ServerResourceDirectoryImpl root = new ServerResourceDirectoryImpl();
		MockResource indexXhtml = new MockResource();
		root.addResource(new ServerResourcePath("/index.xhtml"), indexXhtml);
		ServerResource index = root.getResource(new ServerResourcePath("/index.xhtml"));
		assertNotNull(index);
		assertNull(root.getResource(new ServerResourcePath("/foo")));
		assertNull(root.getResource(new ServerResourcePath("/foo/baz")));
		assertEquals(1, root.getPaths().size());
	}
	
	/**
	 * Test method for
	 * {@link org.jboss.test.faces.staging.ServerResourceDirectoryImpl#getAsStream()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetAsStream() throws IOException {
		ClasspathServerResource resource = new ClasspathServerResource(
				"resource.txt");
		InputStream inputStream = resource.getAsStream();
		assertNotNull(inputStream);
		try {
			byte[] buff = new byte[20];
			assertEquals(3, inputStream.read(buff));

		} finally {
			inputStream.close();			
		}
	}


	/**
	 * Test method for
	 * {@link org.jboss.test.faces.staging.ServerResourceDirectoryImpl#getURL()}.
	 */
	@Test
	public void testGetURL() {
		ClasspathServerResource resource = new ClasspathServerResource(
		"resource.txt");
		assertNotNull(resource.getURL());
	}

}
