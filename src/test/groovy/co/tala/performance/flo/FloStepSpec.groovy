package co.tala.performance.flo

import spock.lang.Specification

class FloStepSpec extends Specification {
    def "FloStep should initialize"() {
        given:
            String name = "name"
            Closure closure = { println("Hello, World!") }
            int orderNumber = 9

        when: "FloStep is initialized"
            FloStep result = new FloStep(name, closure, orderNumber)

        then: "it should not be null"
            verifyAll (result) {
                it.name == name
                it.action == closure
                it.orderNumber == orderNumber
            }
    }
}
