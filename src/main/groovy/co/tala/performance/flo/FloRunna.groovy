package co.tala.performance.flo

import co.tala.performance.async.IParallels
import co.tala.performance.async.Parallels
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import java.time.Instant

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
        final long duration = settings.duration
        final long rampup = settings.rampup
        final Instant startTime = now
        final def getElapsed = { now.toEpochMilli() - startTime.toEpochMilli() }

        try {
            while (getElapsed() < duration) {
                final long elapsed = getElapsed()
                final int activeThreadCount = elapsed > rampup ? threads : (Math.ceil(threads * (elapsed / rampup))).toInteger()
                while (parallels.activeThreadCount < activeThreadCount) {
                    parallels.runAsync {
                        WorkFlo<T> workFlo = workFloClosure(new WorkFloBuilder<T>())
                        flotility.executeWorkFlo(workFlo)
                    }
                }
                sleep(10)
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
