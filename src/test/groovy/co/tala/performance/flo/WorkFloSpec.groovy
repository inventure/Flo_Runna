package co.tala.performance.flo

import spock.lang.Specification

class WorkFloSpec extends Specification {
    def "WorkFlo should initialize"() {
        given:
            Closure<LinkedHashMap<String, String>> metadata = { ["foo": "bar"] }
            List<FloStep> floSteps = []
            Closure action = {}
            10.times {
                floSteps.add(new FloStep("step$it", action, it))
            }

        when: "WorkFlo is initialized"
            WorkFlo result = new WorkFlo(metadata, floSteps)

        then: "it should not be null"
            verifyAll(result) {
                it.metadata() == metadata()
                it.steps.eachWithIndex { FloStep entry, int i ->
                    assert entry.name == floSteps[i].name
                    assert entry.orderNumber == floSteps[i].orderNumber
                    assert entry.action == action
                }
            }
    }

    def "WorkFlo should initialize 2"() {
        given:
            LinkedHashMap<String, String> metadata = ["foo": "bar"]
            List<FloStep> floSteps = []
            Closure action = {}
            10.times {
                floSteps.add(new FloStep("step$it", action, it))
            }

        when: "WorkFlo is initialized"
            WorkFlo result = new WorkFlo(metadata, floSteps)

        then: "it should not be null"
            verifyAll(result) {
                it.metadata() == metadata
                it.steps.eachWithIndex { FloStep entry, int i ->
                    assert entry.name == floSteps[i].name
                    assert entry.orderNumber == floSteps[i].orderNumber
                    assert entry.action == action
                }
            }
    }
}
