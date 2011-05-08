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

import java.lang.reflect.Field;

import org.mockito.Mockito;
import org.mockito.cglib.proxy.Callback;
import org.mockito.cglib.proxy.Factory;
import org.mockito.internal.MockitoCore;
import org.mockito.internal.MockitoInvocationHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.creation.jmock.SerializableNoOp;
import org.mockito.internal.util.MockUtil;

/**
 * The implementation of factory mocks - mocks which needs to be created by constructing class instance by class name (e.g.
 * {@see Class#forName(String)).
 * 
 * @param <T>
 *            the generic type
 */
public class FactoryMockImpl<T> implements FactoryMock<T> {

    /** The message thrown when compatibility issue of used Mockito version are detected */
    private final static String MOCKITO_COMPATIBILITY_MESSAGE = "Mockito internals has changed and this code not anymore compatible";

    /** The original class. */
    private Class<T> originalClass;

    /** The mock. */
    private T mock;

    /**
     * Instantiates a new factory mock impl.
     * 
     * @param type
     *            the type
     */
    FactoryMockImpl(Class<T> type) {
        this.originalClass = type;
        initialize();
    }

    /**
     * Initialize.
     */
    private void initialize() {
        mock = Mockito.mock(originalClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.mockito.factory.FactoryMock#getMockClass()
     */
    @SuppressWarnings("unchecked")
    public Class<T> getMockClass() {
        return (Class<T>) mock.getClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.mockito.factory.FactoryMock#getMockName()
     */
    public String getMockClassName() {
        return mock.getClass().getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.mockito.factory.FactoryMock#createNewMockInstance()
     */
    @SuppressWarnings("unchecked")
    public T createNewMockInstance() {
        try {
            return (T) mock.getClass().newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot create instance for mock factory of class '" + originalClass + "'",
                e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Cannot create instance for mock factory of class '" + originalClass + "'",
                e);
        }
    }

    /**
     * Enhance the mock class instance with mocking ability.
     * 
     * @param mockToEnhance
     *            the mock to enhance
     */
    void enhance(T mockToEnhance) {
        MockUtil mockUtil = getMockUtil();
        MockitoInvocationHandler mockHandler = (MockitoInvocationHandler) mockUtil.getMockHandler(mock);
        MethodInterceptorFilter filter = new MethodInterceptorFilter(mockHandler, getDefaultSettings());
        ((Factory) mockToEnhance).setCallbacks(new Callback[] { filter, SerializableNoOp.SERIALIZABLE_INSTANCE });
    }

    /**
     * Gets the default settings of Mockito
     * 
     * @return the default settings of Mockito
     */
    private MockSettingsImpl getDefaultSettings() {
        return (MockSettingsImpl) new MockSettingsImpl().defaultAnswer(Mockito.RETURNS_DEFAULTS);
    }

    /**
     * Gets the {@link MockUtil} by reflection.
     * 
     * @return the mock utilities
     */
    private MockUtil getMockUtil() {
        MockitoCore mockitoCore = getMockitoCore();

        Field field = getDeclaredFieldFromType(MockitoCore.class, "mockUtil");
        return (MockUtil) getObjectFromDeclaredField(mockitoCore, field);
    }

    /**
     * Gets the {@link MockitoCore} by reflection.
     * 
     * @return the mockito core
     */
    private MockitoCore getMockitoCore() {
        Field mockitoCoreField = getDeclaredFieldFromType(Mockito.class, "MOCKITO_CORE");
        return (MockitoCore) getObjectFromDeclaredField(null, mockitoCoreField);
    }

    /**
     * Gets the declared field from type.
     * 
     * @param type
     *            the type
     * @param fieldName
     *            the field name
     * @return the declared field from type
     */
    private Field getDeclaredFieldFromType(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (Exception e) {
            throw new IllegalStateException(MOCKITO_COMPATIBILITY_MESSAGE, e);
        }
    }

    /**
     * Gets the object from declared field.
     * 
     * @param instance
     *            the instance
     * @param field
     *            the field
     * @return the object from declared field
     */
    private Object getObjectFromDeclaredField(Object instance, Field field) {
        boolean accessible = field.isAccessible();
        Object result = null;
        if (!accessible) {
            field.setAccessible(true);
        }
        try {
            result = field.get(instance);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if (!accessible) {
                field.setAccessible(false);
            }
        }
        return result;
    }
}
