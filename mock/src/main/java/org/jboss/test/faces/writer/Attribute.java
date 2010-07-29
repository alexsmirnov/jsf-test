/******************************************************************************
 * $Id$
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.test.faces.writer;

/**
 * @author asmirnov
 *
 */
public class Attribute {
	private final String name;
	private final Object value;
	private final String componentAttribute;
	/**
	 * @param name
	 * @param value
	 * @param componentAttribute
	 */
	public Attribute(String name, Object value, String componentAttribute) {
	    super();
	    this.name = name;
	    this.value = value;
	    this.componentAttribute = componentAttribute;
	}
	/**
     * @return the name
     */
    public String getName() {
    	return name;
    }
	/**
     * @return the value
     */
    public Object getValue() {
    	return value;
    }
	/**
     * @return the componentAttribute
     */
    public String getComponentAttribute() {
    	return componentAttribute;
    }
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
	/* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Attribute other = (Attribute) obj;
        if (name == null) {
    	    if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }

}
