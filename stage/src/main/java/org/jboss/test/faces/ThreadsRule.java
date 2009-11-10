package org.jboss.test.faces;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import java.util.concurrent.CountDownLatch;

public final class ThreadsRule implements MethodRule {

    public Statement apply(final Statement statement,
            final FrameworkMethod frameworkMethod, final Object o) {
        final Threads concurrent = frameworkMethod.getAnnotation(Threads.class);
        if (concurrent == null)
            return statement;
        else {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    final String name = frameworkMethod.getName();
                    final Thread[] threads = new Thread[concurrent.value()];
                    final CountDownLatch go = new CountDownLatch(1);
                    final CountDownLatch finished = new CountDownLatch(
                            threads.length);
                    for (int i = 0; i < threads.length; i++) {
                        threads[i] = new Thread(new Runnable() {

                            public void run() {
                                try {
                                    go.await();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                                try {
                                    statement.evaluate();
//                                    frameworkMethod.invokeExplosively(o);
                                } catch (Error e) {
                                    throw e;
                                } catch (RuntimeException r) {
                                    throw r;
                                } catch (Throwable throwable) {
                                    RuntimeException r = new RuntimeException(
                                            throwable.getMessage(), throwable);
                                    r.setStackTrace(throwable.getStackTrace());
                                    throw r;
                                } finally {
                                    finished.countDown();
                                }
                            }
                        }, name + "-Thread-" + i);
                        threads[i].start();
                    }
                    go.countDown();
                    finished.await();
                }
            };
        }
    }
}
