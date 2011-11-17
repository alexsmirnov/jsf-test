package org.jboss.test.faces.mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestMockExternalContext {

    /** The environment. */
    private MockFacesEnvironment environment;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        environment = new MockFacesEnvironment();
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        environment.release();
        environment = null;
    }

    @Test
    public void testExternalContext() {
        environment.withExternalContext();
        assertTrue(environment.getFacesContext().getExternalContext() instanceof ExternalContext);
        assertTrue(FacesContext.getCurrentInstance().getExternalContext() instanceof ExternalContext);
        assertSame(environment.getFacesContext().getExternalContext(), FacesContext.getCurrentInstance().getExternalContext());
    }

    @Test
    public void externalContextShouldBeAbleToBeStubbed() {
        environment.withExternalContext();
        ExternalContext externalContext = environment.getExternalContext();
        Mockito.when(externalContext.getAuthType()).thenReturn("customAuthType");

        assertEquals("customAuthType", FacesContext.getCurrentInstance().getExternalContext().getAuthType());
    }
}
