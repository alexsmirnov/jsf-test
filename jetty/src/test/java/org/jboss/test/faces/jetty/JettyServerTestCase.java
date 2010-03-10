/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.faces.jetty;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.test.faces.FacesEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nick Belaevski
 * 
 */
public class JettyServerTestCase {

    private static final int PORT = 9999;

    private FacesEnvironment environment;

    @Before
    public void setUp() throws Exception {
        environment = new FacesEnvironment(new JettyServer(PORT));
        environment.withWebRoot("org/jboss/test/hello-jetty.xhtml").start();
    }

    @After
    public void tearDown() throws Exception {
        environment.release();
        environment = null;
    }

    @Test
    public void testHelloPage() throws Exception {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(new URL("http", "localhost", PORT, "/hello-jetty.jsf?name=JettyServer")
            .toExternalForm());

        try {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + method.getStatusLine());
            }

            String responseBodyAsString = method.getResponseBodyAsString();
            assertTrue(responseBodyAsString.contains("Hello, JettyServer!"));
            
        } finally {
            method.releaseConnection();
        }
    }
}
