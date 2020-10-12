package co.tala.performance.flo

import groovy.transform.PackageScope

import java.time.Instant


/**
 * Utility class for [FloRunna]
 * @param < T >
 */
@PackageScope
class Flotility<T> implements IFlotility<T> {
    private final IFloStepResultStorage<T> results

    Flotility(FloRunnaSettings settings) {
        this.results = new FloStepResultStorage<T>(settings)
    }

    Flotility(IFloStepResultStorage<T> results) {
        this.results = results
    }

    /**
     * Executes a [WorkFlo].
     * If any Exception is thrown, the remaining [FloStep]s will not be executed on the current thread.
     * The Exception message will be stored in the [FloStepResult] for the [FloStep] that failed.
     * @param workFlo
     */
    @Override
    void executeWorkFlo(WorkFlo<T> workFlo) {
        FloError error = null
        final String resultId = UUID.randomUUID().toString()
        Instant beforeWorkFlo = now
        try {
            workFlo.steps.each { FloStep floStep ->
                final Instant beforeAction = now
                try {
                    floStep.action()
                }
                catch (Throwable e) {
                    error = new FloError(floStep.name, e.message)
                    throw e
                }
                finally {
                    final Instant afterAction = now
                    this.results.addResult(floStep, new FloStepResult(beforeAction, afterAction, workFlo.metadata(), error, resultId))
                }
            }
        }
        finally {
            final Instant afterWorkFlo = now
            this.results.addResult(
                new FloStep(FloSource.TOTAL_SUMMARY, {}, Integer.MAX_VALUE),
                new FloStepResult(beforeWorkFlo, afterWorkFlo, workFlo.metadata(), error, resultId)
            )
        }
    }

    @Override
    IFloStepResultStorage<T> getResults() {
        results
    }

    private static Instant getNow() { Instant.now() }
}
