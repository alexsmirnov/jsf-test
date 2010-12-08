package org.jboss.test.qunit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


public class QunitRunnerTest extends QunitTestBase {
    
    private static final String FOO = "<div id=\"foo\">foo</div>";

    @Rule
    public Qunit qunit = Qunit.builder().content(FOO).build();
    
    @BeforeClass
    public static void setupClass(){
        
    }
    
    @Before
    public void setUp(){
        System.out.println("setup");
    }

    @After
    public void tearDown(){
        System.out.println("tear down");
    }
    
    @AfterClass
    public static void tearDownClass(){
        
    }
    
    @Test
    public void checkHtmlContent() throws Exception {
        assertEquals(FOO, qunit.getHtmlContent(this));
    }
    
    @Test
    public void testRunScript() throws Exception {
        assertEquals(Double.valueOf(4.0),qunit.runScript("2+2"));
    }
    
    @Test
    public void testDomScript() throws Exception {
        assertEquals("foo",qunit.runScript("document.getElementById(\"foo\").innerHTML;"));
    }
}
