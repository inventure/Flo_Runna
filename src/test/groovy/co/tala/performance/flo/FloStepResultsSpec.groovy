package co.tala.performance.flo

import spock.lang.Specification

class FloStepResultsSpec extends Specification {
    def "FloStepResults should initialize"() {
        given:
            int orderNumber = 10

        when: "FloStepResults is initialized"
            FloStepResults result = new FloStepResults(orderNumber)

        then: "it is not null"
            verifyAll(result) {
                it.orderNumber == orderNumber
                it.results.isEmpty()
            }
    }
}
