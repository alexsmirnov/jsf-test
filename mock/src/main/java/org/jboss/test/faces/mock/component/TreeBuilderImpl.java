/*
 * $Id$
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.test.faces.mock.component;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.easymock.Capture;
import org.easymock.IAnswer;
import org.jboss.test.faces.mock.FacesMock;

/**
 * <p class="changed_added_4_0">
 * This class builds tree of mock components
 * </p>
 * 
 * @author asmirnov@exadel.com
 * 
 */
public class TreeBuilderImpl<C extends UIComponent> implements TreeBuilder<C> {

    private final C component;

    private final List<TreeBuilderImpl<?>> childrenBuilders;
    
    private final List<UIComponent> children;
    
    private final Map<String,UIComponent> facets;

    protected TreeBuilderImpl(C component) {
        this.component = component;
        this.childrenBuilders = new ArrayList<TreeBuilderImpl<?>>();
        this.children = new ArrayList<UIComponent>();
        this.facets = new HashMap<String, UIComponent>();
        expect(component.getChildren()).andStubReturn(children);
        expect(component.getFacets()).andStubReturn(facets);
        expect(component.getChildCount()).andStubAnswer(new IAnswer<Integer>() {

            public Integer answer() throws Throwable {
                return children.size();
            }
        });
        final Capture<String> facetNameCapture = new Capture<String>();
        expect(component.getFacet(capture(facetNameCapture))).andStubAnswer(new IAnswer<UIComponent>() {

            public UIComponent answer() throws Throwable {
                return facets.get(facetNameCapture.getValue());
            }
        });
        expect(component.getFacetCount()).andStubAnswer(new IAnswer<Integer>() {

            public Integer answer() throws Throwable {
                return facets.size();
            }
        });
        expect(component.getFacetsAndChildren()).andStubAnswer(new IAnswer<Iterator<UIComponent>>() {

            public Iterator<UIComponent> answer() throws Throwable {
                ArrayList<UIComponent> facetsAndChildren = new ArrayList<UIComponent>(facets.values());
                facetsAndChildren.addAll(children);
                return facetsAndChildren.iterator();
            }
        });
    }


    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#addChild()
     */
    public TreeBuilder<UIComponent> addChild() {
        return addChild(FacesMock.createMock(UIComponent.class));
    }

    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#addChild(java.lang.Class)
     */
    public <T extends UIComponent> TreeBuilder<T> addChild(Class<T> childType) {
        return addChild(FacesMock.createMock(childType));
    }

    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#addChild(javax.faces.component.UIComponent)
     */
    public <T extends UIComponent> TreeBuilder<T> addChild(T child) {
        children.add(child);
        TreeBuilderImpl<T> treeBuilder = createChild(child);
        return treeBuilder;
    }


    protected <T extends UIComponent> TreeBuilderImpl<T> createChild(T child) {
        expect(child.getParent()).andStubReturn(component);
        TreeBuilderImpl<T> treeBuilder = new TreeBuilderImpl<T>(child);
        childrenBuilders.add(treeBuilder);
        return treeBuilder;
    }

    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#addFacet(java.lang.String)
     */
    public TreeBuilder<UIComponent> addFacet(String name) {
        return addFacet(name, FacesMock.createMock(UIComponent.class));
    }

    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#addFacet(java.lang.String, java.lang.Class)
     */
    public <T extends UIComponent> TreeBuilder<T> addFacet(String name, Class<T> childType) {
        return addFacet(name, FacesMock.createMock(childType));
    }

    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#addFacet(java.lang.String, javax.faces.component.UIComponent)
     */
    public <T extends UIComponent> TreeBuilder<T> addFacet(String name, T child) {
        facets.put(name, child);
        TreeBuilderImpl<T> treeBuilder = createChild(child);
        return treeBuilder;
    }

    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#setId(java.lang.String)
     */
    public TreeBuilder<C> setId(String id) {
        expect(component.getId()).andStubReturn(id);
        return this;
    }

    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#replay()
     */
    public void replay() {
        visitTree(new TreeVisitor() {
            public void visit(TreeBuilder<?> toVisit) {
                FacesMock.replay(toVisit.getComponent());
            }
        });
    }

    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#verify()
     */
    public void verify() {
        visitTree(new TreeVisitor() {
            public void visit(TreeBuilder<?> toVisit) {
                FacesMock.verify(toVisit.getComponent());
            }
        });
    }

    public void visitTree(TreeVisitor visitor) {
        visitor.visit(this);
        for (TreeBuilder<?> child : childrenBuilders) {
            child.visitTree(visitor);
        }
    }

    /* (non-Javadoc)
     * @see org.jboss.test.faces.mock.component.TreeBuilder#getComponent()
     */
    public C getComponent() {
        return this.component;
    }
}
