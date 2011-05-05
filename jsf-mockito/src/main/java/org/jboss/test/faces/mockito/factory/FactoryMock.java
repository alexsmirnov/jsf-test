package org.jboss.test.faces.mockito.factory;

public interface FactoryMock<T> {

    Class<T> getMockClass();

    String getMockClassName();

    Object createNewMockInstance();

}