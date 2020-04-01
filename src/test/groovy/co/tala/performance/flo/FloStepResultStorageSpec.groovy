package co.tala.performance.flo

import spock.lang.Specification

import java.time.Instant

class FloStepResultStorageSpec extends Specification {
    private FloRunnaSettings settings
    private FloStepResultStorage sut

    def setup() {
        settings = new FloRunnaSettings("a")
        sut = new FloStepResultStorage(settings)
    }

    def "FloStepResultStorage should initialize"() {
        expect: "it should not be null"
            sut != null
    }

    def "toFloExecutionResults should convert the storage to a Map<String, FloExecutionResult<T>>"() {
        given: "3 flosteps are defined"
            FloStep floStep = new FloStep("step1", {}, 0)
            FloStep floStep2 = new FloStep("step2", {}, 1)
            FloStep floStep3 = new FloStep("step3", {}, 2)

        and: "startTime and endTime are defined"
            Instant startTime = Instant.now()
            Instant endTime = startTime.plusSeconds(60)

        when: "100 results are added for each flo step"
            100.times {
                sut.addResult(floStep, ModelFactory.createFloStepResult(it))
                sut.addResult(floStep2, ModelFactory.createFloStepResult(it * 2))
                sut.addResult(floStep3, ModelFactory.createFloStepResult(it * 3))
            }
        and: "toFloExecutionResults is called"
            Map<String, FloExecutionResult> result = sut.toFloExecutionResults(startTime, endTime)

        then: "there should be 3 FloExecutionResult_s"
            verifyAll(result) {
                it.size() == 3

                it.containsKey(floStep.name)
                it.containsKey(floStep2.name)
                it.containsKey(floStep3.name)

                it[floStep.name].floStepName == "step1"
                it[floStep2.name].floStepName == "step2"
                it[floStep3.name].floStepName == "step3"

                it[floStep.name].totalExecutions == 100
                it[floStep2.name].totalExecutions == 100
                it[floStep3.name].totalExecutions == 100

                it[floStep.name].executionTime == 60000
                it[floStep2.name].executionTime == 60000
                it[floStep3.name].executionTime == 60000

                it[floStep.name].executionId != null
                it[floStep2.name].executionId != null
                it[floStep3.name].executionId != null

                it[floStep.name].floStepOrder == 0
                it[floStep2.name].floStepOrder == 1
                it[floStep3.name].floStepOrder == 2

                it[floStep.name].testName == this.settings.testName
                it[floStep2.name].testName == this.settings.testName
                it[floStep3.name].testName == this.settings.testName
            }
    }
}
