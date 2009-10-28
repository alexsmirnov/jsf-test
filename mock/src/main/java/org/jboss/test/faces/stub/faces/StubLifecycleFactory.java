package org.jboss.test.faces.stub.faces;

import java.util.Arrays;
import java.util.Iterator;

import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

public class StubLifecycleFactory extends LifecycleFactory
{
   private static Lifecycle lifecycle;

   public static void setLifecycle(Lifecycle lifecycle)
   {
      StubLifecycleFactory.lifecycle = lifecycle;
   }

   public static Lifecycle getLifecycle()
   {
      return StubLifecycleFactory.lifecycle;
   }

   @Override
   public void addLifecycle(String lifecycleId, Lifecycle lifecycle)
   {
      throw new IllegalArgumentException("Not supported by mock");
   }

   @Override
   public Lifecycle getLifecycle(String lifecycleId)
   {
      return lifecycle;
   }

   @Override
   public Iterator<String> getLifecycleIds()
   {
      return Arrays.asList(DEFAULT_LIFECYCLE).iterator();
   }
}
