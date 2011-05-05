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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import javax.faces.component.UIComponent;

import org.jboss.test.faces.FacesTestException;

/**
 * @author asmirnov
 *
 */
public class ElementRecord extends RecordBase implements Record {
	
	private LinkedHashMap<String,Attribute> attributes = new LinkedHashMap<String,Attribute>();
	private final String name;
	private final UIComponent component;

	public ElementRecord(String name, UIComponent component) {
		this.component = component;
		this.name = name;
    }

	@Override
	public void addAttribute(Attribute attr) {
	    if(getChildren().size()>0){
	    	throw new FacesTestException("Attempt to write attribute after element content");
	    }
	    if(attributes.containsKey(attr.getName())){
	    	throw new FacesTestException("Element alresdy has attribute");
	    }
	    attributes.put(attr.getName(),attr);
	}
	
	@Override
	public String getName() {
	    return name;
	}
	@Override
	public String toString() {
	    StringBuilder text = new StringBuilder();
	    text.append("<").append(name);
	    if(attributes.size()>0){
	    	for (Attribute attr : attributes.values()) {
	            text.append(" ").append(attr.toString());
            }
	    }
	    String content = super.toString();
	    if(content.length()>0){
	    	text.append(">").append(content);
	    	text.append("</").append(name).append(">");
	    } else {
			text.append("/>");
		}
		return text.toString();
	}

	public boolean containsAttribute(String name){
		return attributes.containsKey(name);
	}

	public Attribute getAttribute(String name2) {
	    return attributes.get(name2);
    }
}
