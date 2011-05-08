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
 * <p class="changed_added_4_0">
 * </p>
 * 
 * @author asmirnov@exadel.com
 * 
 */
public class MockFacesEnvironment {

    private MockFacesContext facesContext;

    private boolean withFactories = false;

    private ExternalContext externalContext;

    private ELContext elContext;

    private ServletContext context;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Application application;

    private ViewHandler viewHandler;

    private RenderKit renderKit;

    private ResponseStateManager responseStateManager;

    private RecordingResponseWriter responseWriter;

    private ApplicationFactory applicationFactory;

    private FacesContextFactory facesContextFactory;
    private RenderKitFactory renderKitFactory;
    private LifecycleFactory lifecycleFactory;
    private TagHandlerDelegateFactory tagHandlerDelegateFactory;
    private ExceptionHandlerFactory exceptionHandlerFactory;
    private PartialViewContextFactory partialViewContextFactory;
    private ExternalContextFactory externalContextFactory;

    private static boolean jsf2;

    static {
        try {
            Class.forName("javax.faces.component.behavior.Behavior", false, FacesContext.class.getClassLoader());
            jsf2 = true;
        } catch (Throwable e) {
            jsf2 = false;
        }
    }
    
    public MockFacesEnvironment() {
        MockFacesContext mockFacesContext = new MockFacesContext();
        facesContext = spy(mockFacesContext);
        mockFacesContext.setCurrentContext(facesContext);
    }

    public MockFacesEnvironment withExternalContext() {
        this.externalContext = mock(ExternalContext.class);
        recordExternalContext();
        return this;
    }

    private void recordExternalContext() {
        when(facesContext.getExternalContext()).thenReturn(externalContext);
    }

    public MockFacesEnvironment withELContext() {
        this.elContext = mock(ELContext.class);
        recordELContext();
        return this;
    }

    private void recordELContext() {
        when(facesContext.getELContext()).thenReturn(elContext);
    }

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

    private void recordServletRequest() {
        when(externalContext.getContext()).thenReturn(context);
        when(externalContext.getRequest()).thenReturn(request);
        when(externalContext.getResponse()).thenReturn(response);
    }

    private FactoryMockingService service = FactoryMockingService.getInstance();

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

    private <T> T setupAndEnhance(Class<T> type) {
        String factoryName = type.getName();
        FactoryMock<T> factoryMock = service.createFactoryMock(type);
        FactoryFinder.setFactory(factoryName, factoryMock.getMockClassName());
        T mock = type.cast(FactoryFinder.getFactory(factoryName));
        service.enhance(factoryMock, mock);
        return mock;
    }

    public MockFacesEnvironment withApplication() {
        this.application = mock(Application.class);
        this.viewHandler = mock(ViewHandler.class);
        recordApplication();
        return this;
    }

    private void recordApplication() {
        when(facesContext.getApplication()).thenReturn(application);
        when(application.getViewHandler()).thenReturn(viewHandler);
    }

    public MockFacesEnvironment withRenderKit() {
        this.renderKit = mock(RenderKit.class);
        this.responseStateManager = mock(ResponseStateManager.class);
        recordRenderKit();
        return this;
    }

    private void recordRenderKit() {
        when(facesContext.getRenderKit()).thenReturn(renderKit);
        when(renderKit.getResponseStateManager()).thenReturn(responseStateManager);
    }

    public MockFacesEnvironment withResponseWriter() {
        this.responseWriter = new RecordingResponseWriter("UTF-8", "text/html");
        recordResponseWriter();
        return this;
    }

    private void recordResponseWriter() {
        when(facesContext.getResponseWriter()).thenReturn(responseWriter);
    }

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

    public MockFacesEnvironment reset() {
        recordEnvironment();
        return this;
    }

    public void release() {
        facesContext.release();
        if (withFactories) {
            FactoryFinder.releaseFactories();
        }
    }

    public FacesContext getFacesContext() {
        return this.facesContext;
    }

    public ExternalContext getExternalContext() {
        return this.externalContext;
    }

    public ELContext getElContext() {
        return this.elContext;
    }

    public ServletContext getServletContext() {
        return this.context;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public Application getApplication() {
        return this.application;
    }

    public ViewHandler getViewHandler() {
        return this.viewHandler;
    }

    public RenderKit getRenderKit() {
        return this.renderKit;
    }

    public ResponseStateManager getResponseStateManager() {
        return this.responseStateManager;
    }

    public RecordingResponseWriter getResponseWriter() {
        return this.responseWriter;
    }

    public ApplicationFactory getApplicationFactory() {
        return applicationFactory;
    }

    public FacesContextFactory getFacesContextFactory() {
        return facesContextFactory;
    }

    public RenderKitFactory getRenderKitFactory() {
        return renderKitFactory;
    }

    public LifecycleFactory getLifecycleFactory() {
        return lifecycleFactory;
    }

    public TagHandlerDelegateFactory getTagHandlerDelegateFactory() {
        return tagHandlerDelegateFactory;
    }

    public ExceptionHandlerFactory getExceptionHandlerFactory() {
        return exceptionHandlerFactory;
    }

    public PartialViewContextFactory getPartialViewContextFactory() {
        return partialViewContextFactory;
    }

    public ExternalContextFactory getExternalContextFactory() {
        return externalContextFactory;
    }
}
