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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.mortbay.resource.Resource;

/**
 * @author Nick Belaevski
 * 
 */
final class BadResource extends Resource {

    /**
     * 
     */
    private static final long serialVersionUID = -731399783337789651L;
    
    private String path;

    public BadResource(String name) {
        this.path = name;
    }

    public boolean exists() {
        return false;
    }

    public long lastModified() {
        return -1;
    }

    public boolean isDirectory() {
        return false;
    }

    public long length() {
        return -1;
    }

    public File getFile() {
        return null;
    }

    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(path);
    }

    public OutputStream getOutputStream() throws IOException, SecurityException {
        throw new FileNotFoundException(path);
    }

    public boolean delete() throws SecurityException {
        return false;
    }

    public boolean renameTo(Resource dest) throws SecurityException {
        return false;
    }

    public String[] list() {
        return null;
    }

    @Override
    public Resource addPath(String path) throws IOException, MalformedURLException {
        return new BadResource(this.path + '/' + path);
    }

    @Override
    public String getName() {
        return path;
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public void release() {
    }

}
