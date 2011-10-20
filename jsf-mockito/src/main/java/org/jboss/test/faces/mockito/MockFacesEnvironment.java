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
package org.jboss.test.faces.mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import javax.el.ELContext;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.PartialViewContextFactory;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;
import javax.faces.view.facelets.TagHandlerDelegateFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.test.faces.mockito.factory.FactoryMock;
import org.jboss.test.faces.mockito.factory.FactoryMockingService;
import org.jboss.test.faces.writer.RecordingResponseWriter;

/**
 * <p>
 * Creates and holds the structure of mocks for mocked JSF environment.
 * </p>
 * 
 * <p>
 * When constructed, new mocked {@link FacesContext} is created and set to current context.
 * </p>
 * 
 * @author asmirnov@exadel.com
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 */
public class MockFacesEnvironment {

    /** The faces context. */
    private MockFacesContext facesContext;

    /** The with factories. */
    private boolean withFactories = false;

    /** The external context. */
    private ExternalContext externalContext;

    /** The el context. */
    private ELContext elContext;

    /** The context. */
    private ServletContext context;

    /** The request. */
    private HttpServletRequest request;

    /** The response. */
    private HttpServletResponse response;

    /** The application. */
    private Application application;

    /** The view handler. */
    private ViewHandler viewHandler;

    /** The render kit. */
    private RenderKit renderKit;

    /** The response state manager. */
    private ResponseStateManager responseStateManager;

    /** The response writer. */
    private RecordingResponseWriter responseWriter;

    /** The application factory. */
    private ApplicationFactory applicationFactory;

    /** The faces context factory. */
    private FacesContextFactory facesContextFactory;

    /** The render kit factory. */
    private RenderKitFactory renderKitFactory;

    /** The lifecycle factory. */
    private LifecycleFactory lifecycleFactory;

    /** The tag handler delegate factory. */
    private TagHandlerDelegateFactory tagHandlerDelegateFactory;

    /** The exception handler factory. */
    private ExceptionHandlerFactory exceptionHandlerFactory;

    /** The partial view context factory. */
    private PartialViewContextFactory partialViewContextFactory;

    /** The external context factory. */
    private ExternalContextFactory externalContextFactory;

    /** The jsf2. */
    private static boolean jsf2;

    static {
        try {
            Class.forName("javax.faces.component.behavior.Behavior", false, FacesContext.class.getClassLoader());
            jsf2 = true;
        } catch (Throwable e) {
            jsf2 = false;
        }
    }

    /**
     * Instantiates a new mock faces environment and setup new mocked {@link FacesContext}.
     */
    public MockFacesEnvironment() {
        MockFacesContext mockFacesContext = new MockFacesContext();
        facesContext = spy(mockFacesContext);
        MockFacesContext.setCurrentInstance(facesContext);
        withExternalContext();
    }

    /**
     * With external context.
     * 
     * @return the mock faces environment
     */
    public MockFacesEnvironment withExternalContext() {
        this.externalContext = mock(ExternalContext.class);
        recordExternalContext();
        return this;
    }

    /**
     * Record external context.
     */
    private void recordExternalContext() {
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getApplicationMap()).thenReturn(new HashMap<String, Object>());
    }

    /**
     * With el context.
     * 
     * @return the mock faces environment
     */
    public MockFacesEnvironment withELContext() {
        this.elContext = mock(ELContext.class);
        recordELContext();
        return this;
    }

    /**
     * Record el context.
     */
    private void recordELContext() {
        when(facesContext.getELContext()).thenReturn(elContext);
    }

    /**
     * With servlet request.
     * 
     * @return the mock faces environment
     */
    public MockFacesEnvironment withServletRequest() {
        if (null == externalContext) {
            withExternalContext();
        }
        this.context = mock(ServletContext.class);
        this.request = mock(HttpServletRequest.class);
        this.response = mock(HttpServletResponse.class);
        recordServletRequest();
        return this;
    }

    /**
     * Record servlet request.
     */
    private void recordServletRequest() {
        when(externalContext.getContext()).thenReturn(context);
        when(externalContext.getRequest()).thenReturn(request);
        when(externalContext.getResponse()).thenReturn(response);
    }

    /** The service. */
    private FactoryMockingService service = FactoryMockingService.getInstance();

    /**
     * With factories.
     * 
     * @return the mock faces environment
     */
    public MockFacesEnvironment withFactories() {
        FactoryFinder.releaseFactories();

        applicationFactory = setupAndEnhance(ApplicationFactory.class);
        facesContextFactory = setupAndEnhance(FacesContextFactory.class);
        renderKitFactory = setupAndEnhance(RenderKitFactory.class);
        lifecycleFactory = setupAndEnhance(LifecycleFactory.class);

        if (jsf2) {
            tagHandlerDelegateFactory = setupAndEnhance(TagHandlerDelegateFactory.class);
            exceptionHandlerFactory = setupAndEnhance(ExceptionHandlerFactory.class);
            partialViewContextFactory = setupAndEnhance(PartialViewContextFactory.class);
            externalContextFactory = setupAndEnhance(ExternalContextFactory.class);
        }

        withFactories = true;
        return this;
    }

    /**
     * Setup and enhance.
     * 
     * @param <T>
     *            the generic type
     * @param type
     *            the type
     * @return the t
     */
    private <T> T setupAndEnhance(Class<T> type) {
        String factoryName = type.getName();
        FactoryMock<T> factoryMock = service.createFactoryMock(type);
        FactoryFinder.setFactory(factoryName, factoryMock.getMockClassName());
        T mock = type.cast(FactoryFinder.getFactory(factoryName));
        service.enhance(factoryMock, mock);
        return mock;
    }

    /**
     * With application.
     * 
     * @return the mock faces environment
     */
    public MockFacesEnvironment withApplication() {
        this.application = mock(Application.class);
        this.viewHandler = mock(ViewHandler.class);
        recordApplication();
        return this;
    }

    /**
     * Record application.
     */
    private void recordApplication() {
        when(facesContext.getApplication()).thenReturn(application);
        when(application.getViewHandler()).thenReturn(viewHandler);
    }

    /**
     * With render kit.
     * 
     * @return the mock faces environment
     */
    public MockFacesEnvironment withRenderKit() {
        this.renderKit = mock(RenderKit.class);
        this.responseStateManager = mock(ResponseStateManager.class);
        recordRenderKit();
        return this;
    }

    /**
     * Record render kit.
     */
    private void recordRenderKit() {
        when(facesContext.getRenderKit()).thenReturn(renderKit);
        when(renderKit.getResponseStateManager()).thenReturn(responseStateManager);
    }

    /**
     * With response writer.
     * 
     * @return the mock faces environment
     */
    public MockFacesEnvironment withResponseWriter() {
        this.responseWriter = new RecordingResponseWriter("UTF-8", "text/html");
        recordResponseWriter();
        return this;
    }

    /**
     * Record response writer.
     */
    private void recordResponseWriter() {
        when(facesContext.getResponseWriter()).thenReturn(responseWriter);
    }

    /**
     * Record environment.
     */
    private void recordEnvironment() {
        if (null != externalContext) {
            recordExternalContext();
        }
        if (null != elContext) {
            recordELContext();
        }
        if (null != request) {
            recordServletRequest();
        }
        if (null != application) {
            recordApplication();
        }
        if (null != renderKit) {
            recordRenderKit();
        }
        if (null != responseWriter) {
            recordResponseWriter();
        }
        if (withFactories) {
            withFactories();
        }
    }

    /**
     * Reset.
     * 
     * @return the mock faces environment
     */
    public MockFacesEnvironment reset() {
        recordEnvironment();
        return this;
    }

    /**
     * Release.
     */
    public void release() {
        facesContext.release();
        if (withFactories) {
            FactoryFinder.releaseFactories();
        }
    }

    /**
     * Gets the faces context.
     * 
     * @return the faces context
     */
    public FacesContext getFacesContext() {
        return this.facesContext;
    }

    /**
     * Gets the external context.
     * 
     * @return the external context
     */
    public ExternalContext getExternalContext() {
        return this.externalContext;
    }

    /**
     * Gets the el context.
     * 
     * @return the el context
     */
    public ELContext getElContext() {
        return this.elContext;
    }

    /**
     * Gets the servlet context.
     * 
     * @return the servlet context
     */
    public ServletContext getServletContext() {
        return this.context;
    }

    /**
     * Gets the request.
     * 
     * @return the request
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }

    /**
     * Gets the response.
     * 
     * @return the response
     */
    public HttpServletResponse getResponse() {
        return this.response;
    }

    /**
     * Gets the application.
     * 
     * @return the application
     */
    public Application getApplication() {
        return this.application;
    }

    /**
     * Gets the view handler.
     * 
     * @return the view handler
     */
    public ViewHandler getViewHandler() {
        return this.viewHandler;
    }

    /**
     * Gets the render kit.
     * 
     * @return the render kit
     */
    public RenderKit getRenderKit() {
        return this.renderKit;
    }

    /**
     * Gets the response state manager.
     * 
     * @return the response state manager
     */
    public ResponseStateManager getResponseStateManager() {
        return this.responseStateManager;
    }

    /**
     * Gets the response writer.
     * 
     * @return the response writer
     */
    public RecordingResponseWriter getResponseWriter() {
        return this.responseWriter;
    }

    /**
     * Gets the application factory.
     * 
     * @return the application factory
     */
    public ApplicationFactory getApplicationFactory() {
        return applicationFactory;
    }

    /**
     * Gets the faces context factory.
     * 
     * @return the faces context factory
     */
    public FacesContextFactory getFacesContextFactory() {
        return facesContextFactory;
    }

    /**
     * Gets the render kit factory.
     * 
     * @return the render kit factory
     */
    public RenderKitFactory getRenderKitFactory() {
        return renderKitFactory;
    }

    /**
     * Gets the lifecycle factory.
     * 
     * @return the lifecycle factory
     */
    public LifecycleFactory getLifecycleFactory() {
        return lifecycleFactory;
    }

    /**
     * Gets the tag handler delegate factory.
     * 
     * @return the tag handler delegate factory
     */
    public TagHandlerDelegateFactory getTagHandlerDelegateFactory() {
        return tagHandlerDelegateFactory;
    }

    /**
     * Gets the exception handler factory.
     * 
     * @return the exception handler factory
     */
    public ExceptionHandlerFactory getExceptionHandlerFactory() {
        return exceptionHandlerFactory;
    }

    /**
     * Gets the partial view context factory.
     * 
     * @return the partial view context factory
     */
    public PartialViewContextFactory getPartialViewContextFactory() {
        return partialViewContextFactory;
    }

    /**
     * Gets the external context factory.
     * 
     * @return the external context factory
     */
    public ExternalContextFactory getExternalContextFactory() {
        return externalContextFactory;
    }
}
