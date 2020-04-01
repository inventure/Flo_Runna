package co.tala.performance.flo

import groovy.transform.PackageScope

/**
 * A step in a [WorkFlo], which executes an action and will have metrics captured.
 */
@PackageScope
class FloStep {
    final String name
    final Closure action
    final int orderNumber

    FloStep(
            String name,
            Closure action,
            int orderNumber
    ) {
        this.name = name
        this.action = action
        this.orderNumber = orderNumber
    }
}
