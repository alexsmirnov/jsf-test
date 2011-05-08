package org.jboss.test.faces.mockito;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMockFacesContext {

    private MockFacesEnvironment environment;

    @Before
    public void setUp() {
        environment = new MockFacesEnvironment();
    }

    @After
    public void tearDown() {
        environment.release();
        environment = null;
    }

    @Test
    public void testMockingFacesContext() {
        FacesContext facesContext = environment.getFacesContext();

        assertSame(facesContext, FacesContext.getCurrentInstance());
        environment.release();
    }

    @Test
    public void testStubbingFacesContext() {
        FacesContext facesContext = environment.getFacesContext();
        Application application = mock(Application.class);
        when(facesContext.getApplication()).thenReturn(application);

        assertSame(application, FacesContext.getCurrentInstance().getApplication());
        environment.release();
    }

}
