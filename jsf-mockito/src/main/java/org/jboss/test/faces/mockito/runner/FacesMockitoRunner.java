/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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
package org.jboss.test.faces.mockito.runner;

import static org.jboss.test.faces.annotation.Environment.Feature.APPLICATION;
import static org.jboss.test.faces.annotation.Environment.Feature.EL_CONTEXT;
import static org.jboss.test.faces.annotation.Environment.Feature.EXTERNAL_CONTEXT;
import static org.jboss.test.faces.annotation.Environment.Feature.FACES_CONTEXT;
import static org.jboss.test.faces.annotation.Environment.Feature.FACTORIES;
import static org.jboss.test.faces.annotation.Environment.Feature.RENDER_KIT;
import static org.jboss.test.faces.annotation.Environment.Feature.RESPONSE_WRITER;
import static org.jboss.test.faces.annotation.Environment.Feature.SERVLET_REQUEST;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.PartialViewContextFactory;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;
import javax.faces.view.facelets.TagHandlerDelegateFactory;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.test.faces.annotation.Environment;
import org.jboss.test.faces.mockito.MockFacesEnvironment;
import org.jboss.test.faces.writer.RecordingResponseWriter;
import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.runners.util.FrameworkUsageValidator;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * <p>
 * JUnit runner for running mocked JSF environment.
 * </p>
 * 
 * <p>
 * It initialized {@link MockFacesEnvironment} and is able to inject mocked JSF classes held by {@link MockFacesEnvironment} to
 * current test instance.
 * </p>
 * 
 * <p>
 * Similarly to {@link MockitoJUnitRunner}, it uses {@link MockitoAnnotations#initMocks(Object)} on test instance to mock all
 * the test dependencies annotated by Mockito annotations.
 * </p>
 * 
 * 
 * TODO cleanup of injections
 * 
 * TODO create tests for failures
 * 
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 */
public class FacesMockitoRunner extends BlockJUnit4ClassRunner {

    /** The mocked JSF environment. */
    private MockFacesEnvironment environment;

    /**
     * Instantiates a new faces mockito runner.
     * 
     * @param klass the class
     * @throws InitializationError the initialization error
     */
    public FacesMockitoRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.junit.runners.ParentRunner#run(org.junit.runner.notification.RunNotifier)
     */
    @Override
    public void run(RunNotifier notifier) {
        notifier.addListener(new FrameworkUsageValidator(notifier));
        super.run(notifier);
    }

    @Override
    protected Statement withBefores(final FrameworkMethod method, final Object target, final Statement originalStatement) {
        final Statement onlyBefores = super.withBefores(method, target, new EmptyStatement());
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                runBefore(target);
                onlyBefores.evaluate();
                originalStatement.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfters(final FrameworkMethod method, final Object target, final Statement originalStatement) {
        final Statement onlyAfters = super.withAfters(method, target, new EmptyStatement());

        final Statement runnerAfters = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                runAfter(target);
            }
        };

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                multiExecute(originalStatement, onlyAfters, runnerAfters);
            }
        };
    }

    /**
     * Run before each test
     * 
     * @param target the target test instance
     */
    protected void runBefore(Object target) {
        MockitoAnnotations.initMocks(target);

        environment = new MockFacesEnvironment();
        processInjections(target);
        processFeatures(target);
    }

    /**
     * Run after each test
     * 
     * @param target the target test instance
     */
    protected void runAfter(Object target) {
        environment.release();
        environment = null;
    }

    /**
     * Process features.
     * 
     * @param target the target test instance
     */
    private void processFeatures(Object target) {
        Environment annotation = target.getClass().getAnnotation(Environment.class);
        Environment.Feature[] features = new Environment.Feature[0];
        if (annotation != null) {
            features = annotation.value();
        }

        for (Environment.Feature feature : features) {
            initializeFeature(feature);
        }
    }

    /**
     * Process injections.
     * 
     * @param target the target test instance
     */
    private void processInjections(Object target) {
        for (Class<?> clazz = target.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getAnnotation(Inject.class) != null) {
                    Object injection = getInjection(field.getType());
                    if (injection != null) {
                        inject(field, target, injection);
                    }
                }
            }
        }
    }

    /**
     * Injects the given injection to field of target test instance.
     * 
     * @param field the field
     * @param target the target
     * @param injection the injection
     */
    private void inject(Field field, Object target, Object injection) {
        boolean accessible = field.isAccessible();
        if (!accessible) {
            field.setAccessible(true);
        }
        try {
            field.set(target, injection);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot inject value to " + field, e);
        }
        if (!accessible) {
            field.setAccessible(false);
        }
    }

    /**
     * Initialize feature with currenct mocked environment
     * 
     * @param feature the feature
     */
    private void initializeFeature(Environment.Feature feature) {
        System.out.println("initializaing feature " + feature);
        switch (feature) {
            case FACTORIES:
                environment.withFactories();
                break;
            case APPLICATION:
                environment.withApplication();
                break;
            case EL_CONTEXT:
                environment.withELContext();
                break;
            case EXTERNAL_CONTEXT:
                environment.withExternalContext();
                break;
            case RENDER_KIT:
                environment.withRenderKit();
                break;
            case RESPONSE_WRITER:
                environment.withResponseWriter();
                break;
            case SERVLET_REQUEST:
                environment.withServletRequest();
                break;
            default:
                break;
        }
    }

    /**
     * <p>
     * Tries to get mock for given type.
     * </p>
     * 
     * <p>
     * Before injecting, it initializes all dependencies needed by given mock type by initializing given feature.
     * </p>
     * 
     * @param type the JSF class type to initialized and inject
     * @return the injection or null if given type ais unknown
     */
    private Object getInjection(Class<?> type) {
        if (type == MockFacesEnvironment.class) {
            return environment;
        }
        if (type == FacesContext.class) {
            initializeFeature(FACES_CONTEXT);
            return environment.getFacesContext();
        }
        if (type == Application.class) {
            initializeFeature(APPLICATION);
            return environment.getApplication();
        }
        if (type == ELContext.class) {
            initializeFeature(EL_CONTEXT);
            return environment.getElContext();
        }
        if (type == ApplicationFactory.class) {
            initializeFeature(FACTORIES);
            return environment.getApplicationFactory();
        }
        if (type == ServletContext.class) {
            initializeFeature(SERVLET_REQUEST);
            return environment.getServletContext();
        }
        if (type == ExceptionHandlerFactory.class) {
            initializeFeature(FACTORIES);
            return environment.getExceptionHandlerFactory();
        }
        if (type == ExternalContext.class) {
            initializeFeature(EXTERNAL_CONTEXT);
            return environment.getExternalContext();
        }
        if (type == ExternalContextFactory.class) {
            initializeFeature(FACTORIES);
            return environment.getExternalContextFactory();
        }
        if (type == FacesContextFactory.class) {
            initializeFeature(FACTORIES);
            return environment.getFacesContextFactory();
        }
        if (type == LifecycleFactory.class) {
            initializeFeature(FACTORIES);
            return environment.getLifecycleFactory();
        }
        if (type == PartialViewContextFactory.class) {
            initializeFeature(FACTORIES);
            return environment.getPartialViewContextFactory();
        }
        if (type == RenderKit.class) {
            initializeFeature(RENDER_KIT);
            return environment.getRenderKit();
        }
        if (type == RenderKitFactory.class) {
            initializeFeature(FACTORIES);
            return environment.getRenderKitFactory();
        }
        if (type == HttpServletRequest.class) {
            initializeFeature(SERVLET_REQUEST);
            return environment.getRequest();
        }
        if (type == HttpServletResponse.class) {
            initializeFeature(SERVLET_REQUEST);
            return environment.getResponse();
        }
        if (type == ResponseStateManager.class) {
            initializeFeature(RENDER_KIT);
            return environment.getResponseStateManager();
        }
        if (RecordingResponseWriter.class.isAssignableFrom(type)) {
            initializeFeature(RESPONSE_WRITER);
            return environment.getResponseWriter();
        }
        if (type == TagHandlerDelegateFactory.class) {
            initializeFeature(FACTORIES);
            return environment.getTagHandlerDelegateFactory();
        }
        if (type == ViewHandler.class) {
            initializeFeature(APPLICATION);
            return environment.getViewHandler();
        }
        return null;
    }

    /**
     * A helper to safely execute multiple statements in one.<br/>
     * 
     * Will execute all statements even if they fail, all exceptions will be kept. If multiple {@link Statement}s fail, a
     * {@link MultipleFailureException} will be thrown.
     * 
     * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
     * @version $Revision: $
     */
    private void multiExecute(Statement... statements) throws Throwable {
        List<Throwable> exceptions = new ArrayList<Throwable>();
        for (Statement command : statements) {
            try {
                command.evaluate();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        if (exceptions.isEmpty()) {
            return;
        }
        if (exceptions.size() == 1) {
            throw exceptions.get(0);
        }
        throw new MultipleFailureException(exceptions);
    }

    private static class EmptyStatement extends Statement {
        @Override
        public void evaluate() throws Throwable {
        }
    }
}
