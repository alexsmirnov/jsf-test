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
public interface DirectoryMapAdapter<Resource, Directory> {

    /**
     * Cast resource type to directory type. Return <code>null</code> if resource is not a directory.
     * 
     * @param resource
     * @return
     */
    public Directory asDirectory(Resource resource);
    
    /**
     * Create new instance of directory type with the specified path. 
     * Path is optional meta-information intended for debugging means.
     * <br />
     * E.g.:
     * 
     * <code>
     * Directory directory = new Directory();
     * directory.setName(path);
     * return directory;
     * </code>
     * 
     * @param path
     * @return
     */
    public Directory createChildDirectory(String path);

    /**
     * Get resource path (optional information).
     * Used in conjunction with {@link #createChildDirectory(String)} method.
     * 
     * @param resource
     * @return
     */
    public String getResourcePath(Resource resource);
    
    /**
     * Directory get-or-create operation.
     * Checks for directory with the specified file name and returns it if exists, 
     * otherwise creates new and stores. 
     * 
     * @param directory
     * @param name
     * @return
     */
    public Directory addDirectory(Directory directory, String name);

    /**
     * Adds resource to the directory under specified path
     * 
     * @param directory
     * @param path
     * @param childResource
     */
    public void addResource(Directory directory, ServerResourcePath path, Resource childResource);

    /**
     * Get resource stored in directory by the specified path
     * 
     * @param resource
     * @param path
     * @return
     */
    public Resource getResource(Resource resource, ServerResourcePath path);

}
