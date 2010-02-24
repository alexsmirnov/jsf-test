/*
 * $Id$
 *
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.mockgenerator;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * <p class="changed_added_4_0"></p>
 * Goal which generates EasyMock Java classes.
 * 
 * @goal generate-test-mock
 * @requiresDependencyResolution test
 * @phase generate-test-sources
 * @author asmirnov@exadel.com
 *
 */
public class GenerateTestMockMojo extends AbstractMockMojo {

    /**
     * Location of the plugin configuration file.
     * 
     * @parameter expression="src/test/config/mocks.xml"
     * @required
     */
    private File config;
    /**
     * Directory where the output Java Files will be located.
     * 
     * @parameter 
     *            expression="${project.build.directory}/generated-sources/test-mocks"
     */
    private File outputJavaDirectory;
    
    /**
     * Project classpath.
     * 
     * @parameter expression="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    private List<String> classpathElements;

    /**
     * Project classpath.
     * 
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readonly
     */
    private List<String> testClasspathElements;

    @Override
    protected void addGeneratedSourcesToProject() {
        // Tell project about generated files.
        project.addTestCompileSourceRoot(getOutputJavaDirectory().getAbsolutePath());
    }

    @Override
    protected File getOutputJavaDirectory() {
        return this.outputJavaDirectory;
    }

    @Override
    protected File getConfig() {
        return config;
    }

    @Override
    protected Collection<String> getClasspathElements() {
        HashSet<String> elements = new HashSet<String>(testClasspathElements);
        return elements;
    }

}
