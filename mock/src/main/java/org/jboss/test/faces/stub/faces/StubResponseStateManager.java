package org.jboss.test.faces.stub.faces;

import java.io.IOException;

import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

@SuppressWarnings("deprecation")
public class StubResponseStateManager extends ResponseStateManager
{
   @Override
   public Object getComponentStateToRestore(FacesContext ctx)
   {
      return new Object();
   }

   @Override
   public Object getTreeStructureToRestore(FacesContext ctx, String x)
   {
      return new Object();
   }

   @Override
   public void writeState(FacesContext ctx, SerializedView viewState) throws IOException
   {
   }
}
