/**
 * 
 */
package org.jboss.test.faces.staging;

import javax.el.ELContextListener;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;

import org.jboss.test.faces.TestException;


/**
 * @author asmirnov
 *
 */
public class StaggingJspApplicationContext implements JspApplicationContext {
	
	public static final String FACES_EXPRESSION_FACTORY = "com.sun.faces.expressionFactory";
    public static final String SUN_EXPRESSION_FACTORY="com.sun.el.ExpressionFactoryImpl";
    public static final String JBOSS_EXPRESSION_FACTORY="org.jboss.el.ExpressionFactoryImpl";
	
	private ExpressionFactory expressionFactory ;
	private final ServletContext servletContext;
	

	public StaggingJspApplicationContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		String elFactoryClass = servletContext.getInitParameter(FACES_EXPRESSION_FACTORY);
		if(null == elFactoryClass){
			elFactoryClass = servletContext.getInitParameter("com.sun.el.ExpressionFactoryImpl");
		}
        try {
		if(null == elFactoryClass){
		    try {
		        expressionFactory = instantiate(SUN_EXPRESSION_FACTORY);
		    } catch (ClassNotFoundException e) {
		        expressionFactory = instantiate(JBOSS_EXPRESSION_FACTORY);
            }
		} else {
			expressionFactory = instantiate(elFactoryClass);
		}
		} catch (Exception e) {
			throw new TestException("Couldn't instantiate EL expression factory",e);
		}
	}

    private ExpressionFactory instantiate(String elFactoryClass) throws InstantiationException, IllegalAccessException,
        ClassNotFoundException {
        return Class.forName(elFactoryClass).asSubclass(ExpressionFactory.class).newInstance();
    }

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.JspApplicationContext#addELContextListener(javax.el.ELContextListener)
	 */
	public void addELContextListener(ELContextListener listener) {

	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.JspApplicationContext#addELResolver(javax.el.ELResolver)
	 */
	public void addELResolver(ELResolver resolver) {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.JspApplicationContext#getExpressionFactory()
	 */
	public ExpressionFactory getExpressionFactory() {
		return expressionFactory;
	}

}
