package co.tala.performance.flo

import groovy.transform.PackageScope

import java.time.Instant

/**
 * The performance result for a [FloStep]
 * @param <T>
 */
@PackageScope
class FloStepResult<T> {
    final long elapsed
    final T metadata
    final FloError error
    final Instant startTime
    final Instant endTime
    final String resultId

    FloStepResult(
            Instant startTime,
            Instant endTime,
            T metadata,
            FloError error,
            String resultId
    ) {
        this.startTime = startTime
        this.endTime = endTime
        this.elapsed = endTime.toEpochMilli() - startTime.toEpochMilli()
        this.metadata = metadata
        this.error = error
        this.resultId = resultId
    }
}
