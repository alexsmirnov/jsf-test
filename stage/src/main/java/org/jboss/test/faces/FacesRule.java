package org.jboss.test.faces;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventListener;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.Filter;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.jboss.test.faces.FacesEnvironment.FacesRequest;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * 
 */

/**
 * @author asmirnov
 *
 */
public class FacesRule implements MethodRule,ServletRequestListener {

	private final FacesEnvironment environment;

	/**
	 * 
	 */

	protected FacesRule(FacesEnvironment environment){
		this.environment = environment;
		this.environment.getServer().addWebListener(this);
	}

	public static FacesRule create() {
		return new FacesRule(new FacesEnvironment());
	}

	public static FacesRule create(ApplicationServer server) {
		return new FacesRule(new FacesEnvironment(server));
	}

	public final Statement apply(final Statement base,
			FrameworkMethod method, Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				before();
				try {
					base.evaluate();
				} finally {
					after();
				}
			}
		};
	}

	protected void before() throws Throwable {
		environment.start();
	}

	protected void after() {
		environment.release();
	}
	

	public ApplicationServer getServer() {
		return environment.getServer();
	}

	public FacesRule withFilter(String name, Filter filter) {
		environment.withFilter(name, filter);
		return this;
	}

	public FacesRule withWebRoot(File root) {
		environment.withWebRoot(root);
		return this;
	}

	public FacesRule withWebRoot(URL root) {
		environment.withWebRoot(root);
		return this;
	}

	public FacesRule withWebRoot(String root) {
		environment.withWebRoot(root);
		return this;
	}

	public FacesRule withInitParameter(String name, String value) {
		environment.withInitParameter(name, value);
		return this;
	}

	public FacesRule withResource(String path, String resource) {
		environment.withResource(path, resource);
		return this;
	}

	public FacesRule withResource(String path, URL resource) {
		environment.withResource(path, resource);
		return this;
	}

	public FacesRule withContent(String path, String pageContent) {
		environment.withContent(path, pageContent);
		return this;
	}

	public FacesRule withListener(EventListener listener) {
		environment.getServer().addWebListener(listener);
		return this;
	}

	public void setSessionAttribute(String name,Object value) {
		environment.getServer().getSession().setAttribute(name, value);
	}

	public void setContextAttribute(String name,Object value) {
		environment.getServer().getContext().setAttribute(name, value);
	}

	public Lifecycle getLifecycle() {
		return environment.getLifecycle();
	}

	public Application getApplication() {
		return environment.getApplication();
	}

	public FacesRequest createFacesRequest() throws Exception {
		return environment.createFacesRequest();
	}

	public FacesRequest createFacesRequest(String url)
			throws MalformedURLException, FacesException {
		return environment.createFacesRequest(url);
	}

	public void requestDestroyed(ServletRequestEvent sre) {
	}

	public void requestInitialized(ServletRequestEvent sre) {
	}

}
