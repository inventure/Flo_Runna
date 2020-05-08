package co.tala.performance.flo

import groovy.transform.PackageScope
import groovy.transform.Synchronized

import java.time.Instant

/**
 * Stores all performance results for all [FloStep]s
 * @param <T >
 */
@PackageScope
class FloStepResultStorage<T> implements IFloStepResultStorage<T> {
    private final Object addResultLock = new Object()
    private final Map<String, FloStepResults<T>> map
    private final FloRunnaSettings settings

    FloStepResultStorage(FloRunnaSettings settings) {
        this.settings = settings
        map = [:]
    }

    /**
     * Adds a [FloStepResult]
     * @param floStep
     * @param result
     */
    //NOTE: Only one thread at a time is allowed to access this method to prevent race conditions.
    @Override
    @Synchronized("addResultLock")
    void addResult(FloStep floStep, FloStepResult<T> result) {
        if (!map.containsKey(floStep.name))
            map[floStep.name] = new FloStepResults<T>(floStep.orderNumber)
        map[floStep.name].results << result
    }

    /**
     * Converts [FloStepResultStorage] to [Map<String, FloExecutionResult<T>>]
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    Map<String, FloExecutionResult<T>> toFloExecutionResults(Instant startTime, Instant endTime) {
        Map<String, FloExecutionResult<T>> executionResultMap = [:]
        final String executionId = UUID.randomUUID().toString()
        map.each {
            executionResultMap[it.key] = new FloExecutionResult<T>(it.value.orderNumber, it.key, settings, startTime, endTime, executionId, it.value.results)
        }
        executionResultMap
    }
}
