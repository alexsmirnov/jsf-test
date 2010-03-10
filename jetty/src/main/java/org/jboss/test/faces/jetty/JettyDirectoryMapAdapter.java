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

import java.io.IOException;
import java.net.MalformedURLException;

import org.jboss.test.faces.TestException;
import org.jboss.test.faces.staging.DirectoryMapAdapter;
import org.jboss.test.faces.staging.ServerResourcePath;
import org.mortbay.resource.Resource;

/**
 * @author Nick Belaevski
 * 
 */
public class JettyDirectoryMapAdapter implements DirectoryMapAdapter<Resource, VirtualDirectoryResource> {

    static final DirectoryMapAdapter<Resource, VirtualDirectoryResource> INSTANCE = new JettyDirectoryMapAdapter();
    
    private JettyDirectoryMapAdapter() {
    }
    
    public void addResource(VirtualDirectoryResource resource, ServerResourcePath path, Resource childResource) {
        resource.addResource(path.toString(), childResource);
    }

    public VirtualDirectoryResource asDirectory(Resource resource) {
        if (resource instanceof VirtualDirectoryResource) {
            return (VirtualDirectoryResource) resource;
        }
        return null;
    }

    public Resource getResource(Resource resource, ServerResourcePath path) {
        try {
            return resource.addPath(path.toString());
        } catch (MalformedURLException e) {
            throw new TestException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TestException(e.getMessage(), e);
        }
    }

    public VirtualDirectoryResource addDirectory(VirtualDirectoryResource directory, String name) {
        return directory.createChildDirectory(name);
    }

    public VirtualDirectoryResource createChildDirectory(String path) {
        return new VirtualDirectoryResource(path);
    }

    public String getResourcePath(Resource resource) {
        return resource.getName();
    }

}
