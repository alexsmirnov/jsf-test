package org.jboss.test.qunit;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UrlResourceTest extends QunitTestBase {

    @Rule
    public Qunit qunit = Qunit.builder().load(this.getClass().getResource("/test.js")).build();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAddScriptByUrl() throws Exception {
        assertEquals("foobar",qunit.runScript("foo('bar')"));
    }
}
