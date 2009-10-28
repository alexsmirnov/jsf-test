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
import org.jboss.test.faces.mock.FacesMockController.MockObject;

/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class MockFacesEnvironment implements FacesMockController.MockObject {

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

    MockFacesEnvironment(IMocksControl mocksControl) {
        this.mocksControl = mocksControl;
        facesContext = createMock(FacesContext.class);
    }

    public <T>  T createMock(Class<T> mock) {
            return FacesMock.createMock(mock, mocksControl);
    }

/*
    public MockFacesEnvironment _(){
        return this;
    }
*/    
    public MockFacesEnvironment withExternalContext(){
        this.externalContext = createMock(ExternalContext.class);
        EasyMock.expect(facesContext.getExternalContext()).andStubReturn(externalContext);
        if(withFactories){
            // TODO -register mock factory.
        }
        return this;
    }

    public MockFacesEnvironment withServletRequest(){
        if(null == externalContext){
            withExternalContext();
        }
        this.context = mocksControl.createMock(ServletContext.class);
        EasyMock.expect(externalContext.getContext()).andStubReturn(context);
        this.request = mocksControl.createMock(HttpServletRequest.class);
        EasyMock.expect(externalContext.getRequest()).andStubReturn(request);
        this.response = mocksControl.createMock(HttpServletResponse.class);
        EasyMock.expect(externalContext.getResponse()).andStubReturn(response);
        return this;
    }

    public MockFacesEnvironment withFactories(){
        FactoryFinder.releaseFactories();
        // TODO - register FacesContext factory.
        withFactories = true;
        return this;
    }

    public MockFacesEnvironment withApplication(){
        this.application = createMock(Application.class);
        EasyMock.expect(facesContext.getApplication()).andStubReturn(application);
        this.viewHandler = createMock(ViewHandler.class);
        EasyMock.expect(application.getViewHandler()).andStubReturn(viewHandler);
        return this;
    }

    public MockFacesEnvironment withRenderKit(){
        this.renderKit = createMock(RenderKit.class);
        EasyMock.expect(facesContext.getRenderKit()).andStubReturn(renderKit);
        this.responseStateManager = createMock(ResponseStateManager.class);
        EasyMock.expect(renderKit.getResponseStateManager()).andStubReturn(responseStateManager);
        return this;
    }

    public MockFacesEnvironment replay() {
        mocksControl.replay();
        return this;
    }
    
    public MockFacesEnvironment reset() {
        mocksControl.reset();
        return this;
    }

    public void verify() {
        mocksControl.verify();
    }

    
    public void release() {
        facesContext.release();
        if(withFactories){
            FactoryFinder.releaseFactories();
        }
    }
    /**
     * <p class="changed_added_4_0"></p>
     * @return the facesContext
     */
    public FacesContext getFacesContext() {
        return this.facesContext;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the externalContext
     */
    public ExternalContext getExternalContext() {
        return this.externalContext;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the context
     */
    public ServletContext getContext() {
        return this.context;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the request
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the response
     */
    public HttpServletResponse getResponse() {
        return this.response;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the application
     */
    public Application getApplication() {
        return this.application;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the viewHandler
     */
    public ViewHandler getViewHandler() {
        return this.viewHandler;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the renderKit
     */
    public RenderKit getRenderKit() {
        return this.renderKit;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the responseStateManager
     */
    public ResponseStateManager getResponseStateManager() {
        return this.responseStateManager;
    }

    public IMocksControl getControl() {
        return mocksControl;
    }
}