package org.jboss.test.faces.mockito.runner;

import static org.jboss.test.faces.annotation.Environment.Feature.*;

import java.lang.reflect.Field;
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
import org.junit.rules.MethodRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.runners.util.FrameworkUsageValidator;

// TODO cleanup of injections
public class FacesMockitoRunner extends BlockJUnit4ClassRunner {

    private MockFacesEnvironment environment;

    private MethodRule rule = new MethodRule() {
        public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
            return new Statement() {

                @Override
                public void evaluate() throws Throwable {
                    runBefore(target);
                    base.evaluate();
                    runAfter(target);
                }
            };
        };
    };

    public FacesMockitoRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.addListener(new FrameworkUsageValidator(notifier));
        super.run(notifier);
    }

    @Override
    protected List<MethodRule> rules(Object test) {
        List<MethodRule> list = super.rules(test);
        list.add(0, rule);
        return list;
    }

    protected void runBefore(Object target) {
        MockitoAnnotations.initMocks(target);
        
        environment = new MockFacesEnvironment();
        processInjections(target);
        processFeatures(target);
    }

    protected void runAfter(Object target) {
        environment.release();
        environment = null;
    }

    private void processFeatures(Object target) {
        Environment annotation = target.getClass().getAnnotation(Environment.class);
        Environment.Feature[] features;
        if (annotation != null) {
            features = annotation.value();
        } else {
            features = Environment.Feature.values();
        }

        for (Environment.Feature feature : features) {
            initializeFeature(feature);
        }
    }

    private void processInjections(Object target) {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                Object injection = getInjection(field.getType());
                if (injection != null) {
                    inject(field, target, injection);
                }
            }
        }
    }

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

    private void initializeFeature(Environment.Feature feature) {
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
}
