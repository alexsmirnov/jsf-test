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

import static org.easymock.EasyMock.*;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

import static org.junit.Assert.*;

import org.easymock.internal.matchers.InstanceOf;
import org.jboss.test.faces.mock.context.MockFacesContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class MockFacesEnvironmentTest {

    private MockFacesEnvironment mockEnvironment;

    @Before
    public void setUp() throws Exception {
        this.mockEnvironment = FacesMock.createMockEnvironment();
    }

    @After
    public void tearDown() throws Exception {
        mockEnvironment.release();
    }

    @Test
    public void testVerify() {
        mockEnvironment.withServletRequest().withRenderKit();
        expect(mockEnvironment.getExternalContext().getInitParameter("foo")).andReturn("bar");
        expect(mockEnvironment.getResponseStateManager().isPostback(mockEnvironment.getFacesContext())).andReturn(Boolean.TRUE);
        mockEnvironment.replay();
        assertTrue(FacesContext.getCurrentInstance().getRenderKit().getResponseStateManager().isPostback(FacesContext.getCurrentInstance()));
        assertEquals("bar", FacesContext.getCurrentInstance().getExternalContext().getInitParameter("foo"));
        mockEnvironment.verify();
    }

    @Test
    public void testFactories() throws Exception {
        mockEnvironment.withFactories();
        Object factory = FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        assertTrue(factory instanceof MockFacesContextFactory);
        FacesContextFactory mockFactory = (FacesContextFactory) factory;
        expect(mockFactory.getFacesContext(anyObject(), anyObject(), anyObject(), (Lifecycle) anyObject())).andReturn(mockEnvironment.getFacesContext());
        Lifecycle lifecycle = mockEnvironment.createMock(Lifecycle.class);
        mockEnvironment.replay();
        FacesContextFactory factory2 = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        assertSame(factory, factory2);
        assertSame(mockEnvironment.getFacesContext(),mockFactory.getFacesContext(new Object(), new Object(), new Object(), lifecycle));
        mockEnvironment.verify();
    }
}
