/**
 * 
 */
package org.jboss.test.faces.htmlunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.jboss.test.faces.ApplicationServer;
import org.jboss.test.faces.jetty.JettyServer;
import org.jboss.test.faces.staging.StagingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Element;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author asmirnov
 * 
 */
@RunWith(Parameterized.class)
public class FacesServerTest {

    private Class<?  extends ApplicationServer> applicationServerClass;

    private HtmlUnitEnvironment environment;

    public FacesServerTest(Class<? extends ApplicationServer> applicationServerClass) {
        super();
        this.applicationServerClass = applicationServerClass;
    }

    @Parameters
    public static Collection<Class<?>[]> serverClasses() {
        return Arrays.asList(new Class<?>[][] {{
            JettyServer.class
        }, {
            StagingServer.class
        }});
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        System.setProperty(ApplicationServer.APPLICATION_SERVER_PROPERTY, applicationServerClass.getName());
        this.environment = new HtmlUnitEnvironment(applicationServerClass.newInstance());
        this.environment.withWebRoot("org/jboss/test/hello.xhtml").start();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.environment.release();
        this.environment = null;
        System.clearProperty(ApplicationServer.APPLICATION_SERVER_PROPERTY);
    }

    /**
     * Test method for
     * {@link org.jboss.test.faces.staging.StagingServer#getConnection(java.net.URL)}.
     * 
     * @throws Exception
     */
    @Test
    public void testHelloFacelets() throws Exception {
        HtmlPage page = environment.getPage("/hello.jsf");
        System.out.println(page.asXml());		
        HtmlElement submitElement = page.getElementById("helloForm:submit");
        HtmlForm htmlForm = page.getFormByName("helloForm");
        htmlForm.getInputByName("helloForm:username");
        assertNotNull(htmlForm);
        HtmlInput input = htmlForm.getInputByName("helloForm:username");
        assertNotNull(input);
        input.setValueAttribute("foo");
        HtmlPage responsePage = submitElement.click();
        assertNotNull(responsePage);
        System.out.println(responsePage.asXml());		
        HttpSession session = environment.getServer().getSession(false);
        assertNotNull(session);
        HelloBean bean = (HelloBean) session.getAttribute("HelloBean");
        assertNotNull(bean);
        assertEquals("foo", bean.getName());
        Element span = responsePage.getElementById("responseform:userLabel");
        assertNotNull(span);
        assertEquals("foo", span.getTextContent().trim());		
    }


}
