package org.jboss.test.faces.mock;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.test.faces.mock.Environment.Feature;
import org.jboss.test.faces.mock.component.MockUIData;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MockTestRunner.class)
public class RunnerTest {
    
    @Mock("foo")
    @Environment({Feature.APPLICATION,Feature.EXTERNAL_CONTEXT,Feature.FACTORIES,Feature.RESPONSE_WRITER})
    protected MockFacesEnvironment environment;
    
    @Stub
    protected UIViewRoot viewRoot;
    
    @Mock
    protected MockUIData data;
    
    protected MockController controller;
    
    @Test
    public void testView() throws Exception {
        expect(environment.getFacesContext().getViewRoot()).andReturn(viewRoot);
        expect(viewRoot.getViewId()).andReturn("/foo.xhtml");
        controller.replay();
        assertNotNull(environment.getExternalContext());
        assertNotNull(FactoryFinder.getFactory(FactoryFinder.EXTERNAL_CONTEXT_FACTORY));
        assertSame(viewRoot, environment.getFacesContext().getViewRoot());
        assertEquals("/foo.xhtml", viewRoot.getViewId());
        controller.verify();
    }
    
    @Test
    public void testWriter() throws Exception {
        controller.replay();
        ResponseWriter responseWriter = FacesContext.getCurrentInstance().getResponseWriter();
        responseWriter.startElement("foo", viewRoot);
        responseWriter.writeAttribute("bar", "bar", "bar");
        responseWriter.writeComment("comment");
            responseWriter.startElement("bar", viewRoot);
            responseWriter.writeURIAttribute("href", "http://example.com", "href");
            responseWriter.writeText("text","content");
            responseWriter.endElement("bar");
        responseWriter.endElement("foo");
        controller.verify();
        assertEquals("<foo bar=\"bar\"><!--comment--><bar href=\"http://example.com\">text</bar></foo>", environment.getResponseWriter().toString());
        assertEquals("http://example.com", environment.getResponseWriter().find().element("foo").withAttribute("bar", "bar").element("bar").getAttribute("href"));
    }

    @Test
    public void testCustomMock() throws Exception {
        expect(data.getId()).andReturn("foo");
        controller.replay();
        assertEquals("foo",data.getId());
        controller.verify();
    }
}
