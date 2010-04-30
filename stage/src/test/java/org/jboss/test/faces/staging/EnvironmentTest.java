package org.jboss.test.faces.staging;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

import javax.faces.render.ResponseStateManager;

import org.jboss.test.faces.FacesEnvironment;
import org.jboss.test.faces.Threads;
import org.jboss.test.faces.ThreadsRule;
import org.jboss.test.faces.FacesEnvironment.FacesRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class EnvironmentTest {
    
    private FacesEnvironment environment;
    
    @Rule
    public ThreadsRule threadRule = new ThreadsRule();
    
    @Before
    public void setUp() {
        environment = FacesEnvironment.createEnvironment().withContent("/test.xhtml", 
                "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n" + 
        		"      xmlns:ui=\"http://java.sun.com/jsf/facelets\"\n" + 
        		"      xmlns:h=\"http://java.sun.com/jsf/html\">"+
        		"<h:form id=\"helloForm\" >"+
                "    <h:outputText value=\"foo_bar\" />\n" + 
        		"</h:form>\n" + 
        		"</html>").start();
    }
    
    @After
    public void thearDown() throws Exception{
        environment.release();
        environment = null;
    }

    @Test
    public void testRequest() throws Exception {
        FacesRequest request = environment.createFacesRequest("http://localhost/test.jsf?foo=bar");
        assertNotNull(request.execute());
        String contentAsString = request.getConnection().getContentAsString();
        assertTrue(contentAsString.contains(ResponseStateManager.VIEW_STATE_PARAM));
    }

    @Test
    @Threads(15)
    public void testConcurrentRequests() throws Exception {
//        System.out.println("concurrent request");
        FacesRequest request = environment.createFacesRequest("http://localhost/test.jsf");
        assertNotNull(request.execute());
        String contentAsString = request.getConnection().getContentAsString();
        assertThat(contentAsString,containsString(ResponseStateManager.VIEW_STATE_PARAM));
    }
}
