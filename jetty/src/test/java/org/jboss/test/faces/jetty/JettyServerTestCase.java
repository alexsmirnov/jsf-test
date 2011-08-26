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

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.test.faces.FacesEnvironment;
import org.jboss.test.faces.FacesEnvironment.FacesRequest;
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
        HttpClient client = new DefaultHttpClient();
        HttpRequestBase method = new HttpGet(new URL("http", "localhost", PORT, "/hello-jetty.jsf?name=JettyServer")
            .toExternalForm());

        try {
            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + response.getStatusLine().getReasonPhrase());
            }

            String responseBodyAsString = convertStreamToString(response.getEntity().getContent());
            System.out.println("******");
            System.out.println(responseBodyAsString);
            System.out.println("******");
            assertTrue(responseBodyAsString.contains("Hello, JettyServer!"));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    @Test
    public void testJettyConnection() throws Exception {
        FacesRequest facesRequest = environment.createFacesRequest(new URL("http", "localhost", PORT, "/hello-jetty.jsf?name=JettyServer")
        .toExternalForm());
        facesRequest.execute();
        assertEquals(HttpStatus.SC_OK, facesRequest.getConnection().getResponseStatus());
        String responseBodyAsString = facesRequest.getConnection().getContentAsString();
        assertTrue(responseBodyAsString.contains("Hello, JettyServer!"));
    }
}
