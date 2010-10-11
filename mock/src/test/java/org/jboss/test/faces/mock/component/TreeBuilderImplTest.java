package org.jboss.test.faces.mock.component;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;

import org.jboss.test.faces.mock.FacesMock;
import org.junit.Before;
import org.junit.Test;

public class TreeBuilderImplTest {
    
    private static final String FACET = "facet";
    private TreeBuilderImpl<UIComponent> builder;
    private UIComponent component;
    
    @Before
    public void setUp(){
        component = FacesMock.createMock(UIComponent.class);
        builder = new TreeBuilderImpl<UIComponent>(component);
    }

    @Test
    public void testAddChild() {
        TreeBuilder<UIComponent> child = ViewBuilder.component();
        builder.children(child);
        UIComponent uiComponent = child.getComponent();
        verifySingleChild(uiComponent);
    }

    private void verifySingleChild(UIComponent uiComponent) {
        FacesMock.replay(component,uiComponent);
        assertEquals(1, component.getChildCount());
        assertEquals(0, component.getFacetCount());
        assertEquals(1, component.getChildren().size());
        assertSame(uiComponent,component.getChildren().get(0));
        assertSame(component,uiComponent.getParent());
        Iterator<UIComponent> facetsAndChildren = component.getFacetsAndChildren();
        assertTrue(facetsAndChildren.hasNext());
        assertSame(uiComponent,facetsAndChildren.next());
        assertFalse(facetsAndChildren.hasNext());
        FacesMock.verify(component,uiComponent);
    }

    @Test
    public void testAddChildClassOfT() {
        TreeBuilder<UIInput> child = ViewBuilder.component(UIInput.class);
        builder.children(child);
        UIInput uiComponent = child.getComponent();
        verifySingleChild(uiComponent);
    }

    @Test
    public void testAddChildT() {
        UIInput uiComponent = FacesMock.createMock(UIInput.class);
        TreeBuilder<UIInput> child = ViewBuilder.component(uiComponent);
        builder.children(child);
        assertSame(uiComponent, child.getComponent());
        verifySingleChild(uiComponent);
    }

    @Test
    public void testAddFacetString() {
        Facet<UIComponent> treeBuilder = ViewBuilder.facet(FACET);
        builder.facets(treeBuilder);
        UIComponent uiComponent = treeBuilder.getBuilder().getComponent();
        verifySingleFacet(uiComponent);
    }

    @Test
    public void testAddFacetStringClassOfT() {
        Facet<UIComponent> treeBuilder = ViewBuilder.facet(FACET,UIComponent.class);
        builder.facets(treeBuilder);
        UIComponent uiComponent = treeBuilder.getBuilder().getComponent();
        verifySingleFacet(uiComponent);
    }

    @Test
    public void testAddFacetStringT() {
        UIComponent uiComponent = FacesMock.createMock(UIComponent.class);
        Facet<UIComponent> treeBuilder = ViewBuilder.facet(FACET,uiComponent);
        builder.facets(treeBuilder);
        verifySingleFacet(uiComponent);
    }

    private void verifySingleFacet(UIComponent uiComponent) {
        FacesMock.replay(component,uiComponent);
        assertEquals(0, component.getChildCount());
        assertEquals(1, component.getFacetCount());
        assertEquals(0, component.getChildren().size());
        assertEquals(1, component.getFacets().size());
        assertSame(uiComponent,component.getFacets().get(FACET));
        assertSame(uiComponent,component.getFacet(FACET));
        assertSame(component,uiComponent.getParent());
        Iterator<UIComponent> facetsAndChildren = component.getFacetsAndChildren();
        assertTrue(facetsAndChildren.hasNext());
        assertSame(uiComponent,facetsAndChildren.next());
        assertFalse(facetsAndChildren.hasNext());
        FacesMock.verify(component,uiComponent);
    }

    @Test
    public void testSetId() {
        builder.id(FACET);
        FacesMock.replay(component);
        assertEquals(FACET, component.getId());
        FacesMock.verify(component);
    }

    @Test
    public void testVerify() {
        FacesMock.replay(component);
        assertEquals(0, component.getChildren().size());
        builder.verify();
    }

    @Test
    public void testReplay() {
        builder.replay();
        assertEquals(0, component.getChildren().size());
        FacesMock.verify(component);
    }

    @Test
    public void testVisitTree() {
        final ArrayList<TreeBuilder<?>> builders = new ArrayList<TreeBuilder<?>>();
        TreeBuilder<UIComponent> child = ViewBuilder.component(); 
        builder.children(child);
        builder.visitTree(new TreeVisitor() {            
            public void visit(TreeBuilder<?> toVisit) {
                builders.add(toVisit);
            }
        });
        assertEquals(2, builders.size());
        assertSame(builder, builders.get(0));
        assertSame(child, builders.get(1));
    }

}
