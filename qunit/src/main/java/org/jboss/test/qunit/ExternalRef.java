package org.jboss.test.qunit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ExternalRef extends URLRefBase implements ScriptRef {

    private final String src;

    public ExternalRef(String src) {
        this.src = src;
    }

    public URL getScript(Object base) {
        try {
            URL ref = new URL(src);
            return ref;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getContent(Object base) {
        try {
            InputStream inputStream = getScript(base).openStream();
            return readInputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
