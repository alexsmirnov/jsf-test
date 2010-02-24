package org.jboss.test.faces.mock;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.createNiceControl;
import static org.easymock.EasyMock.createStrictControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;

import org.easymock.IMocksControl;
import org.easymock.internal.ObjectMethodsFilter;
/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class FacesMock {
    
    private static final Class<?> MOCK_OBJECT = FacesMockController.MockObject.class;

    private FacesMock() {
        //hidden constructor
    }

    public static MockFacesEnvironment createMockEnvironment(){
        return new MockFacesEnvironment(createControl());
    }

    public static MockFacesEnvironment createNiceEnvironment(){
        return new MockFacesEnvironment(createNiceControl());
    }

    public static MockFacesEnvironment createStrictEnvironment(){
        return new MockFacesEnvironment(createStrictControl());
    }

    public static <T> T createMock(String name, Class<T> clazz, IMocksControl control) {
        if (MOCK_OBJECT.isAssignableFrom(clazz)) {
            try {
                Constructor<T> constructor = clazz.getConstructor(IMocksControl.class, String.class);
                return (T) constructor.newInstance(control, name);
            } catch (Exception e) {
                throw new IllegalArgumentException("Class " + clazz.getName() + " cannot be created", e);
            }
        } else {
            try {
                return FacesMockController.createMock(name, clazz, control);
            } catch (ClassNotFoundException e) {
                return control.createMock(name, clazz);
            }
        }
    }

    public static <T> T createMock(Class<T> clazz){
        return createMock(null, clazz);
    }
    
    public static <T> T createMock(String name, Class<T> clazz){
        return createMock(name, clazz, createControl());
    }

    public static <T> T createNiceMock(Class<T> clazz){
        return createNiceMock(null, clazz);
    }

    public static <T> T createNiceMock(String name, Class<T> clazz) {
        return createMock(name, clazz, createNiceControl());
    }
    
    public static <T> T createStrictMock(Class<T> clazz){
        return createStrictMock(null, clazz);
    }
    
    public static <T> T createStrictMock(String name, Class<T> clazz){
        return createMock(name, clazz, createStrictControl());
    }
    
    private static IMocksControl getControl(Object mock) {
        if (mock instanceof FacesMockController.MockObject) {
            FacesMockController.MockObject mockObject = (FacesMockController.MockObject) mock;
            return mockObject.getControl();
        } else {
            // Delegate to EazyMock
            return ((ObjectMethodsFilter) Proxy
                .getInvocationHandler(mock)).getDelegate().getControl();
        }
    }

    public static void replay(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).replay();
        }
    }

    public static void reset(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).reset();
        }
    }

    /**
     * Resets the given mock objects (more exactly: the controls of the mock
     * objects) and turn them to a mock with nice behavior. For details, see 
     * the EasyMock documentation.
     * 
     * @param mocks
     *            the mock objects
     */
    public static void resetToNice(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).resetToNice();
        }
    }

    /**
     * Resets the given mock objects (more exactly: the controls of the mock
     * objects) and turn them to a mock with default behavior. For details, see 
     * the EasyMock documentation.
     * 
     * @param mocks
     *            the mock objects
     */
    public static void resetToDefault(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).resetToDefault();
        }
    }

    /**
     * Resets the given mock objects (more exactly: the controls of the mock
     * objects) and turn them to a mock with strict behavior. For details, see 
     * the EasyMock documentation.
     * 
     * @param mocks
     *            the mock objects
     */
    public static void resetToStrict(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).resetToStrict();
        }
    }

    public static void verify(Object... mocks) {
        for (Object object : mocks) {
            getControl(object).verify();
        }
    }

    /**
     * Switches order checking of the given mock object (more exactly: the
     * control of the mock object) the on and off. For details, see the EasyMock
     * documentation.
     * 
     * @param mock
     *            the mock object.
     * @param state
     *            <code>true</code> switches order checking on,
     *            <code>false</code> switches it off.
     */
    public static void checkOrder(Object mock, boolean state) {
        getControl(mock).checkOrder(state);
    }

}
