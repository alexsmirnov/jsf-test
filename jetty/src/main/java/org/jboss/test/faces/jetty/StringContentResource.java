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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.mortbay.resource.Resource;

/**
 * @author Nick Belaevski
 * 
 */
public class StringContentResource extends Resource {

    /**
     * 
     */
    private static final long serialVersionUID = -545867200766773535L;

    private byte[] content;
    
    private String path;
    
    public StringContentResource(String contentString, String path) {
        super();
        this.content = contentString.getBytes();
        this.path = path;
    }

    @Override
    public Resource addPath(String path) throws IOException, MalformedURLException {
        throw new FileNotFoundException(path);
    }

    @Override
    public boolean delete() throws SecurityException {
        return false;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public String getName() {
        return path;
    }

    @Override
    public OutputStream getOutputStream() throws IOException, SecurityException {
        throw new FileNotFoundException(path);
    }

    @Override
    public URL getURL() {
        try {
            return new URL("urn",null,0,"", new URLStreamHandler() {
                
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(u) {
                        
                        @Override
                        public void connect() throws IOException {
                        }
                        
                        @Override
                        public Object getContent() throws IOException {
                            return content;
                        }
                        
                        @Override
                        public InputStream getInputStream() throws IOException {
                            return StringContentResource.this.getInputStream();
                        }
                    };
                }
            });
        } catch (MalformedURLException e) {
            //TODO handle
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long lastModified() {
        return -1;
    }

    @Override
    public long length() {
        return content.length;
    }

    @Override
    public String[] list() {
        return null;
    }

    @Override
    public void release() {
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        return false;
    }

}
