package org.jboss.test.faces.mockito.component;

import javax.faces.component.UIComponent;

public interface TreeBuilder<C extends UIComponent> {


    TreeBuilder<C> id(String id);
    
    TreeBuilder<C> children(TreeBuilder<?> ...builders);

    TreeBuilder<C> facets(Facet<?> ...facets);
    
    C getComponent();

    void visitTree(TreeVisitor visitor);

}