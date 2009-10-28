//$Id: StubLifecycle.java 8195 2008-05-15 13:25:37Z pete.muir@jboss.org $
package org.jboss.test.faces.stub.faces;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

public class StubLifecycle extends Lifecycle
{
   public static final Lifecycle INSTANCE = new StubLifecycle();

   public StubLifecycle()
   {
      StubLifecycleFactory.setLifecycle(this);
      FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, StubLifecycleFactory.class.getName());
   }

   @Override
   public void addPhaseListener(PhaseListener pl)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void execute(FacesContext ctx) throws FacesException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public PhaseListener[] getPhaseListeners()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void removePhaseListener(PhaseListener pl)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void render(FacesContext ctx) throws FacesException
   {
      throw new UnsupportedOperationException();
   }
}
