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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jboss.test.faces.ApplicationServer;
import org.jboss.test.faces.FilterHolder;
import org.jboss.test.faces.ServletHolder;
import org.jboss.test.faces.TestException;
import org.jboss.test.faces.staging.HttpConnection;
import org.jboss.test.faces.staging.ServerResourcePath;
import org.jboss.test.faces.staging.StaggingJspApplicationContext;
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

    private final class JettyConnection extends HttpConnection {
        private final URL url;
        private String requestContentType;
        private String requestEncoding="UTF-8";
        private String requestBody;
        private final HttpClient client;
        private HttpRequestBase httpClientMethod;
        private HttpResponse response;
        private int statusCode;
        private Map<String,String> requestHeaders = new HashMap<String, String>();

        public JettyConnection(URL url) {
            client = new DefaultHttpClient();
            // TODO set default values for host, port, protocol.
            this.url = url;
        }

        @Override
        public void start() {
            throw new UnsupportedOperationException("Jetty server does not allow in-process requests");
            
        }

        @Override
        public void setRequestContentType(String contentType) {
            this.requestContentType = contentType;
        }

        @Override
        public void setRequestCharacterEncoding(String charset) throws UnsupportedEncodingException {
            this.requestEncoding = charset;
        }

        @Override
        public void setRequestBody(String body) {
            this.requestBody = body;
            
        }

        @Override
        public boolean isStarted() {
            return null != httpClientMethod;
        }

        @Override
        public boolean isFinished() {
            return null != response;
        }

        @Override
        public int getResponseStatus() {
            return statusCode;
        }

        @Override
        public Map<String, String[]> getResponseHeaders() {
            Header[] headers = httpClientMethod.getAllHeaders();
            HashMap<String, String[]> map = new HashMap<String, String[]>(headers.length);
            for (Header header : headers) {
                map.put(header.getName(), new String[]{header.getValue()});
            }
            return map;
        }

        @Override
        public String getResponseContentType() {
            Header responseHeader = httpClientMethod.getFirstHeader("Content-Type");
            return null!=responseHeader?responseHeader.getValue():null;
        }

        @Override
        public long getResponseContentLength() {
            return response.getEntity().getContentLength();
        }

        @Override
        public String getResponseCharacterEncoding() {
            return response.getEntity().getContentEncoding().getName();
        }

        @Override
        public byte[] getResponseBody() {
            try {
                return response.getEntity().getContent().toString().getBytes();
            } catch (IOException e) {
                throw new TestException(e);
            }
        }

        @Override
        public HttpServletResponse getResponse() {
            throw new UnsupportedOperationException("Jetty server does not allow in-process requests");
        }

        @Override
        public HttpServletRequest getRequest() {
            throw new UnsupportedOperationException("Jetty server does not allow in-process requests");
        }

        @Override
        public String getErrorMessage() {
            return response.getStatusLine().getReasonPhrase();
        }


        @Override
        public String getContentAsString() {
            try {
                return convertStreamToString(response.getEntity().getContent());
            } catch (IOException e) {
                throw new TestException(e);
            }
        }

        @Override
        public void finish() {
            if(null != httpClientMethod){
                client.getConnectionManager().shutdown();
            }
            
        }

        @Override
        public void execute() {
            switch (getRequestMethod()) {
                case GET:
                    this.httpClientMethod = new HttpGet(url.toExternalForm());
                    break;
                case POST:
                    HttpPost postMethod = new HttpPost(url.toExternalForm());
                    if(null != requestBody) {
                        BasicHttpEntity body = new BasicHttpEntity();
                        body.setContentType(requestContentType);
                        body.setContentEncoding(requestEncoding);
                        postMethod.setEntity(body);
                    }
                    this.httpClientMethod = postMethod;
                    break;

                default:
                    throw new UnsupportedOperationException("Http Method "+getRequestMethod()+" is not supported");
            }
            HttpParams params = getHttpParams();
            try {
                if (null != params) {
                    httpClientMethod.setParams(params);
                }
                for (Map.Entry<String,String> entry : requestHeaders.entrySet()) {
                    httpClientMethod.addHeader(entry.getKey(), entry.getValue());
                }
                // TODO - add parameters to request.
                this.response = client.execute(httpClientMethod);
                this.statusCode = this.response.getStatusLine().getStatusCode();
            } catch (IOException e) {
                throw new TestException(e);
            }
        }

        private HttpParams getHttpParams() {
            Map<String, String[]> parameterMap = getRequestParameters();
            BasicHttpParams params = null;
            if (!parameterMap.isEmpty()) {
                params = new BasicHttpParams();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    if(null !=entry.getValue()){
                        params.setParameter(entry.getKey(), entry.getValue());
                    }
                }
            }
            return params;
        }

        @Override
        public void addRequestHeaders(Map<String, String> headers) {
            requestHeaders.putAll(headers);
        }

        @Override
        protected String getRequestCharacterEncoding() {
            return requestEncoding;
        }

        
    }

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

    private boolean isClassAvailable(String elFactoryClass)  {
        try {
            Class.forName(elFactoryClass);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void init() {
        server = new Server(port);

        HandlerList handlers = new HandlerList();

        handlers.setHandlers(new Handler[] { webAppContext, new DefaultHandler() });
        server.setHandler(handlers);
        // Try to register EL Expression factory explicitly, because we does not use JSF.
        if(isClassAvailable(StaggingJspApplicationContext.SUN_EXPRESSION_FACTORY)){
            addInitParameter(StaggingJspApplicationContext.FACES_EXPRESSION_FACTORY, StaggingJspApplicationContext.SUN_EXPRESSION_FACTORY);
        } else if(isClassAvailable(StaggingJspApplicationContext.JBOSS_EXPRESSION_FACTORY)){
            addInitParameter(StaggingJspApplicationContext.FACES_EXPRESSION_FACTORY, StaggingJspApplicationContext.JBOSS_EXPRESSION_FACTORY);
        }
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

    @Override
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

    @Override
    @SuppressWarnings("unchecked")
    public void addInitParameter(String name, String value) {
        webAppContext.getInitParams().put(name, value);
    }

    @Override
    public void addWebListener(EventListener listener) {
        webAppContext.addEventListener(listener);
    }

    @Override
    public ServletContext getContext() {
        if (!webAppContext.isStarted()) {
            throw new IllegalStateException("Server should be started before getContext() can be called!");
        }
        return webAppContext.getServletContext();
    }

    @Override
    public void addMimeType(String extension, String mimeType) {
        webAppContext.getMimeTypes().addMimeMapping(extension, mimeType);
    }

    @Override
    public void addContent(String path, String content) {
        serverRoot.addResource(path, new StringContentResource(content, path));
    }

    @Override
    public void addResource(String path, String resource) {
        serverRoot.addResource(path, Resource.newClassPathResource(resource));
    }

    @Override
    public void addResource(String path, URL resource) {
        try {
            serverRoot.addResource(path, Resource.newResource(resource));
        } catch (IOException e) {
            throw new TestException(e.getMessage(), e);
        }
    }

    @Override
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

    @Override
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
    
    @Override
    public int getPort() {
        return port;
    }
    
    @Override
    public HttpConnection getConnection(URL url) {
        return new JettyConnection(url);
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (session == null && create) {
            throw new UnsupportedOperationException("Session creation is not supported by JettyServer");
        }
        
        return session;
    }

    @Override
    public boolean isSessionPerThread() {
        return false;
    }

    @Override
    public void setSessionPerThread(boolean sessionPerThread) {
        //do nothing
    }

    @Override
    protected void addDirectory(String directoryPath) {
        serverRoot.createChildDirectory(new ServerResourcePath(directoryPath));
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
