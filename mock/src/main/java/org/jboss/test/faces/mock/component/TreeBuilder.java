package org.jboss.test.faces.mock.component;

import javax.faces.component.UIComponent;

public interface TreeBuilder<C extends UIComponent> {

    TreeBuilder<UIComponent> addChild();

    <T extends UIComponent> TreeBuilder<T> addChild(Class<T> childType);

    <T extends UIComponent> TreeBuilder<T> addChild(T child);

    TreeBuilder<UIComponent> addFacet(String name);

    <T extends UIComponent> TreeBuilder<T> addFacet(String name, Class<T> childType);

    <T extends UIComponent> TreeBuilder<T> addFacet(String name, T child);

    TreeBuilder<C> setId(String id);

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