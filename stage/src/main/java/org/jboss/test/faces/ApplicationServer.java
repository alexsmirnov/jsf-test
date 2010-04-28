/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.faces;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.jsp.JspFactory;

import org.jboss.test.faces.staging.HttpConnection;
import org.jboss.test.faces.staging.ServerLogger;
import org.jboss.test.faces.staging.StagingServer;

/**
 * @author Nick Belaevski
 * 
 */
public abstract class ApplicationServer {

    private static final Logger log = ServerLogger.RESOURCE.getLogger();

    public static final String APPLICATION_SERVER_PROPERTY = ApplicationServer.class.getName();

    /**
     * Internal method used by the {@link #addResourcesFromDirectory(String, URL)} to process 'file' protocol.
     * 
     * @param resource
     *            source directory.
     * @param baseDirectory
     *            target virtual directory.
     */
    protected void addResourcesFromFile(String resourcePath, URL resource) {
        File file = new File(resource.getPath());
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        try {
            addFiles(resourcePath, file);
        } catch (MalformedURLException e) {
            throw new TestException(e);
        }
    }

    /**
     * Internal method used by the {@link #addResourcesFromDirectory(String, URL)} to process 'jar' protocol.
     * 
     * @param resource
     *            URL to the any object in the source directory.
     * @param baseDirectory
     *            target virtual directory.
     */
    protected void addResourcesFromJar(String resourcePath, URL resource) {
        try {
            String jarPath = resource.getPath();
            String entry = jarPath.substring(jarPath.indexOf('!') + 2);
            jarPath = jarPath.substring(0, jarPath.indexOf('!'));
            File file = new File(new URI(jarPath));
            ZipFile zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            entry = entry.substring(0, entry.lastIndexOf('/') + 1);
            while (entries.hasMoreElements()) {
                ZipEntry zzz = (ZipEntry) entries.nextElement();
                if (zzz.getName().startsWith(entry) && !zzz.isDirectory()) {
                    String relativePath = zzz.getName().substring(entry.length());
                    URL relativeResource = new URL(resource, relativePath);
                    addResource(resourcePath + "/" + relativePath, relativeResource);
                }
            }

        } catch (IOException e) {
            throw new TestException("Error read Jar content", e);
        } catch (URISyntaxException e) {
            throw new TestException(e);
        }
    }

    /**
     * Internal reccursive method process directory content and all subdirectories.
     * 
     * @param baseDirectory
     * @param file
     * @throws MalformedURLException
     */
    protected void addFiles(String resourcePath, File file) throws MalformedURLException {
        File[] files = file.listFiles();
        for (File subfile : files) {
            if (!subfile.isDirectory()) {
                addResource(resourcePath + "/" + subfile.getName(), subfile.toURI().toURL());
            } else {
                String directoryPath = resourcePath + "/" + subfile.getName();
                addDirectory(directoryPath);
                addFiles(directoryPath, subfile);
            }
        }
    }

    protected abstract void addDirectory(String directoryPath);

    /**
     * Register servlet in this application server
     * 
     * @param servletHolder
     */
    public abstract void addServlet(ServletHolder servletHolder);

    /**
     * Register filter in this application server
     * 
     * @param filterHolder
     */
    public abstract void addFilter(FilterHolder filterHolder);

    /**
     * Add web application init parameter.
     * 
     * @param name
     * @param value
     */
    public abstract void addInitParameter(String name, String value);

    /**
     * Add default mime type for serve files with given extension.
     * 
     * @param extension
     * @param mimeType
     */
    public abstract void addMimeType(String extension, String mimeType);

    public abstract void addContent(String path, String content);

    /**
     * Add java resource to the virtual web application content. This method makes all parent directories as needed.
     * 
     * @param path
     *            path to the file in the virtual web server.
     * @param resource
     *            path to the resource in the classpath, as required by the {@link ClassLoader#getResource(String)}.
     */
    public abstract void addResource(String path, String resource);

    /**
     * Add resource to the virtual veb application content. This method makes all parent directories as needed.
     * 
     * @param path
     *            path to the file in the virtual web server.
     * @param resource
     *            {@code URL} to the file content.
     */
    public abstract void addResource(String path, URL resource);

    /**
     * Add all resources from the directory to the virtual web application content.
     * 
     * @param path
     *            name of the target directory in the virtual web application. If no such directory exists, it will be
     *            created, as well as all parent directories as needed.
     * @param resource
     *            {@code URL} to the source directory or any file in the source directory. Only 'file' or 'jar'
     *            protocols are supported. If this parameter points to a file, it will be converted to a enclosing
     *            directory.
     */
    public void addResourcesFromDirectory(String path, URL resource) {
        String protocol = resource.getProtocol();
        if ("jar".equals(protocol)) {
            addResourcesFromJar(path, resource);
        } else if ("file".equals(protocol)) {
            addResourcesFromFile(path, resource);
        } else {
            throw new TestException("Unsupported protocol " + protocol);
        }
    }

    /**
     * Add all files from the directory to the virtual web application content.
     * 
     * @param path
     *            name of the target directory in the virtual web application. If no such directory exists, it will be
     *            created, as well as all parent directories as needed.
     * @param resource
     *            {@code File} of the source directory or any file in the source directory. If this parameter points to
     *            a file, it will be converted to a enclosing directory.
     */
    public void addResourcesFromDirectory(String path, File directory) {
        if (!directory.exists()) {
            throw new TestException("directory does not exist:" + directory.getAbsolutePath());
        }
        try {
            addFiles(path, directory);
        } catch (MalformedURLException e) {
            throw new TestException(e);
        }
    }

    /**
     * Add web-application wide listenes, same as it is defined by the &lt;listener&gt; element in the web.xml file for
     * a real server. Supported listener types:
     * <ul>
     * <li>{@link ServletContextListener}</li>
     * <li>{@link ServletContextAttributeListener}</li>
     * <li>{@link HttpSessionListener}</li>
     * <li>{@link HttpSessionAttributeListener}</li>
     * <li>{@link ServletRequestListener}</li>
     * <li>{@link ServletRequestAttributeListener}</li>
     * </ul>
     * 
     * @param listener
     *            web listener instance.
     */
    public abstract void addWebListener(EventListener listener);

    /**
     * Virtual server initialization. This method creates instances of the {@link ServletContext}, {@link JspFactory},
     * informs {@link ServletContextListener} ind inits all {@link Filter} and {@link Servlet} instances. It should be
     * called from test setUp method to prepare testing environment.
     */
    public abstract void init();

    /**
     * Stop wirtual server. This method informs {@link ServletContextListener} ind inits all {@link Filter} and
     * {@link Servlet} instances, as well remove all internal objects. It should be called from the testt thearDown
     * method to clean up testing environment.
     * 
     */
    public abstract void destroy();

    /**
     * Get instance of virtual web application context.
     * 
     * @return context instance.
     */
    public abstract ServletContext getContext();

    /**
     * Get port on which server accepts connections
     * 
     * @return
     */
    public abstract int getPort();

    /**
     * Get virtual connection to the given URL. Even thought for an http request to the external servers, only local
     * connection to the virtual server will be created.
     * 
     * @param url
     *            request url.
     * @return local connection to the appropriate servlet in the virtual server.
     * @throws {@link TestException} if no servlet found to process given URL.
     */
    public abstract HttpConnection getConnection(URL url);

    public abstract boolean isSessionPerThread();

    public abstract void setSessionPerThread(boolean sessionPerThread);

    /**
     * Get virtual server session object. Create new one if necessary.
     * 
     * @return instance of the virtual server session.
     */
    public abstract HttpSession getSession();

    /**
     * 
     * Returns the current <code>HttpSession</code> associated with this server or, if there is no current session and
     * <code>create</code> is true, returns a new session. Staging server supports only one session per instance,
     * different clients for the same server instance does not supported.
     * 
     * <p>
     * If <code>create</code> is <code>false</code> and the request has no valid <code>HttpSession</code>, this method
     * returns <code>null</code>.
     * 
     * 
     * @param create
     *            <code>true</code> to create a new session for this request if necessary; <code>false</code> to return
     *            <code>null</code> if there's no current session
     * 
     * 
     * @return the <code>HttpSession</code> associated with this server instance or <code>null</code> if
     *         <code>create</code> is <code>false</code> and the server has no session
     * 
     */
    public abstract HttpSession getSession(boolean create);

    /**
     * Creates implementation of {@link ApplicationServer} according to system property. 
     * If system property is not set, creates default implementation.
     * 
     * @return server or default 
     */
    public static ApplicationServer createApplicationServer() {
        String applicationServerClassName = System.getProperty(APPLICATION_SERVER_PROPERTY);
        if (applicationServerClassName != null) {
            return createApplicationServer(applicationServerClassName);
        }        
        return new StagingServer();
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @param applicationServerClassName
     * @return 
     */
    private static ApplicationServer createApplicationServer(String applicationServerClassName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ApplicationServer.class.getClassLoader();
        }

        Class<?> applicationServer;
        try {
            applicationServer = Class.forName(applicationServerClassName, true, classLoader);
            Class<? extends ApplicationServer> applicationServerClass = 
                applicationServer.asSubclass(ApplicationServer.class);
            
            return applicationServerClass.newInstance();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new TestException(e);
        }
    }
}