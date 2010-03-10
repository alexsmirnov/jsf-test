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
package org.jboss.test.faces;

import java.util.Map;

/**
 * @author Nick Belaevski
 * 
 */
abstract class AbstractServletObjectHolder {

    private String name = "Default";
    
    private Map<String, String> initParameters;
    
    private String mapping;
    
    protected AbstractServletObjectHolder(String mapping) {
        super();
        this.mapping = mapping;
    }

    /**
     * @return the mapping
     */
    public String getMapping() {
        return mapping;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the initParameters
     */
    public Map<String, String> getInitParameters() {
        return initParameters;
    }
    
    /**
     * @param initParameters the initParameters to set
     */
    public void setInitParameters(Map<String, String> initParameters) {
        this.initParameters = initParameters;
    }
    
}
