package co.tala.performance.flo

import groovy.transform.PackageScope

@PackageScope
class FloStepResults<T> {
    final int orderNumber
    final List<FloStepResult<T>> results

    FloStepResults(
        int orderNumber
    ) {
        this.orderNumber = orderNumber
        this.results = []
    }
}
