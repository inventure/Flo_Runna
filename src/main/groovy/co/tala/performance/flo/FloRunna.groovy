package co.tala.performance.flo

import co.tala.performance.async.IParallels
import co.tala.performance.async.Parallels
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

/**
 * Convert your Spock tests into Load tests. Use [FloRunna] to simulate a workflow in parallel
 * to create load on a service.
 * @param < T >
 */
class FloRunna<T> {
    private final IParallels parallels
    private final FloRunnaSettings settings
    private final IFlotility flotility
    private final IFloWriter floWriter

    FloRunna(FloRunnaSettings settings) {
        parallels = new Parallels()
        this.settings = settings
        this.flotility = new Flotility(settings)
        this.floWriter = new FloWriter(settings)
    }

    FloRunna(
        IParallels parallels,
        FloRunnaSettings settings,
        IFlotility flotility,
        IFloWriter floWriter
    ) {
        this.parallels = parallels
        this.settings = settings
        this.flotility = flotility
        this.floWriter = floWriter
    }

    /**
     * Executes a [WorkFlo] in parallel as a load test. Pass a [Closure<WorkFlo<T>>], which will be run repetitively
     * in parallel until the end of the test run. The threads, duration, and rampup are defined in [FloRunnaSettings].
     * @param workFloClosure
     * @return
     */
    Map<String, FloExecutionResult<T>> execute(
        @ClosureParams(value = SimpleType.class, options = "co.tala.performance.flo.WorkFloBuilder")
            Closure<WorkFlo<T>> workFloClosure
    ) {
        Map<String, FloExecutionResult<T>> results
        final int threads = settings.threads
        final int iterations = settings.iterations
        final long duration = settings.duration
        final long rampup = settings.rampup
        final Instant startTime = now
        final def getElapsed = { now.toEpochMilli() - startTime.toEpochMilli() }
        AtomicInteger currentIteration = new AtomicInteger(1)

        try {
            int iterationsPerThread = Math.round(iterations / threads).toInteger()

            while (getElapsed() < duration || currentIteration <= iterations) {
                final long elapsed = getElapsed()
                final int activeThreadCount = elapsed > rampup ? threads : (Math.ceil(threads * (elapsed / rampup))).toInteger()
                // each thread gets a certain chunk of work to do
                AtomicInteger iterationChunkCount = new AtomicInteger(1)

                while (parallels.activeThreadCount < activeThreadCount) {
                    parallels.runAsync {
                        if (iterations > 0) {
                            // additional check on currentIteration for times when the iterations do not break
                            // down into nice chucks with the number of threads under test. So if we have 5
                            // iterations, but only 3 threads, the third thread will only need 1 iteration.
                            while (iterationChunkCount.get() <= iterationsPerThread && (currentIteration <= iterations)) {
                                currentIteration.set(currentIteration.incrementAndGet())
                                iterationChunkCount.set(iterationChunkCount.incrementAndGet())
                                WorkFlo<T> workFlo = workFloClosure(new WorkFloBuilder<T>())
                                flotility.executeWorkFlo(workFlo)
                            }
                        } else {
                            // basically running only on duration and not utilizing iterations
                            WorkFlo<T> workFlo = workFloClosure(new WorkFloBuilder<T>())
                            flotility.executeWorkFlo(workFlo)
                        }
                    }
                }

                Thread.sleep(10)
                if (iterations > 0 && currentIteration > iterations) {
                    break
                }
            }
            parallels.waitAll()
        }
        finally {
            final Instant endTime = now
            results = flotility.results.toFloExecutionResults(startTime, endTime)
            if (settings.outputEnabled) {
                floWriter.writeResults(results)
            }
        }
        results
    }

    private static Instant getNow() { Instant.now() }
}
