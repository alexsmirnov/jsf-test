package org.jboss.test.qunit;

import java.net.URL;

public interface ScriptRef {

    URL getScript(Object base);

    String getContent(Object base);

    
}
