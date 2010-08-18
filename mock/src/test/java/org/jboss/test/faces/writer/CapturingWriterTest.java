package org.jboss.test.faces.writer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CapturingWriterTest {

    private RecordingResponseWriter writer;

    @Before
    public void setUp(){
        writer = new RecordingResponseWriter("UTF-8","text/html");   
    }
    
    @After
    public void tearDown(){
        writer = null;
    }
    
    @Test
    public void testToString() throws Exception{
        writer.startDocument();
        writer.startElement("html", null);
        writer.writeAttribute("id", "id1", null);
        writer.startElement("img", null);
        writer.writeURIAttribute("href", "http://google.com", null);
        writer.writeText("<a>", null);
        writer.write("<b>");
        writer.endElement("img");
        writer.endElement("html");
        writer.endDocument();
        assertEquals("<html id=\"id1\"><img href=\"http://google.com\"><a><b></img></html>", writer.toString());
    }

    private void captureContent() throws Exception {
        writer.startElement("html", null);
        writer.writeAttribute("id", "id1", null);
        writer.startElement("img", null);
        writer.writeURIAttribute("href", "http://google.com", null);
        writer.writeText("<a>", null);
        writer.endElement("img");
        writer.startElement("img", null);
        writer.writeURIAttribute("href", "http://exadel.com", null);
        writer.write("<b>");
        writer.startElement("img", null);
        writer.writeURIAttribute("href", "http://example.com", null);
        writer.writeText("foo", null);
        writer.endElement("img");
        writer.endElement("img");
        writer.endElement("html");

    }
    @Test
    public void testFind() throws Exception {
        Criteria find = createCriteria();
        assertTrue(find.element("html").matches());
    }

    private Criteria createCriteria() throws Exception {
        captureContent();
        Criteria find = writer.find();
        return find;
    }

    @Test
    public void testFindAtLevel() throws Exception {
        Criteria find = createCriteria();
        assertEquals("foo",find.element("img").atLevel(2).getText());
    }
    @Test
    public void testFindAtPosition() throws Exception {
        Criteria find = createCriteria();
        assertEquals("<b>foo",find.element("img").atPosition(1).getText());
    }

    @Test
    public void testFindAttribute() throws Exception {
        Criteria find = createCriteria();
        assertEquals("id1",find.element(".*").withAttribute("id").getAttribute("id"));
    }

    @Test
    public void testFindAttributeByPattern() throws Exception {
        Criteria find = createCriteria();
        assertEquals("img",find.element(".*").withAttribute("href",".*example\\.com").getName());
    }
    @Test
    public void testFindTextByPattern() throws Exception {
        Criteria find = createCriteria();
        assertEquals("http://google.com",find.element("img").contains("<a>").getAttribute("href"));
    }
    @Test
    public void testFindAtSecondLevel() throws Exception {
        Criteria find = createCriteria();
        assertEquals("foo",find.element("html").element("img").element("img").getText());
    }

}
