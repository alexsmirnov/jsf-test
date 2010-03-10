package org.jboss.test.faces;

import org.jboss.test.faces.staging.suite.UrlResourceLoadingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses({
    UrlResourceLoadingTest.class 
})
public class ApplicationServerTestSuite {

}