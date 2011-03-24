package org.jboss.test.qunit;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class StringResourceTest extends QunitTestBase {

    @Rule
    public Qunit qunit = Qunit.builder().loadResource("/test.js").build();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoadedScript() throws Exception {
        assertEquals("foobar",qunit.runScript("foo('bar')"));
    }
}
