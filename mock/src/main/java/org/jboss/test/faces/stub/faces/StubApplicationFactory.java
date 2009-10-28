package org.jboss.test.faces.stub.faces;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

/**
 * An mock implementation of the JSF ApplicationFactory which returns a mock
 * Application wrapped in a SeamApplication. This class can be registered with
 * JSF to allow JSF to be used formally in a test environment as follows:
 * 
 * <code>
 * FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY,
 *    "org.jboss.test.faces.mock.faces.StubApplicationFactory");
 * Application application = ((ApplicationFactory) FactoryFinder
 *    .getFactory(FactoryFinder.APPLICATION_FACTORY)).getApplication();    
 * </code>
 * 
 * @author Dan Allen
 */
public class StubApplicationFactory extends ApplicationFactory
{
   private Application application;
   
   @Override
   public Application getApplication()
   {
//      if (application == null)
//      {
//         application = new SeamApplication();
//      }
      return application;
   }

   @Override
   public void setApplication(Application application)
   {
      this.application = application;
   }

}
