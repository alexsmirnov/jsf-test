/**
 * 
 */
package org.jboss.test.faces.staging;

import java.util.EventListener;

/**
 * Internal interface, used to invoke listeners of different types.

 * @author asmirnov
 *
 * @param <T> listener type.
 */
interface EventInvoker<T extends EventListener> {
	public void invoke(T listener);
}