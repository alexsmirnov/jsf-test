package org.jboss.test.faces.staging;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.jsp.JspFactory;

import org.jboss.test.faces.ApplicationServer;
import org.jboss.test.faces.FilterHolder;
import org.jboss.test.faces.ServletHolder;
import org.jboss.test.faces.TestException;

/**
 * This class implements limited Http servlet container 2.5 functionality. It is
 * designed for a test purposes only ,so that has a limitations:
 * <ul>
 * <li>supports local calls only.</li>
 * <li>java code only configuration ( no xml files processed ).</li>
 * <li>just one web application, 'deployed' in the root context.</li>
 * <li>only one client session</li>
 * <li>communicates by the local java calls only, no network connection</li>
 * <li>no JSP compilator support ( but it is possible to register pre-compiled
 * pages as servlets)</li>
 * <li>...</li>
 * </ul>
 * It is main part of the test framework.
 * 
 */
public class StagingServer extends ApplicationServer {

	private static final Class<ServletRequestListener> REQUEST_LISTENER_CLASS = ServletRequestListener.class;

	private static final Class<ServletRequestAttributeListener> REQUEST_ATTRIBUTE_LISTENER_CLASS = ServletRequestAttributeListener.class;

	private static final Class<ServletContextListener> CONTEXT_LISTENER_CLASS = ServletContextListener.class;

	private static final Class<HttpSessionListener> SESSION_LISTENER_CLASS = HttpSessionListener.class;

	private static final Class<HttpSessionAttributeListener> SESSION_ATTRIBUTE_LISTENER_CLASS = HttpSessionAttributeListener.class;

	private static final Logger log = ServerLogger.SERVER.getLogger();

	private final List<RequestChain> servlets = new ArrayList<RequestChain>();

	private RequestChain defaultServlet = new ServletContainer(null,
			new StaticServlet());

	private final List<EventListener> contextListeners = new ArrayList<EventListener>();

	private final Map<String, String> initParameters = new HashMap<String, String>();

	private final ServerResourceDirectory serverRoot = new ServerResourceDirectoryImpl();

	private final Map<String, String> mimeTypes = new HashMap<String, String>();

	private InvocationListener invocationListener;

	private final StagingServletContext context = new LocalContext();

	private ServletContext contextProxy;

	private HttpSession currentSession = null;
	
	private ThreadLocal<HttpSession> sessions = new ThreadLocal<HttpSession>();
	
	private List<ServerHttpSession> sessionInstances = new ArrayList<ServerHttpSession>();
	
	private boolean sessionPerThread = false;

	private int port = 0 /* this server doesn't operate with network ports, so any will suit */;

	private boolean initialised = false;

	/**
	 * This inner class links ServletContext calls to the server instance.
	 * 
	 * @author asmirnov
	 * 
	 */
	private class LocalContext extends StagingServletContext {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
		 */
		public String getMimeType(String file) {
			int indexOfDot = file.lastIndexOf('.');
			// get extension.
			if (indexOfDot >= 0) {
				file = file.substring(indexOfDot);
			}
			return mimeTypes.get(file);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.test.faces.staging.StagingServletContext#valueBound(javax
		 * .servlet.ServletContextAttributeEvent)
		 */
		@Override
		protected void valueBound(ServletContextAttributeEvent event) {
			// inform listeners.
			for (EventListener listener : contextListeners) {
				if (listener instanceof ServletContextAttributeListener) {
					ServletContextAttributeListener contextListener = (ServletContextAttributeListener) listener;
					contextListener.attributeAdded(event);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.test.faces.staging.StagingServletContext#valueReplaced(javax
		 * .servlet.ServletContextAttributeEvent)
		 */
		@Override
		protected void valueReplaced(ServletContextAttributeEvent event) {
			// inform listeners.
			for (EventListener listener : contextListeners) {
				if (listener instanceof ServletContextAttributeListener) {
					ServletContextAttributeListener contextListener = (ServletContextAttributeListener) listener;
					contextListener.attributeReplaced(event);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.test.faces.staging.StagingServletContext#valueUnbound(javax
		 * .servlet.ServletContextAttributeEvent)
		 */
		@Override
		protected void valueUnbound(ServletContextAttributeEvent event) {
			// inform listeners.
			for (EventListener listener : contextListeners) {
				if (listener instanceof ServletContextAttributeListener) {
					ServletContextAttributeListener contextListener = (ServletContextAttributeListener) listener;
					contextListener.attributeRemoved(event);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.test.faces.staging.StagingServletContext#getServerResource
		 * (java.lang.String)
		 */
		@Override
		protected ServerResource getServerResource(String path) {
			return serverRoot.getResource(new ServerResourcePath(path));
		}

	}

	/**
	 * This inner class links session object calls to the server instance.
	 * 
	 * @author asmirnov
	 * 
	 */
	private class ServerHttpSession extends StagingHttpSession {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.servlet.http.HttpSession#getServletContext()
		 */
		public ServletContext getServletContext() {
			return context;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.test.faces.staging.StagingHttpSession#valueBound(javax.servlet
		 * .http.HttpSessionBindingEvent)
		 */
		@Override
		protected void valueBound(
				final HttpSessionBindingEvent sessionBindingEvent) {
			// inform session listeners.
			fireEvent(SESSION_ATTRIBUTE_LISTENER_CLASS,
					new EventInvoker<HttpSessionAttributeListener>() {
						public void invoke(HttpSessionAttributeListener listener) {
							listener.attributeAdded(sessionBindingEvent);
						}
					});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.test.faces.staging.StagingHttpSession#valueUnbound(javax.
		 * servlet.http.HttpSessionBindingEvent)
		 */
		@Override
		protected void valueUnbound(
				final HttpSessionBindingEvent sessionBindingEvent) {
			// inform session listeners.
			fireEvent(SESSION_ATTRIBUTE_LISTENER_CLASS,
					new EventInvoker<HttpSessionAttributeListener>() {
						public void invoke(HttpSessionAttributeListener listener) {
							listener.attributeRemoved(sessionBindingEvent);
						}
					});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.test.faces.staging.StagingHttpSession#valueReplaced(javax
		 * .servlet.http.HttpSessionBindingEvent)
		 */
		@Override
		protected void valueReplaced(
				final HttpSessionBindingEvent sessionBindingEvent) {
			// inform session listeners.
			fireEvent(SESSION_ATTRIBUTE_LISTENER_CLASS,
					new EventInvoker<HttpSessionAttributeListener>() {
						public void invoke(HttpSessionAttributeListener listener) {
							listener.attributeReplaced(sessionBindingEvent);
						}
					});
		}
		
		@Override
		public void invalidate() {
			super.invalidate();
			setCurrentSession(null);
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private <T extends EventListener> int fireEvent(Class<T> listenerClass,
			EventInvoker<T> invoker) {
	    int errorsCount=0;
		for (EventListener listener : contextListeners) {
			if (listenerClass.isInstance(listener)) {
                try {
                    invoker.invoke((T) listener);
                } catch (Throwable e) {
                    // Application exceptions should not hung server.
                    log.log(Level.SEVERE, "Exception in listener", e);
                    errorsCount++;
                }
            }
        }
		return errorsCount;
	}

	@Override
	protected void addDirectory(String directoryPath) {
	    serverRoot.addDirectory(new ServerResourcePath(directoryPath));
	}
	
    /**
     * Append executable server object ( {@link Filter} or {@link Servlet} to
     * the server.
     * 
     * @see ApplicationServer#addFilter(FilterHolder)
     * @see ApplicationServer#addServlet(ServletHolder)
     * 
     * @param servlet
     */
	public void addServlet(RequestChain servlet) {
		servlets.add(servlet);
	}

    public void replaceServlet(RequestChain oldHandler, RequestChain newHandler) {
        servlets.remove(oldHandler);
        servlets.add(newHandler);
    }

    /**
     * Add servlet to the server.
     * 
     * @see ApplicationServer#addFilter(FilterHolder)
     * @see ApplicationServer#addServlet(ServletHolder)
     * 
     * @param mapping
     *            servlet mapping
     * @param servlet
     *            {@link Servlet} instance.
     */
	public void addServlet(String mapping, Servlet servlet) {
		servlets.add(new ServletContainer(mapping, servlet));
	}

    /**
     * Get appropriate object ( Filter or Servlet ) for a given path.
     * 
     * @param path
     *            request path relative to web application context.
     * @return Appropriate Filter or Servlet executable object to serve given
     *         request. If no servlet was registered for the given path, try to
     *         send requested object directly.
     */
	public RequestChain getServlet(String path) {
		RequestChain result = null;
		for (RequestChain servlet : servlets) {
			if (servlet.isApplicable(path)) {
				result = servlet;
				break;
			}
		}
		if (null == result) {
			// Is requested object exist in the virtual content ?
			try {
				URL resource = context.getResource(path);
				if (null != resource) {
					// Serve it directly.
					result = defaultServlet;
				}
			} catch (MalformedURLException e) {
				log.warning("Mailformed request URL " + e.getMessage());
			}
		}
		return result;
	}

	@Override
    public void addInitParameter(String name, String value) {
		initParameters.put(name, value);
	}

	@Override
    public void addMimeType(String extension, String mimeType) {
		mimeTypes.put(extension, mimeType);
	}

    @Override
    public void addContent(String path, String content) {
        ServerResourcePath resourcePath = new ServerResourcePath(path);
        serverRoot.addResource(resourcePath, new StringContentServerResource(content));
    }
	
	@Override
    public void addResource(String path, String resource) {
		ServerResourcePath resourcePath = new ServerResourcePath(path);
		serverRoot.addResource(resourcePath, new ClasspathServerResource(
				resource));
	}

	@Override
    public void addResource(String path, URL resource) {
		serverRoot.addResource(new ServerResourcePath(path),
				new UrlServerResource(resource));
	}

	@Override
    public void addWebListener(EventListener listener) {
		contextListeners.add(listener);
	}

    /**
     * Getter method for 'interceptor' events listener.
     * 
     * @return the invocationListener
     */
	public InvocationListener getInvocationListener() {
		return invocationListener;
	}

    /**
     * Set listener which gets events on all calls to any methods of the
     * {@link ServletContext}, {@link HttpSession}, {@link HttpServletRequest},
     * {@link HttpServletResponse} instances in the virtual server. this
     * interceptor can be used to check internal calls in the tests .
     * 
     * @param invocationListener
     *            the invocationListener to set
     */
	public void setInvocationListener(InvocationListener invocationListener) {
		this.invocationListener = invocationListener;
	}

	/**
	 * Create instance of the {@link InvocationHandler} for the proxy objects.
	 * This handler fire events to the registered {@link InvocationListener} (
	 * if present ) after target object method call.
	 * 
	 * @return the invocationHandler
	 */
	InvocationHandler getInvocationHandler(final Object target) {
		return new InvocationHandler() {

			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				InvocationListener listener = getInvocationListener();
				try {
					Object result = method.invoke(target, args);
					if (null != listener) {
						listener.afterInvoke(new InvocationEvent(target,
								method, args, result));
					}
					return result;
				} catch (Throwable e) {
					if (null != listener) {
						listener.processException(new InvocationErrorEvent(
								target, method, args, e));
					}
					throw e;
				}
			}

		};

	}

	@Override
    public boolean isSessionPerThread() {
		return sessionPerThread;
	}

	@Override
    public void setSessionPerThread(boolean sessionPerThread) {
		this.sessionPerThread = sessionPerThread;
	}
	
	
	HttpSession getCurrentSession() {
		if (isSessionPerThread()) {
			return sessions.get();
		} else {
			return currentSession;
		}
	}
	
	void setCurrentSession(HttpSession session) {
		if (isSessionPerThread()) {
			sessions.set(session);
		} else {
			this.currentSession=session;
		}
		
	}

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

	@Override
    public synchronized HttpSession getSession(boolean create) {
		if (!initialised) {
			throw new TestException("Staging server have not been initialised");
		}
		HttpSession httpSession = this.getCurrentSession();
		if (null == httpSession && create) {
			ServerHttpSession sessionImpl = new ServerHttpSession();
			// Create proxy objects.
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (null == loader) {
				loader = this.getClass().getClassLoader();
			}
			httpSession = (HttpSession) Proxy.newProxyInstance(loader,
					new Class[] { HttpSession.class },
					getInvocationHandler(sessionImpl));
			setCurrentSession(httpSession);
			// inform session listeners.
			final HttpSessionEvent event = new HttpSessionEvent(httpSession);
			fireEvent(SESSION_LISTENER_CLASS,
					new EventInvoker<HttpSessionListener>() {
						public void invoke(HttpSessionListener listener) {
							listener.sessionCreated(event);
						}
					});
			sessionInstances.add(sessionImpl);
		}
		return httpSession;
	}

	private void readDefaultMimeTypes() {
	    InputStream is = null;
	    try {
	        is = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/jboss/test/faces/staging/mime.properties");
	        Properties props = new Properties();
	        props.load(is);
	        
	        for (Object key : props.keySet()) {
                mimeTypes.put("." + key, props.getProperty((String) key));
            }
	    } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
	        if (is != null) {
	            try {
                    is.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, e.getMessage(), e);
                }
	        }
	    }
	}
	
	@Override
    public void init() {
        log.info("Init staging server");

        readDefaultMimeTypes();
		
		// Create Jsp factory
		JspFactory.setDefaultFactory(new StaggingJspFactory(this.context));
		// Create init parameters
		context.addInitParameters(initParameters);
		// Inform listeners
		// Create proxy objects.
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (null == loader) {
			loader = this.getClass().getClassLoader();
		}
		this.contextProxy = (ServletContext) Proxy.newProxyInstance(loader,
				new Class[] { ServletContext.class },
				getInvocationHandler(context));
		// Create default servlet
		final ServletContextEvent event = new ServletContextEvent(context);
		if(fireEvent(CONTEXT_LISTENER_CLASS,
				new EventInvoker<ServletContextListener>() {
					public void invoke(ServletContextListener listener) {
						listener.contextInitialized(event);
					}
				})>0){
            throw new TestException("Server not started due to listener error ");
		}
		// Init servlets
		try {
			for (RequestChain servlet : servlets) {
				// init servlet
				servlet.init(context);
			}
			defaultServlet.init(context);
		} catch (ServletException e) {
			throw new TestException("Servlet initialisation error ", e);
		}
		try {
			NamingManager.setInitialContextFactoryBuilder(new StagingInitialContextFactoryBuilder());
		} catch (NamingException e) {
			log.warning("Error set initial context factory builder.");
		} catch (IllegalStateException e) {
			log.warning("Initial context factory builder already set.");
		}
		this.initialised = true;
	}

	@Override
    public void destroy() {
		if (!initialised) {
			throw new TestException("Staging server have not been initialised");
		}
		this.initialised = false;
		// Destroy session
		// TODO - destroy all threads.
		for (Iterator<ServerHttpSession> sessionIterator = sessionInstances.iterator(); sessionIterator.hasNext();) {
			ServerHttpSession session = sessionIterator.next();
			// inform session listeners.
			final HttpSessionEvent event = new HttpSessionEvent(session);
			fireEvent(SESSION_LISTENER_CLASS,
					new EventInvoker<HttpSessionListener>() {
						public void invoke(HttpSessionListener listener) {
							listener.sessionDestroyed(event);
						}
					});
			session.invalidate();
			sessionIterator.remove();
		}
		setCurrentSession(null);
		// Inform listeners
		final ServletContextEvent event = new ServletContextEvent(context);
		fireEvent(CONTEXT_LISTENER_CLASS,
				new EventInvoker<ServletContextListener>() {
					public void invoke(ServletContextListener listener) {
						listener.contextDestroyed(event);
					}
				});
		// Destroy servlets
		for (RequestChain servlet : servlets) {
			servlet.destroy();
		}
		defaultServlet.destroy();
		// Clear Jsp factory
		JspFactory.setDefaultFactory(null);
		this.contextProxy = null;
		log.info("Staging server have been destroyed");
	}

    /**
     * Get virtual connection to the given URL. Even thought for an http request
     * to the external servers, only local connection to the virtual server will
     * be created.
     * 
     * @param url
     *            request url.
     * @return local connection to the appropriate servlet in the virtual
     *         server.
     * @throws {@link TestException} if no servlet found to process given URL.
     */
	@Override
    public StagingConnection getConnection(URL url) {
		if (!initialised) {
			throw new TestException("Staging server have not been initialised");
		}
		return new StagingConnection(this, url);
	}

	@Override
    public ServletContext getContext() {
		if (!initialised) {
			throw new TestException("Staging server have not been initialised");
		}
		return contextProxy;
	}

	/**
	 * Inform {@link ServletRequestListener} instances. For internal use only.
	 * 
	 * @param request
	 *            started request.
	 */
	void requestStarted(ServletRequest request) {
		final ServletRequestEvent event = new ServletRequestEvent(context,
				request);
		fireEvent(REQUEST_LISTENER_CLASS,
				new EventInvoker<ServletRequestListener>() {
					public void invoke(ServletRequestListener listener) {
						listener.requestInitialized(event);

					}
				});
	}

	/**
	 * Inform {@link ServletRequestListener} instances. For internal use only.
	 * 
	 * @param request
	 *            finished request.
	 */
	void requestFinished(ServletRequest request) {
		final ServletRequestEvent event = new ServletRequestEvent(context,
				request);
		fireEvent(REQUEST_LISTENER_CLASS,
				new EventInvoker<ServletRequestListener>() {
					public void invoke(ServletRequestListener listener) {
						listener.requestDestroyed(event);
					}
				});
	}

	void requestAttributeAdded(ServletRequest request, String name, Object o) {
		final ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(
				context, request, name, o);
		fireEvent(REQUEST_ATTRIBUTE_LISTENER_CLASS,
				new EventInvoker<ServletRequestAttributeListener>() {
					public void invoke(ServletRequestAttributeListener listener) {
						listener.attributeAdded(event);
					}
				});
	}

	void requestAttributeRemoved(ServletRequest request, String name,
			Object removed) {
		final ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(
				context, request, name, removed);
		fireEvent(REQUEST_ATTRIBUTE_LISTENER_CLASS,
				new EventInvoker<ServletRequestAttributeListener>() {
					public void invoke(ServletRequestAttributeListener listener) {
						listener.attributeRemoved(event);
					}
				});
	}

	void requestAttributeReplaced(ServletRequest request, String name,
			Object value) {
		final ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(
				context, request, name, value);
		fireEvent(REQUEST_ATTRIBUTE_LISTENER_CLASS,
				new EventInvoker<ServletRequestAttributeListener>() {
					public void invoke(ServletRequestAttributeListener listener) {
						listener.attributeReplaced(event);
					}
				});
	}

    @Override
    public void addFilter(FilterHolder filterHolder) {
        Map<String, String> initParameters = filterHolder.getInitParameters();
        String mapping = filterHolder.getMapping();
        String name = filterHolder.getName();
        Filter filter = filterHolder.getFilter();
        
        RequestChain oldHandler = getServlet(mapping);
        FilterContainer newHandler = new FilterContainer(filter, oldHandler);
        newHandler.setName(name);
        
        if (initParameters != null) {
            for (Entry<String, String> initEntry : initParameters.entrySet()) {
                newHandler.addInitParameter(initEntry.getKey(), initEntry.getValue());
            }
        }
        
        replaceServlet(oldHandler, newHandler);
    }

    @Override
    public void addServlet(ServletHolder servletHolder) {
        Map<String, String> initParameters = servletHolder.getInitParameters();
        String mapping = servletHolder.getMapping();
        String name = servletHolder.getName();
        Servlet servlet = servletHolder.getServlet();
        
        ServletContainer servletContainer = new ServletContainer(mapping, servlet);
        servletContainer.setName(name);
        
        if (initParameters != null) {
            for (Entry<String, String> initEntry : initParameters.entrySet()) {
                servletContainer.addInitParameter(initEntry.getKey(), initEntry.getValue());
            }
        }
    
        addServlet(servletContainer);
    }
    
    @Override
    public int getPort() {
        return port;
    }
    
}
