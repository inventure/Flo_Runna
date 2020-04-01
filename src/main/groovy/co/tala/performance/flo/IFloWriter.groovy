package co.tala.performance.flo

import groovy.transform.PackageScope

@PackageScope
interface IFloWriter<T> {
    void writeResults(Map<String, FloExecutionResult<T>> results)
}
