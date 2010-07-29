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

import java.io.IOException;
import java.io.Writer;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import org.jboss.test.faces.mock.FacesTestException;

/**
 * @author asmirnov
 *
 */
public class RecordingResponseWriter extends ResponseWriter {

	private Record currentRecord;
	private String characterEncoding;
	private String contentType;
	/**
     * @param characterEncoding
     * @param contentType
     */
    public RecordingResponseWriter(String characterEncoding, String contentType) {
	    super();
	    this.characterEncoding = characterEncoding;
	    this.contentType = contentType;
    }

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#cloneWithWriter(java.io.Writer)
	 */
	@Override
	public ResponseWriter cloneWithWriter(Writer writer) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#startDocument()
     */
    @Override
    public void startDocument() throws IOException {
    	currentRecord = new DocumentRecord();
    }

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#endDocument()
	 */
	@Override
	public void endDocument() throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#startElement(java.lang.String, javax.faces.component.UIComponent)
     */
    @Override
    public void startElement(String name, UIComponent component)
            throws IOException {
    	currentRecord = currentRecord.addRecord(new ElementRecord(name,component));    
    }

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#endElement(java.lang.String)
	 */
	@Override
	public void endElement(String name) throws IOException {
		if(!name.equals(currentRecord.getName())){
	    	throw new FacesTestException("End element does not match current element");
		}
		currentRecord = currentRecord.getParent();
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#writeAttribute(java.lang.String, java.lang.Object, java.lang.String)
	 */
	@Override
	public void writeAttribute(String name, Object value, String property)
	        throws IOException {
		currentRecord.addAttribute(new Attribute(name, value, property));
	}

	/* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#writeURIAttribute(java.lang.String, java.lang.Object, java.lang.String)
     */
    @Override
    public void writeURIAttribute(String name, Object value, String property)
            throws IOException {
    	currentRecord.addAttribute(new URIAttribute(name, value, property));
    }

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#writeText(java.lang.Object, java.lang.String)
	 */
	@Override
	public void writeText(Object text, String property) throws IOException {
		currentRecord.addRecord(new TextRecord(text,property));

	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ResponseWriter#writeText(char[], int, int)
	 */
	@Override
	public void writeText(char[] text, int off, int len) throws IOException {
		currentRecord.addRecord(new TextRecord(String.copyValueOf(text, off, len),null));
	}

	/* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#flush()
     */
    @Override
    public void flush() throws IOException {
    	// do nothing
    
    }

	/* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding() {
    	return this.characterEncoding;
    }

	/* (non-Javadoc)
     * @see javax.faces.context.ResponseWriter#getContentType()
     */
    @Override
    public String getContentType() {
    	return this.contentType;
    }

	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() throws IOException {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		currentRecord.addRecord(new TextRecord(String.copyValueOf(cbuf, off, len),null));
	}

	@Override
    public void writeComment(Object comment) throws IOException {
		currentRecord.addRecord(new CommentRecord(comment));
    }

}
