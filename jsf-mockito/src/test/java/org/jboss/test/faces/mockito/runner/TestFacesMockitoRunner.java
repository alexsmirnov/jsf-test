package org.jboss.test.faces.mockito.runner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(FacesMockitoRunner.class)
public class TestFacesMockitoRunner {

    @Inject
    FacesContext facesContext;
    
    @Inject
    Application application;
    
    @Test
    public void testInjectingFacesContext() {
        assertSame(FacesContext.getCurrentInstance(), facesContext);
    }
    
    @Test
    public void testUsingFacesContext() {
        UIViewRoot viewRoot = mock(UIViewRoot.class);
        
        when(facesContext.getViewRoot()).thenReturn(viewRoot);
        
        assertSame(FacesContext.getCurrentInstance().getViewRoot(), viewRoot);
    }
    
    @Test
    public void testInjectingApplication() {
        assertNotNull(application);
    }
}
