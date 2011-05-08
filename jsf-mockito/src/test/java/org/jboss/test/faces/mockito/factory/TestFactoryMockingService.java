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
package org.jboss.test.faces.mockito.factory;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

import org.jboss.test.faces.mockito.factory.FactoryMock;
import org.jboss.test.faces.mockito.factory.FactoryMockingService;
import org.junit.Test;

/**
 * The Class TestFactoryMockingService.
 */
public class TestFactoryMockingService {

    /**
     * Test factory.
     */
    @Test
    public void testFactory() {
        FactoryMockingService service = FactoryMockingService.getInstance();
        FactoryMock<ApplicationFactory> mockFactory = service.createFactoryMock(ApplicationFactory.class);

        FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, mockFactory.getMockClassName());

        ApplicationFactory newMock = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);

        service.enhance(mockFactory, newMock);

        Application application = mock(Application.class);
        when(newMock.getApplication()).thenReturn(application);

        assertSame(newMock.getApplication(), application);

        ApplicationFactory newMock2 = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);

        assertSame(newMock2.getApplication(), application);
    }
}
