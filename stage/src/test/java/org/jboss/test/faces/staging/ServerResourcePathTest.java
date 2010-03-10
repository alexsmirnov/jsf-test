/**
 * 
 */
package org.jboss.test.faces.staging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jboss.test.faces.staging.ServerResourcePath;
import org.junit.Test;

/**
 * @author asmirnov
 *
 */
public class ServerResourcePathTest {

	/**
	 * Test method for {@link org.jboss.test.faces.staging.ServerResourcePath#ServerResourcePath(java.lang.String)}.
	 */
	@Test
	public void testRootPath() {
		ServerResourcePath path = new ServerResourcePath("/");
		assertNull(path.getFileName());
		assertEquals("/", path.toString());

		assertFalse(path.hasNextPath());
        assertNull(path.getNextPath());
	}

	/**
	 * Test method for {@link org.jboss.test.faces.staging.ServerResourcePath#ServerResourcePath(java.lang.String)}.
	 */
	@Test
	public void testWebInfPath() {
		ServerResourcePath path = ServerResourcePath.WEB_INF;
		assertEquals("WEB-INF", path.getFileName());
		assertEquals("/WEB-INF", path.toString());

		assertFalse(path.hasNextPath());
		path = path.getNextPath();
		assertNull(path);
	}

	/**
	 * Test method for {@link org.jboss.test.faces.staging.ServerResourcePath#ServerResourcePath(java.lang.String)}.
	 */
	@Test
	public void testWebInfTrainingSlashPath() {
		ServerResourcePath path = new ServerResourcePath("/WEB-INF/");
        assertEquals("WEB-INF", path.getFileName());
        assertEquals("/WEB-INF", path.toString());

        assertFalse(path.hasNextPath());
        path = path.getNextPath();
        assertNull(path);
	}

	
	/**
	 * Test method for {@link org.jboss.test.faces.staging.ServerResourcePath#ServerResourcePath(java.lang.String)}.
	 */
	@Test
	public void testWebXmlPath() {
		ServerResourcePath path = ServerResourcePath.WEB_XML;
		assertEquals("WEB-INF", path.getFileName());
		assertEquals("/WEB-INF/web.xml", path.toString());
		
        assertTrue(path.hasNextPath());
		path = path.getNextPath();
		assertNotNull(path);
		
		assertEquals("web.xml", path.getFileName());
		assertEquals("/web.xml", path.toString());
		
        assertFalse(path.hasNextPath());
		path = path.getNextPath();
		assertNull(path);
	}

	@Test
	public void testDirPath() throws Exception {
		ServerResourcePath path = new ServerResourcePath("/foo/bar/");
		
		assertEquals("foo", path.getFileName());
		assertEquals("/foo/bar", path.toString());
		
        assertTrue(path.hasNextPath());
		path = path.getNextPath();
		assertNotNull(path);
		
		assertEquals("bar", path.getFileName());
        assertEquals("/bar", path.toString());

        assertFalse(path.hasNextPath());
        path = path.getNextPath();
        assertNull(path);
	}

    @Test
    public void testFilePath() throws Exception {
        ServerResourcePath path = new ServerResourcePath("/foo/bar/baz.xml");
        
        assertEquals("foo", path.getFileName());
        assertEquals("/foo/bar/baz.xml", path.toString());
        
        assertTrue(path.hasNextPath());
        path = path.getNextPath();
        assertNotNull(path);
        
        assertEquals("bar", path.getFileName());
        assertEquals("/bar/baz.xml", path.toString());

        assertTrue(path.hasNextPath());
        path = path.getNextPath();
        assertNotNull(path);

        assertEquals("baz.xml", path.getFileName());
        assertEquals("/baz.xml", path.toString());
        
        assertFalse(path.hasNextPath());
        path = path.getNextPath();
        assertNull(path);
    }
}
