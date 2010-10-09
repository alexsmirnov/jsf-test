package org.jboss.test.faces.mock.component;

import static org.junit.Assert.*;

import javax.faces.component.UIViewRoot;

import org.jboss.test.faces.mock.FacesMock;
import org.junit.Test;

public class ViewBuilderTest {

    private static final String FOO_XML = "/foo.xml";

    @Test
    public void testCreateView() {
        UIViewRoot component = ViewBuilder.createView().setViewId(FOO_XML).getComponent();
        assertTrue(component instanceof MockUIViewRoot);
        checkViewId(component);
    }

    private void checkViewId(UIViewRoot component) {
        FacesMock.replay(component);
        assertEquals(FOO_XML, component.getViewId());
        FacesMock.verify(component);
    }

    @Test
    public void testCreateViewUIViewRoot() {
        UIViewRoot viewRoot = FacesMock.createMock(UIViewRoot.class);
        ViewBuilder viewBuilder = ViewBuilder.createView(viewRoot).setViewId(FOO_XML);
        assertSame(viewRoot,viewBuilder.getComponent());
        checkViewId(viewRoot);
    }

}
