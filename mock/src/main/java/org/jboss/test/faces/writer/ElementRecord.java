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

import java.util.LinkedHashSet;

import javax.faces.component.UIComponent;

import org.jboss.test.faces.mock.FacesTestException;

/**
 * @author asmirnov
 *
 */
public class ElementRecord extends RecordBase implements Record {
	
	private LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
	private final String name;
	private final UIComponent component;

	public ElementRecord(String name, UIComponent component) {
		this.component = component;
		this.name = name;
    }

	@Override
	public void addAttribute(Attribute attr) {
	    if(getRecords().size()>0){
	    	throw new FacesTestException("Attempt to write attribute after element content");
	    }
	    if(attributes.contains(attr)){
	    	throw new FacesTestException("Element alresdy has attribute");
	    }
	    attributes.add(attr);
	}
}
