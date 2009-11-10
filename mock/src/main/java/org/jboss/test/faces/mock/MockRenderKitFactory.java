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

import java.util.Collections;
import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class MockRenderKitFactory extends RenderKitFactory {

    /* (non-Javadoc)
     * @see javax.faces.render.RenderKitFactory#addRenderKit(java.lang.String, javax.faces.render.RenderKit)
     */
    @Override
    public void addRenderKit(String renderKitId, RenderKit renderKit) {
        // do nothing ?
    }

    /* (non-Javadoc)
     * @see javax.faces.render.RenderKitFactory#getRenderKit(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public RenderKit getRenderKit(FacesContext context, String renderKitId) {
        if(null != MockFacesEnvironment.instance){
            return MockFacesEnvironment.instance.getRenderKit();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.faces.render.RenderKitFactory#getRenderKitIds()
     */
    @Override
    public Iterator<String> getRenderKitIds() {
        return Collections.singleton(RenderKitFactory.HTML_BASIC_RENDER_KIT).iterator();
    }

}
