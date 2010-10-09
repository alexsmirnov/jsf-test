/*
 * $Id$
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

package org.jboss.test.faces.mock.component;

import static org.easymock.EasyMock.*;

import javax.faces.component.UIViewRoot;

import org.jboss.test.faces.mock.FacesMock;

/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class ViewBuilder extends TreeBuilderImpl<UIViewRoot> {

    protected ViewBuilder(UIViewRoot component) {
        super(component);
    }

    public static ViewBuilder createView() {
        return createView(FacesMock.createMock(UIViewRoot.class));
    }

    public static ViewBuilder createView(UIViewRoot mockViewRoot) {
        ViewBuilder treeBuilder = new ViewBuilder(mockViewRoot);
        expect(mockViewRoot.getParent()).andStubReturn(null);
        return treeBuilder;
    }

    public ViewBuilder setViewId(String viewId) {
        expect(getComponent().getViewId()).andStubReturn(viewId);
        return this;
    }
}
