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

public class TestFactoryMockingService {

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
