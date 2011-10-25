package org.jboss.test.faces.mock;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


import org.junit.Ignore;
import org.junit.Test;

public class MockFacesContextTest {

    @Test
    @Ignore
    public void testCreate() throws Exception {
        FacesContext facesContext = FacesMock.createMock(FacesContext.class);
        expect(facesContext.getMaximumSeverity()).andReturn(FacesMessage.SEVERITY_INFO);
        ExternalContext externalContext = FacesMock.createMock(ExternalContext.class);
        expect(facesContext.getExternalContext()).andReturn(externalContext);
        FacesMock.replay(facesContext,externalContext);
        FacesContext facesContext2 = FacesContext.getCurrentInstance();
        assertSame(facesContext, facesContext2);
        assertEquals(FacesMessage.SEVERITY_INFO, facesContext2.getMaximumSeverity());
        assertSame(externalContext, facesContext2.getExternalContext());
        FacesMock.verify(facesContext,externalContext);
    }
}
