/*
 * $Id$
 *
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.test.faces.mock;
import static org.jboss.test.faces.mock.FacesMockController.findMethod;
import static org.jboss.test.faces.mock.FacesMockController.invokeMethod;

import javax.faces.component.ValueHolder;
import javax.faces.convert.Converter;

import org.easymock.IMocksControl;
import org.jboss.test.faces.mock.component.MockUIComponent;

/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class MockUIOutput extends MockUIComponent implements ValueHolder {

    public MockUIOutput(IMocksControl control, String name) {
        super(control, name);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ValueHolder#getConverter()
     */
    public Converter getConverter() {
        return invokeMethod(this, findMethod(ValueHolder.class, "getConverter"));
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ValueHolder#getLocalValue()
     */
    public Object getLocalValue() {
        return invokeMethod(this, findMethod(ValueHolder.class, "getLocalValue"));
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ValueHolder#getValue()
     */
    public Object getValue() {
        return invokeMethod(this, findMethod(ValueHolder.class, "getValue"));
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ValueHolder#setConverter(javax.faces.convert.Converter)
     */
    public void setConverter(Converter converter) {
        invokeMethod(this, findMethod(ValueHolder.class, "setConverter",Converter.class),converter);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ValueHolder#setValue(java.lang.Object)
     */
    public void setValue(Object value) {
        invokeMethod(this, findMethod(ValueHolder.class, "setValue",Object.class),value);
    }

}
