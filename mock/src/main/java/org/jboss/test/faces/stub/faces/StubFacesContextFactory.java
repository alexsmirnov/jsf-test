package org.jboss.test.faces.stub.faces;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

public class StubFacesContextFactory extends FacesContextFactory
{
   private static FacesContext facesContext;

   @Override
   public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle) throws FacesException
   {
      return facesContext;
   }

   public static void setFacesContext(FacesContext facesContext)
   {
      StubFacesContextFactory.facesContext = facesContext;
   }

   public static FacesContext getFacesContext()
   {
      return StubFacesContextFactory.facesContext;
   }
}
