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
import java.util.Arrays;
import java.util.Iterator;
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
 * Goal which generates EasyMock Java classes.
 *
 * @goal generate-mock
 * @requiresDependencyResolution compile
 * @phase generate-sources
 */
public class GenerateMockMojo
    extends AbstractMojo
{
    /**
     * Top maven project.
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;
    /**
     * Location of the compiled java classes.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * Location of the plugin configuration file.
     * @parameter expression="src/main/config/mocks.xml"
     * @required
     */
    protected File configuration;

    /**
     * Directory where the output Java Files will be located.
     * 
     * @parameter expression="${project.build.directory}/generated-sources/mocks"
     */
    protected File outputJavaDirectory;

    /**
     * Project classpath.
     * 
     * @parameter expression="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    protected List<String> classpathElements;
    
    /**
      * <p class="changed_added_4_0">Project classloader that able to load classes from project dependencies.</p>
      */
    protected ClassLoader projectClassLoader;
    
    /**
      * <p class="changed_added_4_0">Parsed generator configuration.</p>
      */
    protected MockConfig config;

    public void execute()
        throws MojoExecutionException
    {
        File f = outputJavaDirectory;
        // Prepare output directory.
        if ( !f.exists() )
        {
            f.mkdirs();
        }
        config = readConfig();
        projectClassLoader = createProjectClassLoader();
        MockControlSource mockControl = new MockControlSource(outputJavaDirectory, config.getMockController());
        mockControl.printFileHeader();
        for (Mock mock : (List<Mock>)config.getMocks()) {
            generateClass(mock);
            mockControl.printMockClass(mock.getName());
        }
        mockControl.printFileFooter();
        try {
            mockControl.writeClassFile();
        } catch (IOException e) {
            throw new MojoExecutionException("Error writing mock controller Java source", e);
        }
        // Tell project about generated files.
        project.addCompileSourceRoot(outputJavaDirectory.getAbsolutePath());            
    }

    private static final List<String> systemMethods = Arrays.asList("toString","equals","hashCode","getClass","wait","notify","notifyAll");
    /**
     * <p class="changed_added_4_0">Generate single Mock class</p>
     * @param config
     * @param mock
     */
    protected void generateClass(Mock mock) throws MojoExecutionException {
        try {
            String className = mock.getClassName();
            Class<?> baseClass = projectClassLoader.loadClass(className);
            String mockClassName = mock.getName();
            if(null == mockClassName || mockClassName.length()==0){
                // Calculate mock class name.
                int indexOfDot = className.lastIndexOf('.');
                mockClassName = config.getMockPackage()+"."+config.getClassPrefix()+className.substring(indexOfDot+1);
                mock.setName(mockClassName);
            }
            MockJavaSource javaSource = new MockJavaSource(outputJavaDirectory,mockClassName,className,config.getMockController());
            javaSource.printFileHeader();
            // TODO - remove duplicated methods.
            Method[] declaredMethods = baseClass.getMethods();
            for (Method method : declaredMethods) {
                String name = method.getName();
                skipMethod(mock, name);
                if(!systemMethods.contains(name) && !skipMethod(mock, name) && 0 ==(method.getModifiers()&Modifier.STATIC)){
                    javaSource.printMethod(method);
                }
            }
            javaSource.printFileFooter();
            javaSource.writeClassFile();
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Base class for Mock not found",e);
        } catch (IOException e) {
            throw new MojoExecutionException("Error writing Mock class source",e);
        }
        
    }

    @SuppressWarnings("unchecked")
    protected boolean skipMethod(Mock mock, String name) {
        for (MockMethod mockMethod : (List<MockMethod>)mock.getMethods()) {
            if(name.equals(mockMethod.getName())&& mockMethod.isExclude()){
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected ClassLoader createProjectClassLoader() {
        ClassLoader classLoader = null;
        try {
            List<String> classpathElements = project
            .getCompileClasspathElements();
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
        } catch (DependencyResolutionRequiredException e) {
            getLog().error("Dependencies not resolved ", e);
        }

        return classLoader;
    }

    protected MockConfig readConfig() throws MojoExecutionException {
        // Read configuration
        FileInputStream configurationInput=null;
        try {
            MockConfigXpp3Reader reader = new MockConfigXpp3Reader();
            configurationInput = new FileInputStream(configuration);
            MockConfig mockConfig = reader.read(configurationInput);
            return mockConfig;
        } catch(FileNotFoundException e) {
          throw new MojoExecutionException("Configuration file does not exists:"+configuration.getAbsolutePath()); 
        } catch (IOException e) {
            throw new MojoExecutionException("Error readind configuration file: "+configuration.getAbsolutePath(),e); 
        } catch (XmlPullParserException e) {
            throw new MojoExecutionException("Error parsing configuration file: "+configuration.getAbsolutePath(),e); 
        } finally {
            if(null != configurationInput){
                try {
                    configurationInput.close();
                } catch (IOException e) {
                    // Ignore it.
                }
            }
        }
    }
}
