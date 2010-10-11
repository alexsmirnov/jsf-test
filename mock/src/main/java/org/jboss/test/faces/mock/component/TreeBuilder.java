package org.jboss.test.faces.mock.component;

import javax.faces.component.UIComponent;

public interface TreeBuilder<C extends UIComponent> {


    TreeBuilder<C> id(String id);
    
    TreeBuilder<C> children(TreeBuilder<?> ...builders);

    TreeBuilder<C> facets(Facet<?> ...facets);

    void replay();

    void verify();

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the component
     */
    C getComponent();

    void visitTree(TreeVisitor visitor);

}