package org.jboss.test.qunit;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ScriptContentTest extends QunitTestBase {

    private static final String FOO = "function bar(param){ return \"foo\"+param;}\n";
    @Rule
    public Qunit qunit =  Qunit.builder().loadContent(FOO).build();;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoadedScript() throws Exception {
        assertEquals("foobar",qunit.runScript("bar('bar')"));
    }
}
