package org.jboss.test.faces.mock;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import javax.faces.component.UIViewRoot;

import org.jboss.test.faces.mock.Environment.Feature;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MockTestRunner.class)
public class RunnerTest {
    
    @Mock("foo")
    @Environment({Feature.APPLICATION,Feature.EXTERNAL_CONTEXT})
    protected MockFacesEnvironment environment;
    
    @Stub
    protected UIViewRoot viewRoot;
    
    protected MockController controller;
    
    @Test
    public void testView() throws Exception {
        expect(environment.getFacesContext().getViewRoot()).andReturn(viewRoot);
        expect(viewRoot.getViewId()).andReturn("/foo.xhtml");
        controller.replay();
        assertNotNull(environment.getExternalContext());
        assertSame(viewRoot, environment.getFacesContext().getViewRoot());
        assertEquals("/foo.xhtml", viewRoot.getViewId());
        controller.verify();
    }

}
