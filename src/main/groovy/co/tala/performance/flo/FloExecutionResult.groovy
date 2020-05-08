package co.tala.performance.flo

import java.time.Instant

/**
 * Data class to represent all performance metrics for the test run.
 * @param < T >
 */
class FloExecutionResult<T> {
    private transient final List<Long> orderedResponseTimes

    final String testName
    final String floStepName
    final int threads
    final long duration
    final long rampup
    final long perc50
    final long perc80
    final long perc90
    final long perc95
    final long perc99
    final int totalExecutions
    final double executionsPerSecond
    final Instant startTime
    final Instant endTime
    final long executionTime
    final String executionId
    final int floStepOrder
    final List<FloStepResult<T>> results
    final List<FloStepResult<T>> slowestResults

    FloExecutionResult(
        int floStepOrder,
        String floStepName,
        FloRunnaSettings settings,
        Instant startTime,
        Instant endTime,
        String executionId,
        List<FloStepResult<T>> results
    ) {
        this.floStepOrder = floStepOrder
        this.testName = settings.testName
        this.floStepName = floStepName
        this.threads = settings.threads
        this.duration = settings.duration
        this.rampup = settings.rampup
        this.startTime = startTime
        this.endTime = endTime
        this.executionId = executionId

        this.executionTime = endTime.toEpochMilli() - startTime.toEpochMilli()
        this.slowestResults = results.sort { it.elapsed }.takeRight(10).reverse()
        this.results = results.sort { it.startTime.toEpochMilli() }
        this.orderedResponseTimes = this.results.collect { it.elapsed }.sort()
        this.totalExecutions = this.orderedResponseTimes.size()
        this.perc50 = getPercentile(0.50)
        this.perc80 = getPercentile(0.80)
        this.perc90 = getPercentile(0.90)
        this.perc95 = getPercentile(0.95)
        this.perc99 = getPercentile(0.99)
        this.executionsPerSecond = totalExecutions / (this.executionTime / 1000)
    }

    private long getPercentile(double percentile) {
        totalExecutions > 0 ? orderedResponseTimes[Math.floor(totalExecutions * percentile)] : 0
    }
}
