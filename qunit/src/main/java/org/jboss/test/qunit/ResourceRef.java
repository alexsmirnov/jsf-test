package org.jboss.test.qunit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ResourceRef extends URLRefBase implements ScriptRef {

    private final String src;

    public ResourceRef(String src) {
        this.src = src;
    }

    public URL getScript(Object base) {
        URL resource = base.getClass().getResource(src);
        if(null == resource){
            throw new RuntimeException("Resource not found: "+src);
        }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Qunit.DEFAULT_URL);
            if(!src.startsWith("/")){
                stringBuilder.append('/');
            }
            stringBuilder.append(src);
            URL url = new URL(stringBuilder.toString());
            return url;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid resource url: ",e);
        }
    }

    public String getContent(Object base) {
        try {
            URL resource = base.getClass().getResource(src);
            if(null == resource){
                throw new RuntimeException("Resource not found: "+src);
            }
            URLConnection connection = resource.openConnection();
            connection.setUseCaches(false);
            InputStream inputStream = connection.getInputStream();
            return readInputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
