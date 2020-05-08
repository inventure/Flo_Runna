package co.tala.performance.async

import co.tala.performance.exception.AggregateException
import groovy.transform.Synchronized

/**
 * Utility class to handle running multiple tasks in parallel
 */
class Parallels implements IParallels {
    private class PrivateThreads {
        private final Set<Thread> threads
        private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler
        private final List<Throwable> throwables

        PrivateThreads() {
            threads = []
            throwables = []
            uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
                private final Object uncaughtExceptionHandlerLock = new Object()

                @Synchronized("uncaughtExceptionHandlerLock")
                void uncaughtException(Thread th, Throwable ex) {
                    throwables << ex
                }
            }
        }

        void start(Closure action) {
            Thread thread = Thread.start { action() }
            thread.uncaughtExceptionHandler = uncaughtExceptionHandler
            threads << thread
        }

        List<Throwable> waitAll() {
            threads.each { Thread thread -> thread.join() }
            threads.clear()
            throwables
        }
    }

    private final PrivateThreads threads

    Parallels() {
        this.threads = new PrivateThreads()
    }

    /**
     * Runs a task asynchronously
     * @param action
     * @return
     */
    @Override
    IParallels runAsync(Closure action) {
        threads.start { action() }
        this
    }

    /**
     * Gets the count of threads that are not terminated
     * @return
     */
    @Override
    int getActiveThreadCount() {
        Set<Thread> terminatedThreads = threads.threads.findAll { it.state == Thread.State.TERMINATED }
        threads.threads.removeAll(terminatedThreads)
        threads.threads.size()
    }

    /**
     * Waits for all tasks to complete. If any exception is thrown on any Thread, a [PowerAssertionFailure] will be thrown
     */
    @Override
    void waitAll() {
        List<Throwable> throwables = threads.waitAll()
        if (!throwables.isEmpty())
            throw new AggregateException(throwables)
    }
}
