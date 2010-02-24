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

package org.jboss.test.faces.mock;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.createNiceControl;
import static org.easymock.EasyMock.createStrictControl;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.ResponseStateManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.easymock.internal.MocksControl.MockType;
import org.jboss.test.faces.mock.application.MockApplicationFactory;
import org.jboss.test.faces.mock.context.MockExceptionHandlerFactory;
import org.jboss.test.faces.mock.context.MockExternalContext;
import org.jboss.test.faces.mock.context.MockExternalContextFactory;
import org.jboss.test.faces.mock.context.MockFacesContextFactory;
import org.jboss.test.faces.mock.context.MockPartialViewContextFactory;
import org.jboss.test.faces.mock.lifecycle.MockLifecycleFactory;
import org.jboss.test.faces.mock.render.MockRenderKitFactory;

/**
 * <p class="changed_added_4_0">
 * </p>
 * 
 * @author asmirnov@exadel.com
 * 
 */
public class MockFacesEnvironment implements FacesMockController.MockObject {

    private static ThreadLocal<MockFacesEnvironment> instance = new ThreadLocal<MockFacesEnvironment>();

    private final IMocksControl mocksControl;

    private FacesContext facesContext;

    private boolean withFactories = false;

    private ExternalContext externalContext;

    private ServletContext context;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Application application;

    private ViewHandler viewHandler;

    private RenderKit renderKit;

    private ResponseStateManager responseStateManager;

    // Factory methods

    public static MockFacesEnvironment createEnvironment() {
        return new MockFacesEnvironment(createControl());
    }

    public static MockFacesEnvironment createStrictEnvironment() {
        return new MockFacesEnvironment(createStrictControl());
    }

    public static MockFacesEnvironment createNiceEnvironment() {
        return new MockFacesEnvironment(createNiceControl());
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the instance
     */
    public static MockFacesEnvironment getInstance() {
        return instance.get();
    }

    MockFacesEnvironment(IMocksControl mocksControl) {
        this.mocksControl = mocksControl;
        facesContext = createMock(FacesContext.class);
        instance.set(this);
    }

    public <T> T createMock(Class<T> mock) {
        return createMock(null, mock);
    }

    public <T> T createMock(String name, Class<T> mock) {
        return FacesMock.createMock(name, mock, mocksControl);
    }

    /*
     * public MockFacesEnvironment _(){ return this; }
     */
    public MockFacesEnvironment withExternalContext() {
        this.externalContext = createMock(ExternalContext.class);
        recordExternalContext();
        return this;
    }

    private void recordExternalContext() {
        EasyMock.expect(facesContext.getExternalContext()).andStubReturn(externalContext);
        if (withFactories) {
            // TODO -register mock factory.
        }
    }

    public MockFacesEnvironment withServletRequest() {
        if (null == externalContext) {
            withExternalContext();
        }
        this.context = mocksControl.createMock(ServletContext.class);
        this.request = mocksControl.createMock(HttpServletRequest.class);
        this.response = mocksControl.createMock(HttpServletResponse.class);
        recordServletRequest();
        return this;
    }

    private void recordServletRequest() {
        EasyMock.expect(externalContext.getContext()).andStubReturn(context);
        EasyMock.expect(externalContext.getRequest()).andStubReturn(request);
        EasyMock.expect(externalContext.getResponse()).andStubReturn(response);
    }

    public MockFacesEnvironment withFactories() {
        FactoryFinder.releaseFactories();
        FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, MockApplicationFactory.class.getName());
        FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY, MockFacesContextFactory.class.getName());
        FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY, MockRenderKitFactory.class.getName());
        FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, MockLifecycleFactory.class.getName());
//        FactoryFinder.setFactory(FactoryFinder.TAG_HANDLER_DELEGATE_FACTORY, MockType.class.getName());
//        FactoryFinder.setFactory(FactoryFinder.EXCEPTION_HANDLER_FACTORY, MockExceptionHandlerFactory.class.getName());
//        FactoryFinder.setFactory(FactoryFinder.PARTIAL_VIEW_CONTEXT_FACTORY, MockPartialViewContextFactory.class.getName());
//        FactoryFinder.setFactory(FactoryFinder.EXTERNAL_CONTEXT_FACTORY, MockExternalContextFactory.class.getName());
        // TODO - detect JSF version and register additional factories.
        withFactories = true;
        return this;
    }

    public MockFacesEnvironment withApplication() {
        this.application = createMock(Application.class);
        this.viewHandler = createMock(ViewHandler.class);
        recordApplication();
        return this;
    }

    private void recordApplication() {
        EasyMock.expect(facesContext.getApplication()).andStubReturn(application);
        EasyMock.expect(application.getViewHandler()).andStubReturn(viewHandler);
    }

    public MockFacesEnvironment withRenderKit() {
        this.renderKit = createMock(RenderKit.class);
        this.responseStateManager = createMock(ResponseStateManager.class);
        recordRenderKit();
        return this;
    }

    private void recordRenderKit() {
        EasyMock.expect(facesContext.getRenderKit()).andStubReturn(renderKit);
        EasyMock.expect(renderKit.getResponseStateManager()).andStubReturn(responseStateManager);
    }

    public MockFacesEnvironment replay() {
        mocksControl.replay();
        return this;
    }

    public MockFacesEnvironment reset() {
        mocksControl.reset();
        recordEnvironment();
        return this;
    }

    private void recordEnvironment() {
        if(null != externalContext){
            recordExternalContext();
        }
        if(null != request){
            recordServletRequest();
        }
        if (null != application) {
            recordApplication();
        }
        if(null != renderKit){
            recordRenderKit();
        }
        if(withFactories){
            FactoryFinder.releaseFactories();
            withFactories();
        }
    }

    public MockFacesEnvironment resetToStrict() {
        mocksControl.resetToStrict();
        recordEnvironment();
        return this;
    }

    public MockFacesEnvironment resetToDefault() {
        mocksControl.resetToDefault();
        recordEnvironment();
        return this;
    }

    public MockFacesEnvironment resetToNice() {
        mocksControl.resetToNice();
        recordEnvironment();
        return this;
    }

    public void verify() {
        mocksControl.verify();
    }

    public void release() {
        facesContext.release();
        instance.remove();
        if (withFactories) {
            FactoryFinder.releaseFactories();
        }
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the facesContext
     */
    public FacesContext getFacesContext() {
        return this.facesContext;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the externalContext
     */
    public ExternalContext getExternalContext() {
        return this.externalContext;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the context
     */
    public ServletContext getContext() {
        return this.context;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the request
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the response
     */
    public HttpServletResponse getResponse() {
        return this.response;
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

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the viewHandler
     */
    public ViewHandler getViewHandler() {
        return this.viewHandler;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the renderKit
     */
    public RenderKit getRenderKit() {
        return this.renderKit;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the responseStateManager
     */
    public ResponseStateManager getResponseStateManager() {
        return this.responseStateManager;
    }

    public IMocksControl getControl() {
        return mocksControl;
    }
}
