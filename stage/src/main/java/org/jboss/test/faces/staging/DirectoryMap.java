/**
 * 
 */
package org.jboss.test.faces.staging;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Directory-like map for a virtual web application content.
 * @author Nick Belaevski
 *
 */
public class DirectoryMap<Resource, Directory extends Resource> {

    /**
     * Directory content.
     */
    private final Map<String, Resource> children = new TreeMap<String, Resource>();

    private final Directory directory;
    
    private final DirectoryMapAdapter<Resource, Directory> adapter;
    
    public DirectoryMap(Directory directory, DirectoryMapAdapter<Resource, Directory> instance) {
        super();
        this.directory = directory;
        this.adapter = instance;
    }

    private void putChild(String fileName, Resource child) {
        Resource previousChild = children.put(fileName, child);
        if (previousChild != null) {
            //TODO warning for replaced resource
        }
    }
    
    public Directory addDirectory(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException();
        }
        
        Resource childResource = children.get(fileName);

        Directory result = adapter.asDirectory(childResource);

        if (result == null) {
            String resourcePath = adapter.getResourcePath(directory);
            if (resourcePath == null) {
                resourcePath = "";
            }
            
            result = adapter.createChildDirectory(resourcePath + "/" + fileName);
            putChild(fileName, result);
        }
        
        return result;
    }

    public Directory addDirectory(ServerResourcePath path) {
        if (null == path) {
            throw new IllegalArgumentException();
        }

        Directory result = this.directory;
        ServerResourcePath subPath = path;
        String fileName;
        
        while (subPath != null && (fileName = subPath.getFileName()) != null) {
            result = adapter.addDirectory(result, fileName);
            subPath = subPath.getNextPath();
        }
        
        return result;
    }
    
    public void addResource(ServerResourcePath path, Resource resource) {
        if (null == path) {
            throw new IllegalArgumentException();
        }
        
        String fileName = path.getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException();
        }

        if (path.hasNextPath()) {
            Directory childResource = addDirectory(fileName);
            
            adapter.addResource(childResource, path.getNextPath(), resource);
        } else {
            putChild(fileName, resource);
        }
    }

    public Resource getResource(ServerResourcePath path) {
        if (null == path) {
            throw new NullPointerException();
        }

        Resource resource = null; //children.get(path.getName());

        String fileName = path.getFileName();
        if (fileName == null) {
            // Path points to the resource itself.
            resource = this.directory;
        } else {
            resource = children.get(fileName);
            if (resource != null && path.hasNextPath()) {
                // Get next resource in the tree, if exists.
                resource = adapter.getResource(resource, path.getNextPath());
            }
        }

        return resource;
    }

    public Set<String> getResourceNames() {
        return children.keySet();
    }

    public Collection<Resource> getResources() {
        return children.values();
    }

    public void clear() {
        children.clear();
    }
}
