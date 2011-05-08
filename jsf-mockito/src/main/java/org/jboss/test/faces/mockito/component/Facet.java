package org.jboss.test.faces.mockito.component;

import javax.faces.component.UIComponent;

public class Facet<T extends UIComponent> {
    
    private final TreeBuilder<T> builder;
    private final String name;

    public Facet(String name, TreeBuilder<T> builder) {
        this.name = name;
        this.builder = builder;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the builder
     */
    public TreeBuilder<T> getBuilder() {
        return this.builder;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the name
     */
    public String getName() {
        return this.name;
    }

}
