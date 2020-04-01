package co.tala.performance.flo

import spock.lang.Specification

class FloErrorSpec extends Specification {
    def "FloError should initialize"() {
        given:
            String floStepName = "floStepName"
            String message = "message"

        when: "it is initialized"
            FloError result = new FloError(floStepName, message)

        then: "it should not be null"
            verifyAll(result) {
                it.floStepName == floStepName
                it.message == message
            }
    }
}
