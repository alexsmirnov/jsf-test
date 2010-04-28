/*
 * $Id$
 *
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.test.faces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventListener;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.LogManager;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.Filter;

import org.jboss.test.faces.staging.HttpConnection;
import org.jboss.test.faces.staging.HttpConnection;

/**
 * <p class="changed_added_4_0">
 * </p>
 * 
 * @author asmirnov@exadel.com
 * 
 */
public class FacesEnvironment {

    public class FacesRequest {
        /**
         * Current virtual connection. This field populated by the {@link #setupWebContent()} method only.
         */
        private HttpConnection connection;

        /**
         * Current {@link FacesContext} instance. This field populated by the {@link #setupWebContent()} method only.
         */
        private FacesContext facesContext;

        private String viewId;

        public FacesRequest start() {
            connection.start();
            FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
                .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            facesContext = facesContextFactory.getFacesContext(facesServer.getContext(), connection.getRequest(),
                connection.getResponse(), lifecycle);
            if (null != viewId) {
                facesContext.setViewRoot(application.getViewHandler().createView(facesContext, viewId));
            }
            return this;
        }

        public byte[] execute() {
            connection.execute();
            return connection.getResponseBody();
        }

        public FacesRequest withViewId(String viewId) {
            this.viewId = viewId;
            return this;
        }

        public FacesRequest withParameter(String name, String value) {
            this.connection.addRequestParameter(name, value);
            return this;
        }

        public void release() {
            if (null != facesContext) {
                facesContext.release();
                facesContext = null;
            }
            if (null != connection) {
                if (!connection.isFinished()) {
                    connection.finish();
                }
                connection = null;
            }
            requests.remove(this);
        }

        /**
         * <p class="changed_added_4_0">
         * </p>
         * 
         * @return the connection
         */
        public HttpConnection getConnection() {
            return this.connection;
        }
    }

    private List<FacesRequest> requests = new CopyOnWriteArrayList<FacesRequest>();

    private ClassLoader contextClassLoader;

    /**
     * Prepared test server instance. Populated by the default {@link #setUp()} method.
     */
    private ApplicationServer facesServer;

    /**
     * JSF {@link Lifecycle} instance. Populated by the default {@link #setUp()} method.
     */
    private Lifecycle lifecycle;

    /**
     * JSF {@link Application} instance. Populated by the default {@link #setUp()} method.
     */
    private Application application;

    private boolean initialized = false;

    private ServletHolder facesServletContainer;

    private FilterHolder filterContainer;

    private String webXmlDefault;

    private File webRoot;

    public FacesEnvironment() {
        this(ApplicationServer.createApplicationServer());
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     */
    public FacesEnvironment(ApplicationServer applicationServer) {
        this.facesServer = applicationServer;
        setupFacesServlet();
        setupFacesListener();
        setupJsfInitParameters();
        setupWebContent();
    }
    
    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the facesServer
     */
    public ApplicationServer getServer() {
        return this.facesServer;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the lifecycle
     */
    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the application
     */
    public Application getApplication() {
        return this.application;
    }

    public FacesEnvironment withFilter(String name, Filter filter) {
        checkNotInitialized();
        filterContainer = new FilterHolder(facesServletContainer.getMapping(), filter);
        filterContainer.setName(name);

        return this;
    }

    public FacesEnvironment withRichFaces() {
        checkNotInitialized();
        try {
            Filter ajaxFilter = createInstance("org.ajax4jsf.Filter");
            withFilter("ajax4jsf", ajaxFilter);
            webXmlDefault = "org/jboss/test/faces/ajax-web.xml";
            return this;
        } catch (ClassNotFoundException e) {
            throw new TestException(e);
        }
    }

    public FacesEnvironment withSeam() {
        checkNotInitialized();
        try {
            Filter ajaxFilter = createInstance("org.jboss.seam.servlet.SeamFilter");
            withFilter("ajax4jsf", ajaxFilter);
            EventListener seamListener = createInstance("org.jboss.seam.servlet.SeamListener");
            facesServer.addWebListener(seamListener);
            webXmlDefault = "org/jboss/test/faces/ajax-web.xml";
            return this;
        } catch (ClassNotFoundException e) {
            throw new TestException(e);
        }
    }

    public FacesEnvironment withWebRoot(File root) {
        checkNotInitialized();
        webRoot = root;
        return this;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param path
     * @param resource
     * @see org.jboss.test.faces.staging.StagingServer#addResource(java.lang.String, java.net.URL)
     */
    public FacesEnvironment withWebRoot(URL root) {
        checkNotInitialized();
        this.facesServer.addResourcesFromDirectory("/", root);
        webRoot = null;
        return this;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param root
     * @return
     */
    public FacesEnvironment withWebRoot(String root) {
        checkNotInitialized();
        return withWebRoot(FacesEnvironment.class.getClassLoader().getResource(root));
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param name
     * @param value
     * @see org.jboss.test.faces.staging.StagingServer#addInitParameter(java.lang.String, java.lang.String)
     */
    public FacesEnvironment withInitParameter(String name, String value) {
        checkNotInitialized();
        this.facesServer.addInitParameter(name, value);
        return this;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param path
     * @param resource
     * @see org.jboss.test.faces.staging.StagingServer#addResource(java.lang.String, java.lang.String)
     */
    public FacesEnvironment withResource(String path, String resource) {
        this.facesServer.addResource(path, resource);
        return this;
    }

    public FacesEnvironment withResource(String path, URL resource) {
        this.facesServer.addResource(path, resource);
        return this;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param path
     * @param resource
     * @see org.jboss.test.faces.staging.StagingServer#addResource(java.lang.String, java.lang.String)
     */
    public FacesEnvironment withContent(String path, String pageContent) {
        this.facesServer.addContent(path, pageContent);
        return this;
    }

    /**
     * Setup staging server instance with JSF implementation. First, this method creates a local test instance and calls
     * the other template method in the next sequence:
     * <ol>
     * <li>{@link #setupFacesServlet()}</li>
     * <li>{@link #setupFacesListener()}</li>
     * <li>{@link #setupJsfInitParameters()}</li>
     * <li>{@link #setupWebContent()}</li>
     * </ol>
     * After them, test server is initialized as well as fields {@link #lifecycle} and {@link #application} populated.
     * Also, if the resource "logging.properties" is exist in the test class package, The Java {@link LogManager} will
     * be configured with its content.
     * 
     * @throws java.lang.Exception
     */
    public FacesEnvironment start() {
        contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        facesServer.addResource("/WEB-INF/web.xml", webXmlDefault);
        if (null != webRoot) {
            facesServer.addResourcesFromDirectory("/", webRoot);
        }

        facesServer.addServlet(facesServletContainer);

        if (filterContainer != null) {
            facesServer.addFilter(filterContainer);
        }

        facesServer.init();
        ApplicationFactory applicationFactory = (ApplicationFactory) FactoryFinder
            .getFactory(FactoryFinder.APPLICATION_FACTORY);
        application = applicationFactory.getApplication();
        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
            .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
        initialized = true;
        return this;
    }

    /**
     * This hook method called from the {@link #setUp()} should append JSF implementation listener to the test server.
     * Default version applends "com.sun.faces.config.ConfigureListener" or
     * "org.apache.myfaces.webapp.StartupServletContextListener" for the existed SUN RI or MyFaces implementation. This
     * metod also calls appropriate {@link #setupSunFaces()} or {@link #setupMyFaces()} methods.
     */
    protected void setupFacesListener() {
        EventListener listener = null;
        try {
            // Check Sun RI configuration listener class.
            listener = createInstance("com.sun.faces.config.ConfigureListener");
            setupSunFaces();
        } catch (ClassNotFoundException e) {
            // No JSF RI listener, check MyFaces.
            try {
                listener = createInstance("org.apache.myfaces.webapp.StartupServletContextListener");
                setupMyFaces();
            } catch (ClassNotFoundException e1) {
                throw new TestException("No JSF listeners have been found", e1);
            }
        }
        facesServer.addWebListener(listener);
    }

    /**
     * This template method called from {@link #setUp()} to create {@link FacesServlet} instance. The default
     * implementation also tests presense of the "org.ajax4jsf.Filter" class. If this class is avalable, these instance
     * appended to the Faces Servlet call chain. Default mapping to the FacesServlet instance is "*.jsf"
     */
    protected void setupFacesServlet() {
        facesServletContainer = new ServletHolder("*.jsf", new FacesServlet());
        facesServletContainer.setName("Faces Servlet");
        webXmlDefault = "org/jboss/test/faces/web.xml";
    }

    /**
     * This template method called from {@link #setUp()} to append appropriate init parameters to the test server. The
     * default implementation sets state saving method to the "server", default jsf page suffix to the ".xhtml" and
     * project stage to UnitTest
     */
    protected void setupJsfInitParameters() {
        facesServer.addInitParameter(StateManager.STATE_SAVING_METHOD_PARAM_NAME,
            StateManager.STATE_SAVING_METHOD_SERVER);
        facesServer.addInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME, ".xhtml");
        // Do not use Jsf 2.0 classes directly because this environment should
        // be applicable for any JSF version.
        facesServer.addInitParameter("javax.faces.PROJECT_STAGE", "UnitTest");
    }

    /**
     * This template method called from the {@link #setupFacesListener()} if MyFaces implementation presents. The
     * default implementation does nothing.
     */
    protected void setupMyFaces() {
        // Do nothing by default.
    }

    /**
     * This template method called from the {@link #setupFacesListener()} if Sun JSF reference implementation presents.
     * The default implementation sets the "com.sun.faces.validateXml" "com.sun.faces.verifyObjects" init parameters to
     * the "true"
     */
    protected void setupSunFaces() {
        facesServer.addInitParameter("com.sun.faces.validateXml", "true");
        facesServer.addInitParameter("com.sun.faces.verifyObjects", "true");
    }

    /**
     * This template method called from the {@link #setUp()} to populate virtual server content. The default
     * implementation tries to load web content from directory pointed by the System property "webroot" or same property
     * from the "/webapp.properties" file.
     */
    protected void setupWebContent() {
        String webappDirectory = System.getProperty("webroot");
        webRoot = null;
        if (null == webappDirectory) {
            URL resource = this.getClass().getResource("/webapp.properties");
            if (null != resource && "file".equals(resource.getProtocol())) {
                Properties webProperties = new Properties();
                try {
                    InputStream inputStream = resource.openStream();
                    webProperties.load(inputStream);
                    inputStream.close();
                    webRoot = new File(resource.getPath());
                    webRoot = new File(webRoot.getParentFile(), webProperties.getProperty("webroot")).getAbsoluteFile();
                } catch (IOException e) {
                    throw new TestException(e);
                }
            }
        } else {
            webRoot = new File(webappDirectory);
        }

    }

    /**
     * Setup virtual server connection to run tests inside JSF lifecycle. The default implementation setups virtual
     * request to the "http://localhost/test.jsf" URL and creates {@link FacesContext} instance. Two template methods
     * are called :
     * <ol>
     * <li>{@link #setupConnection()} to prepare request method, parameters, headers and so</li>
     * <li>{@link #setupView()} to create default view.</li>
     * </ol>
     * 
     * @throws Exception
     */
    public FacesRequest createFacesRequest() throws Exception {
        String url = "http://localhost/test.jsf";
        return createFacesRequest(url).withViewId("/test.xhtml");
    }

    /**
     * <p class="changed_added_2_0">
     * </p>
     * 
     * @param url
     * @throws MalformedURLException
     * @throws FacesException
     */
    public FacesRequest createFacesRequest(String url) throws MalformedURLException, FacesException {
        FacesRequest request = new FacesRequest();
        request.connection = getServer().getConnection(new URL(url));
        requests.add(request);
        return request;
    }

    /**
     * JSF and Virtual server instance cleanup.
     * 
     * @throws java.lang.Exception
     */
    public void release() {
        checkInitialized();
        for (FacesRequest request : this.requests) {
            request.release();
        }
        facesServer.destroy();
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        facesServer = null;
        application = null;
        lifecycle = null;
        initialized = false;
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new TestException("JSF test environment has not been initialized");
        }
    }

    private void checkNotInitialized() {
        if (initialized) {
            throw new TestException("JSF test environment has already been initialized");
        }
    }
    
    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param <T>
     * @param className
     * @return
     * @throws TestException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private <T> T createInstance(String className) throws TestException, ClassNotFoundException {
        try {
            Class<?> clazz = FacesEnvironment.class.getClassLoader().loadClass(className);
            return (T) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TestException(e);
        }
    }

    public static FacesEnvironment createEnvironment() {
        return new FacesEnvironment();
    }

    public static FacesEnvironment createEnvironment(ApplicationServer applicationServer) {
        return new FacesEnvironment(applicationServer);
    }

}
