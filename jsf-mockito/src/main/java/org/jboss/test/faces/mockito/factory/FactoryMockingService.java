package org.jboss.test.faces.mockito.factory;

public class FactoryMockingService {

    public static FactoryMockingService getInstance() {
        return new FactoryMockingService();
    }

    public <T> FactoryMock<T> createFactoryMock(Class<T> type) {
        return new FactoryMockImpl<T>(type);
    }

    public <T> void enhance(FactoryMock<T> factoryMock, T mockToEnhance) {
        ((FactoryMockImpl<T>) factoryMock).enhance(mockToEnhance);
    }
}
