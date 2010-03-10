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
package org.jboss.test.faces.staging;


/**
 * @author Nick Belaevski
 * 
 */
public class StagingDirectoryMapAdapter implements DirectoryMapAdapter<ServerResource, ServerResourceDirectory> {

    static final DirectoryMapAdapter<ServerResource, ServerResourceDirectory> INSTANCE = 
        new StagingDirectoryMapAdapter();

    private StagingDirectoryMapAdapter() {
    }

    public void addResource(ServerResourceDirectory directory, ServerResourcePath path, ServerResource childResource) {
        directory.addResource(path, childResource);
    }

    public ServerResourceDirectory asDirectory(ServerResource resource) {
        if (resource instanceof ServerResourceDirectory) {
            return (ServerResourceDirectory) resource;
        }
        return null;
    }

    public ServerResource getResource(ServerResource resource, ServerResourcePath path) {
        return resource.getResource(path);
    }

    public ServerResourceDirectory addDirectory(ServerResourceDirectory directory, String name) {
        return directory.addDirectory(name);
    }

    public ServerResourceDirectory createChildDirectory(String path) {
        return new ServerResourceDirectoryImpl();
    }

    public String getResourcePath(ServerResource resource) {
        return "";
    }
}
