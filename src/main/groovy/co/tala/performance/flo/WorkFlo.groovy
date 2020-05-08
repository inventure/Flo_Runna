package co.tala.performance.flo

import groovy.transform.PackageScope

/**
 * A data class to represent the sequence of [FloStep]s
 * @param < T >
 */
@PackageScope
class WorkFlo<T> {
    final T metadata
    final List<FloStep> steps

    WorkFlo(
        T metadata,
        List<FloStep> steps
    ) {
        this.metadata = metadata
        this.steps = steps
    }
}
