package co.tala.performance.flo

import spock.lang.Specification

class WorkFloBuilderSpec extends Specification {
    def "build should create a WorkFlo"() {
        given: "WorkFloBuilder is initialized"
            Closure<LinkedHashMap<String, String>> metadata = { ["foo": "bar"] }
            Closure action = {}
            WorkFloBuilder sut = new WorkFloBuilder()

        when: "addStep is called 10 times"
            sut.setMetadata(metadata)
            10.times {
                sut.addStep("step$it", action)
            }

        and: "build is invoked"
            WorkFlo result = sut.build()

        then: "a WorkFlo with 10 steps should be returned"
            verifyAll(result) {
                it.metadata() == metadata()
                it.steps.size() == 10
                it.steps.eachWithIndex { FloStep entry, int i ->
                    assert entry.orderNumber == i
                    assert entry.action == action
                    assert entry.name == "step$i"
                }
            }
    }

    def "build should create a WorkFlo without Closure"() {
        given: "WorkFloBuilder is initialized"
            LinkedHashMap<String, String> metadata = ["foo": "bar"]
            Closure action = {}
            WorkFloBuilder sut = new WorkFloBuilder()

        when: "addStep is called 10 times"
            sut.setMetadata(metadata)
            10.times {
                sut.addStep("step$it", action)
            }

        and: "build is invoked"
            WorkFlo result = sut.build()

        then: "a WorkFlo with 10 steps should be returned"
            verifyAll(result) {
                it.metadata() == metadata
                it.steps.size() == 10
                it.steps.eachWithIndex { FloStep entry, int i ->
                    assert entry.orderNumber == i
                    assert entry.action == action
                    assert entry.name == "step$i"
                }
            }
    }

    def "build with no steps should create an empty WorkFlo"() {
        given:
            Closure<LinkedHashMap<String, String>> metadata = { ["foo": "bar"] }
            WorkFloBuilder sut = new WorkFloBuilder()

        when: "addStep is never called"
        and: "build is invoked"
            WorkFlo result = sut.setMetadata(metadata).build()

        then: "A WorkFlo with no steps should be returned"
            verifyAll(result) {
                it.metadata() == metadata()
                it.steps.size() == 0
            }
    }

    def "build with no metadata should create a WorkFlo with null metadata"() {
        given:
            WorkFloBuilder sut = new WorkFloBuilder()

        when: "addStep is never called"
        and: "build is invoked"
            WorkFlo result = sut.addStep("step", {}).build()

        then: "A WorkFlo with no steps should be returned"
            verifyAll(result) {
                it.steps.size() == 1
                it.metadata() == null
            }
    }
}
