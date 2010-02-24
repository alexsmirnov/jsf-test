package org.jboss.mockgenerator;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.mockgenerator.config.Mock;
import org.jboss.mockgenerator.config.MockConfig;
import org.jboss.mockgenerator.config.MockMethod;
import org.jboss.mockgenerator.config.io.xpp3.MockConfigXpp3Reader;

/**
 */
public abstract class AbstractMockMojo extends AbstractMojo {
    /**
     * Top maven project.
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;
    /**
     * Location of the compiled java classes.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * <p class="changed_added_4_0">
     * Project classloader that able to load classes from project dependencies.
     * </p>
     */
    protected ClassLoader projectClassLoader;

    /**
     * <p class="changed_added_4_0">
     * Parsed generator configuration.
     * </p>
     */
    protected MockConfig mockConfig;
    
    protected int errorsCount = 0;

    public void execute() throws MojoExecutionException {
        File f = getOutputJavaDirectory();
        // Prepare output directory.
        if (!f.exists()) {
            f.mkdirs();
        }
        mockConfig = readConfig();
        projectClassLoader = createProjectClassLoader();
        MockControlSource mockControl = new MockControlSource(
                getOutputJavaDirectory(), mockConfig.getMockController());
        mockControl.printFileHeader();
        for (Mock mock : (List<Mock>) mockConfig.getMocks()) {
            generateClass(mock);
            mockControl.printMockClass(mock.getName());
        }
        if(errorsCount>0){
            throw new MojoExecutionException(
                    "Errors during mock classes grneration");
        }
        mockControl.printFileFooter();
        try {
            mockControl.writeClassFile();
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Error writing mock controller Java source", e);
        }
        addGeneratedSourcesToProject();
    }

    protected abstract void addGeneratedSourcesToProject();

    /**
     * <p class="changed_added_4_0"></p>
     * @return the outputJavaDirectory
     */
    protected abstract File getOutputJavaDirectory();

    /**
     * <p class="changed_added_4_0"></p>
     * @return the config
     */
    protected abstract File getConfig();

    /**
     * <p class="changed_added_4_0"></p>
     * @return the classpathElements
     */
    protected abstract Collection<String> getClasspathElements();

    private static final List<String> requiredSystemMethods = Arrays.asList("toString", "equals", "hashCode");

    private static final List<String> systemMethods = Arrays.asList("getClass", "wait", "notify", "notifyAll");

    /**
     * <p class="changed_added_4_0">
     * Generate single Mock class
     * </p>
     * 
     * @param mockConfig
     * @param mock
     */
    protected void generateClass(Mock mock) throws MojoExecutionException {
        try {
            String className = mock.getClassName();
            Class<?> baseClass = projectClassLoader.loadClass(className);
            String mockClassName = mock.getName();
            if (null == mockClassName || mockClassName.length() == 0) {
                // Calculate mock class name.
                int indexOfDot = className.lastIndexOf('.');
                mockClassName = mockConfig.getMockPackage() + "."
                        + mockConfig.getClassPrefix()
                        + className.substring(indexOfDot + 1);
                mock.setName(mockClassName);
            }
            MockJavaSource javaSource = new MockJavaSource(getOutputJavaDirectory(),
                    mockClassName, className, mockConfig.getMockController());
            javaSource.printFileHeader(mock.getPostConstruct());
            List<Method> declaredMethods = getPublicMethods(baseClass);
            for (Method method : declaredMethods) {
                String name = method.getName();
                if (!skipMethod(mock, name)) {
                    javaSource.printMethod(method);
                }
            }
            javaSource.printFileFooter(mock.getCode());
            javaSource.writeClassFile();
        } catch (ClassNotFoundException e) {
            getLog().error("Base class for Mock not found", e);
            errorsCount++;
        } catch (IOException e) {
            getLog().error("Error writing Mock class source",
                    e);
            errorsCount++;
        }

    }

    protected List<Method> getPublicMethods(Class<?> baseClass) {
        Method[] declaredMethods = baseClass.getMethods();
        List<Method> publicMethods = new LinkedList<Method>();
        for (Method method : declaredMethods) {
            List<Method> overridenPublicMethods = new ArrayList<Method>();

            boolean visible = true;
            int modifiers = method.getModifiers();
            // Do not generate non-abstract system , static, and final methods.
            if (requiredSystemMethods.contains(method.getName()) 
                || (systemMethods.contains(method.getName()) && 0 == (modifiers&Modifier.ABSTRACT))
            		|| 0 != (modifiers & (Modifier.STATIC|Modifier.FINAL))) {
                visible = false;
            } else {
            	for (Method otherMethod : publicMethods) {
                    if (method.getName().equals(otherMethod.getName())
                            && method != otherMethod) {
                        // two different methods with same name, check arguments
                        Class<?>[] methodParameters = method
                                .getParameterTypes();
                        Class<?>[] otherMethodParameters = otherMethod
                                .getParameterTypes();
                        if (Arrays.equals(methodParameters,
                                otherMethodParameters)) {
                            // these methods have same signatures, check their
                            // classes.
                            if (method.getDeclaringClass().isAssignableFrom(
                                    otherMethod.getDeclaringClass())) {
                            	
                            	if (!method.getDeclaringClass().equals(otherMethod.getDeclaringClass())) {
                                    // otherMetood overrides method, so we should
                                    // skip
                                    // first declaration.
                                    visible = false;
                                    break;
                            	} else {
                                    if (method.getReturnType().isAssignableFrom(
                                    		otherMethod.getReturnType())) {

                                    	//Java 5+ permits methods that return subtypes of parent's return type
                                        visible = false;
                                        break;
                                    } else {
                                        overridenPublicMethods.add(otherMethod);
                                    }
                            	}
                            } else {
                                overridenPublicMethods.add(otherMethod);
                            }
                        }
                    }
                }
            }

            publicMethods.removeAll(overridenPublicMethods);

            if (visible) {
                publicMethods.add(method);
            }
        }
        return publicMethods;
    }

    @SuppressWarnings("unchecked")
    protected boolean skipMethod(Mock mock, String name) {
        for (MockMethod mockMethod : (List<MockMethod>) mock.getMethods()) {
            if (name.equals(mockMethod.getName()) && mockMethod.isExclude()) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected ClassLoader createProjectClassLoader() {
        ClassLoader classLoader = null;
        try {
            Collection<String> classpathElements = getClasspathElements();
            String outputDirectory = project.getBuild().getOutputDirectory();
            URL[] urls = new URL[classpathElements.size() + 1];
            int i = 0;
            urls[i++] = new File(outputDirectory).toURI().toURL();
            for (Iterator<String> iter = classpathElements.iterator(); iter
                    .hasNext();) {
                String element = iter.next();
                urls[i++] = new File(element).toURI().toURL();
            }
            classLoader = new URLClassLoader(urls);
        } catch (MalformedURLException e) {
            getLog().error("Bad URL in classpath", e);
        }

        return classLoader;
    }

    protected MockConfig readConfig() throws MojoExecutionException {
        // Read configuration
        FileInputStream configurationInput = null;
        try {
            MockConfigXpp3Reader reader = new MockConfigXpp3Reader();
            configurationInput = new FileInputStream(getConfig());
            MockConfig mockConfig = reader.read(configurationInput);
            return mockConfig;
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(
                    "Configuration file does not exists:"
                            + getConfig().getAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Error readind configuration file: "
                            + getConfig().getAbsolutePath(), e);
        } catch (XmlPullParserException e) {
            throw new MojoExecutionException(
                    "Error parsing configuration file: "
                            + getConfig().getAbsolutePath(), e);
        } finally {
            if (null != configurationInput) {
                try {
                    configurationInput.close();
                } catch (IOException e) {
                    // Ignore it.
                }
            }
        }
    }
}
