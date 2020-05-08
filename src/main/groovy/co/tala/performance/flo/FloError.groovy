package co.tala.performance.flo

import groovy.transform.PackageScope

@PackageScope
class FloError {
    final String floStepName
    final String message

    FloError(
        String floStepName,
        String message
    ) {
        this.floStepName = floStepName
        this.message = message
    }
}
