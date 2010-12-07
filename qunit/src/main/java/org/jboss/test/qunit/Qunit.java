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

package org.jboss.test.qunit;

import java.net.URL;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * <p class="changed_added_4_0">This class represents qunit test environment ( html page, java script libraries ... ) </p>
 * @author asmirnov@exadel.com
 *
 */
public class Qunit implements MethodRule {
    
    /**
     * <p class="changed_added_4_0">Html content for JavaScript tests.</p>
     * 
     */
    private String htmlContent;
    
    private Iterable<URL> scripts;

    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {
            
            @Override
            public void evaluate() throws Throwable {
                try {
                    setupQunit(method,target);
                    base.evaluate();
                } finally {
                    thearDownQunit(method,target);
                }
                
            }
        };
    }

    protected void thearDownQunit(FrameworkMethod method, Object target) {
        // TODO Auto-generated method stub
        
    }

    protected void setupQunit(FrameworkMethod method, Object target) {
        // TODO Auto-generated method stub
        
    }

}
