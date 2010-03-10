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
package org.jboss.test.faces.jetty;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventListener;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.test.faces.ApplicationServer;
import org.jboss.test.faces.FilterHolder;
import org.jboss.test.faces.ServletHolder;
import org.jboss.test.faces.TestException;
import org.jboss.test.faces.staging.ServerResourcePath;
import org.jboss.test.faces.staging.StagingConnection;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.Resource;

/**
 * @author Nick Belaevski
 * 
 */
public class JettyServer extends ApplicationServer {

    private static final String WEB_XML = "/WEB-INF/web.xml";

    private static final String DUMMY_WEB_XML = "org/jboss/test/faces/jetty/dummy/web.xml";
    
    private static final int DEFAULT_PORT = 8880;

    private int port;

    private Server server;

    private WebAppContext webAppContext;

    private VirtualDirectoryResource serverRoot = new VirtualDirectoryResource("");

    private HttpSession session;

    public JettyServer() {
        this(DEFAULT_PORT);
    }

    public JettyServer(int port) {
        this.port = port;
        createContext();
    }

    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        
        return classLoader;
    }
    
    private void createContext() {
        webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setBaseResource(serverRoot);
        webAppContext.setClassLoader(getClassLoader());
        
        org.mortbay.jetty.servlet.ServletHolder defaultServletHolder = 
            new org.mortbay.jetty.servlet.ServletHolder(new DefaultServlet());
        //defaultServletHolder.setInitParameter("aliases", Boolean.FALSE.toString());

        webAppContext.addServlet(defaultServletHolder, "/");
        
        webAppContext.addEventListener(new HttpSessionListener() {
            
            public void sessionDestroyed(HttpSessionEvent se) {
                session = null;
            }
            
            public void sessionCreated(HttpSessionEvent se) {
                session = se.getSession();
            }
        });
    }

    public void init() {
        server = new Server(port);

        HandlerList handlers = new HandlerList();

        handlers.setHandlers(new Handler[] { webAppContext, new DefaultHandler() });
        server.setHandler(handlers);

        //JSF initialization listener requires web.xml file, so add dummy web.xml if none was registered
        Resource webXml;
        try {
            webXml = webAppContext.getResource(WEB_XML);
            if (webXml == null || !webXml.exists()) {
                URL dummyWebXml = webAppContext.getClassLoader().getResource(DUMMY_WEB_XML);
                addResource(WEB_XML, dummyWebXml);
            }
        } catch (MalformedURLException e1) {
            //ignore
        }
        
        try {
            server.start();
            server.setStopAtShutdown(true);
        } catch (Exception e) {
            throw new TestException(e.getMessage(), e);
        }
    }

    public void destroy() {
        try {
            server.stop();
            server.setStopAtShutdown(false);
            server.destroy();
        } catch (Exception e) {
            throw new TestException(e.getMessage(), e);
        } finally {
            session = null;
            server = null;
        }
    }

    @SuppressWarnings("unchecked")
    public void addInitParameter(String name, String value) {
        webAppContext.getInitParams().put(name, value);
    }

    public void addWebListener(EventListener listener) {
        webAppContext.addEventListener(listener);
    }

    public ServletContext getContext() {
        if (!webAppContext.isStarted()) {
            throw new IllegalStateException("Server should be started before getContext() can be called!");
        }
        return webAppContext.getServletContext();
    }

    public void addMimeType(String extension, String mimeType) {
        webAppContext.getMimeTypes().addMimeMapping(extension, mimeType);
    }

    public void addContent(String path, String content) {
        serverRoot.addResource(path, new StringContentResource(content, path));
    }

    public void addResource(String path, String resource) {
        serverRoot.addResource(path, Resource.newClassPathResource(resource));
    }

    public void addResource(String path, URL resource) {
        try {
            serverRoot.addResource(path, Resource.newResource(resource));
        } catch (IOException e) {
            throw new TestException(e.getMessage(), e);
        }
    }

    public void addFilter(FilterHolder filerHolder) {
        Map<String, String> initParameters = filerHolder.getInitParameters();
        String mapping = filerHolder.getMapping();
        String name = filerHolder.getName();
        Filter filter = filerHolder.getFilter();

        org.mortbay.jetty.servlet.FilterHolder jettyFilterHolder = new org.mortbay.jetty.servlet.FilterHolder(filter);
        jettyFilterHolder.setName(name);
        jettyFilterHolder.setInitParameters(initParameters);

        webAppContext.addFilter(jettyFilterHolder, mapping, Handler.ALL);
    }

    public void addServlet(ServletHolder servletHolder) {
        Map<String, String> initParameters = servletHolder.getInitParameters();
        String mapping = servletHolder.getMapping();
        String name = servletHolder.getName();
        Servlet servlet = servletHolder.getServlet();

        org.mortbay.jetty.servlet.ServletHolder jettyServletHolder = new org.mortbay.jetty.servlet.ServletHolder(
            servlet);
        jettyServletHolder.setName(name);
        jettyServletHolder.setInitParameters(initParameters);

        webAppContext.addServlet(jettyServletHolder, mapping);
    }
    
    public int getPort() {
        return port;
    }
    
    public StagingConnection getConnection(URL url) {
        throw new UnsupportedOperationException();
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public HttpSession getSession(boolean create) {
        if (session == null && create) {
            throw new UnsupportedOperationException("Session creation is not supported by JettyServer");
        }
        
        return session;
    }

    public boolean isSessionPerThread() {
        return false;
    }

    public void setSessionPerThread(boolean sessionPerThread) {
        //do nothing
    }

    @Override
    protected void addDirectory(String directoryPath) {
        serverRoot.createChildDirectory(new ServerResourcePath(directoryPath));
    }
}
