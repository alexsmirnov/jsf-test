/**
 * 
 */
package org.jboss.test.faces.htmlunit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpSession;

import org.jboss.test.faces.AbstractFacesTest;
import org.jboss.test.faces.FacesEnvironment;
import org.jboss.test.faces.htmlunit.LocalWebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.SubmittableElement;

/**
 * @author asmirnov
 * 
 */
public class FacesServerTest {


	private HtmlUnitEnvironment environment;

    /**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	    this.environment = new HtmlUnitEnvironment();
	    this.environment.withResource("/WEB-INF/faces-config.xml", "org/jboss/test/WEB-INF/faces-config.xml").
	    withResource("/hello.xhtml", "org/jboss/test/hello.xhtml").withResource("/response.xhtml", "org/jboss/test/response.xhtml").
	    withResource("/wave.med.gif", "org/jboss/test/wave.med.gif").start();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	    this.environment.release();
	    this.environment = null;
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
		Element submitElement = page.getElementById("helloForm:submit");
		HtmlForm htmlForm = page.getFormByName("helloForm");
		htmlForm.getInputByName("helloForm:username");
		assertNotNull(htmlForm);
		HtmlInput input = htmlForm.getInputByName("helloForm:username");
		assertNotNull(input);
		input.setValueAttribute("foo");
		HtmlPage responsePage = (HtmlPage) htmlForm.submit((SubmittableElement) submitElement);
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
