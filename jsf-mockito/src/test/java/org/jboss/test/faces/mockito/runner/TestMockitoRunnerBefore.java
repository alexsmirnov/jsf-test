/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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
package org.jboss.test.faces.mockito.runner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

/**
 * The Class TestMockitoRunnerBefore.
 */
@RunWith(FacesMockitoRunner.class)
public class TestMockitoRunnerBefore {

    /** The faces context. */
    @Inject
    FacesContext facesContext;

    /** The application. */
    @Mock
    Application application;

    /** The application cache. */
    Application applicationCache;

    /** The faces context cache. */
    FacesContext facesContextCache;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        assertNotNull(application);
        assertNotNull(facesContext);
        when(facesContext.getApplication()).thenReturn(application);
    }

    /**
     * Test injection.
     */
    @Test
    public void testInjection() {
        assertSame(application, facesContext.getApplication());
        applicationCache = application;
        facesContextCache = facesContext;
    }

    /**
     * Test each test has own environment.
     */
    @Test
    public void testEachTestHasOwnEnvironment() {
        assertSame(application, facesContext.getApplication());
        assertNotSame(application, applicationCache);
        assertNotSame(application, facesContextCache);
    }
}
