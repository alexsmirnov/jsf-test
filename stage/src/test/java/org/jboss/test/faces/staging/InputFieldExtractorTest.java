package org.jboss.test.faces.staging;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Map;

import org.jboss.test.faces.FacesEnvironment;
import org.junit.Test;


public class InputFieldExtractorTest {
    
    private static final String CONTENT = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
            		"<form id=\"helloForm\" name=\"helloForm\" method=\"post\" action=\"/test.jsf\" enctype=\"application/x-www-form-urlencoded\">\n" + 
            		"<input type=\"hidden\" name=\"helloForm\" value=\"helloForm\" />\n" + 
            		"foo_bar<input type=\"hidden\" name=\"javax.faces.ViewState\" id=\"javax.faces.ViewState\" value=\"3598242702676799043:-4740956537176246209\" autocomplete=\"off\" />\n" + 
            		"</form>\n" + 
            		"</html>";

    @Test
    public void testGetInputs() throws Exception {
        Collection<String> inputFields = FacesEnvironment.getInputFields(CONTENT);
        assertEquals(2, inputFields.size());
    }

    @Test
    public void testGetHiddenFields() throws Exception {
        Map<String, String> fields = FacesEnvironment.getHiddenFields(CONTENT);
        assertEquals(2, fields.size());
        assertEquals("helloForm", fields.get("helloForm"));
    }
}
