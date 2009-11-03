package org.jboss.test.faces.staging;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.test.faces.staging.ServerResourcePath;
import org.jboss.test.faces.staging.ServerResourcesDirectory;
import org.jboss.test.faces.staging.StagingServer;
import org.junit.Test;

public class UrlResourceLoadingTest {

	@Test
	public void testAddFromJar() throws Exception {
		URL resource = this.getClass().getClassLoader().getResource("java/util/Set.class");
		assertNotNull(resource);
		StagingServer server = new StagingServer();
		ServerResourcesDirectory baseDir = new ServerResourcesDirectory();
		server.addResourcesFromJar(resource, baseDir);
		assertNotNull(baseDir.getResource(new ServerResourcePath("/Map.class")));
		assertNotNull(baseDir.getResource(new ServerResourcePath("/concurrent/atomic/AtomicBoolean.class")));
	}

	@Test
	public void testAddFromFile() throws Exception {
		URL resource = this.getClass().getClassLoader().getResource("resource.txt");
		assertNotNull(resource);
		StagingServer server = new StagingServer();
		ServerResourcesDirectory baseDir = new ServerResourcesDirectory();
		server.addResourcesFromFile(resource, baseDir);
		assertNotNull(baseDir.getResource(new ServerResourcePath("/foo.txt")));
		assertNotNull(baseDir.getResource(new ServerResourcePath("/baz/bar.txt")));
	}

	
	@Test
	public void testGetDirectory()  throws Exception {
		URL resource = this.getClass().getClassLoader().getResource("java/util/Set.class");
		assertNotNull(resource);
		StagingServer server = new StagingServer();
		server.addResourcesFromDirectory("/WEB-INF/classes/java/util/", resource);
		try {
		server.init();
		assertNotNull(server.getContext().getResource("/WEB-INF/classes/java/util/Map.class"));
		} finally {
			server.destroy();
		}
		
	}

}
