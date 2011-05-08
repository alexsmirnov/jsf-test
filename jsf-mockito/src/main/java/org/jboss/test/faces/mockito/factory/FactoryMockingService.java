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

/**
 * The service class for creating new factory mocks and their enhancing by mocking ability.
 */
public class FactoryMockingService {

    /**
     * Gets the single instance of FactoryMockingService.
     * 
     * @return single instance of FactoryMockingService
     */
    public static FactoryMockingService getInstance() {
        return new FactoryMockingService();
    }

    /**
     * Creates the factory mock.
     * 
     * @param <T>
     *            the generic type
     * @param type
     *            the type
     * @return the factory mock
     */
    public <T> FactoryMock<T> createFactoryMock(Class<T> type) {
        return new FactoryMockImpl<T>(type);
    }

    /**
     * Enhance the factored mock by mocking ability.
     * 
     * @param <T>
     *            the generic type
     * @param factoryMock
     *            the factory mock
     * @param mockToEnhance
     *            the mock to enhance
     */
    public <T> void enhance(FactoryMock<T> factoryMock, T mockToEnhance) {
        ((FactoryMockImpl<T>) factoryMock).enhance(mockToEnhance);
    }
}
