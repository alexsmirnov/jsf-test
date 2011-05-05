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

public class FactoryMockImpl<T> implements FactoryMock<T> {

    private final static String MOCKITO_COMPATIBILITY_MESSAGE = "Mockito internals has changed and this code not anymore compatible";

    private Class<T> originalClass;
    private T mock;

    FactoryMockImpl(Class<T> type) {
        this.originalClass = type;
        initialize();
    }

    private void initialize() {
        mock = Mockito.mock(originalClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.mockito.factory.FactoryMock#getMockClass()
     */
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
    public Object createNewMockInstance() {
        try {
            return mock.getClass().newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot create instance for mock factory of class '" + originalClass + "'",
                e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Cannot create instance for mock factory of class '" + originalClass + "'",
                e);
        }
    }

    void enhance(T mockToEnhance) {
        MockUtil mockUtil = getMockUtil();
        MockitoInvocationHandler mockHandler = (MockitoInvocationHandler) mockUtil.getMockHandler(mock);
        MethodInterceptorFilter filter = new MethodInterceptorFilter(mockHandler, getDefaultSettings());
        ((Factory) mockToEnhance).setCallbacks(new Callback[] { filter, SerializableNoOp.SERIALIZABLE_INSTANCE });
    }

    private MockSettingsImpl getDefaultSettings() {
        return (MockSettingsImpl) new MockSettingsImpl().defaultAnswer(Mockito.RETURNS_DEFAULTS);
    }

    private MockUtil getMockUtil() {
        MockitoCore mockitoCore = getMockitoCore();

        Field field = getDeclaredFieldFromType(MockitoCore.class, "mockUtil");
        return (MockUtil) getObjectFromDeclaredField(mockitoCore, field);
    }

    private MockitoCore getMockitoCore() {
        Field mockitoCoreField = getDeclaredFieldFromType(Mockito.class, "MOCKITO_CORE");
        return (MockitoCore) getObjectFromDeclaredField(null, mockitoCoreField);
    }

    private Field getDeclaredFieldFromType(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (Exception e) {
            throw new IllegalStateException(MOCKITO_COMPATIBILITY_MESSAGE, e);
        }
    }

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
