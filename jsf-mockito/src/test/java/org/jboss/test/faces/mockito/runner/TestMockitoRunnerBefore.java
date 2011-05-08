package org.jboss.test.faces.mockito.runner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(FacesMockitoRunner.class)
public class TestMockitoRunnerBefore {

    @Inject
    FacesContext facesContext;

    @Mock
    Application application;

    Application applicationCache;

    FacesContext facesContextCache;

    @Before
    public void setUp() {
        assertNotNull(application);
        assertNotNull(facesContext);
        when(facesContext.getApplication()).thenReturn(application);
    }

    @Test
    public void testInjection() {
        assertSame(application, facesContext.getApplication());
        applicationCache = application;
        facesContextCache = facesContext;
    }

    @Test
    public void testEachTestHasOwnEnvironment() {
        assertSame(application, facesContext.getApplication());
        assertNotSame(application, applicationCache);
        assertNotSame(application, facesContextCache);
    }
}
