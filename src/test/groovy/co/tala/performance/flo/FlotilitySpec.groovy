package co.tala.performance.flo


import spock.lang.Specification

class FlotilitySpec extends Specification {
    IFloStepResultStorage storageMock
    private Flotility sut

    def setup() {
        storageMock = Mock()
        sut = new Flotility(storageMock)
    }

    def "executeWorkFlo should add results for all steps and the TotalSummary"() {
        given: "a Workflo exists"
            LinkedHashMap<String, String> metadata = ["foo": "bar"]
            WorkFlo workFlo = new WorkFloBuilder()
                    .setMetadata(metadata)
                    .addStep("step1", { sleep(10) })
                    .addStep("step2", { sleep(20) })
                    .addStep("step3", { sleep(30) })
                    .build()

            Map<String, Integer> stepCount = [:]
            workFlo.steps.each {
                stepCount[it.name] = 0
            }
            stepCount["TotalSummary"] = 0

        when: "executeWorkFlo is invoked 3 times"
            3.times {
                sut.executeWorkFlo(workFlo)
            }

        then: "results should be added for all steps"
            12 * storageMock.addResult(_ as FloStep, _ as FloStepResult) >> { FloStep floStep, FloStepResult result ->
                stepCount[floStep.name] += 1
                Closure<Void> assertElapsed = { long bottom, long top ->
                    long elapsed = result.elapsed
                    assert elapsed >= bottom && elapsed <= top
                    assert elapsed == result.endTime.toEpochMilli() - result.startTime.toEpochMilli()
                }
                switch (floStep.name) {
                    case "step1":
                        assertElapsed(10, 20)
                        assert floStep.orderNumber == 0
                        break
                    case "step2":
                        assertElapsed(20, 30)
                        assert floStep.orderNumber == 1
                        break
                    case "step3":
                        assertElapsed(30, 40)
                        assert floStep.orderNumber == 2
                        break
                    case "TotalSummary":
                        assertElapsed(60, 150)
                        assert floStep.orderNumber == Integer.MAX_VALUE
                        break
                    default:
                        assert false: "${floStep.name} was not expeceted"
                }
                assert result.metadata == metadata
                assert UUID.fromString(result.resultId) != null
            }
            stepCount.each {
                assert it.value == 3
            }
    }

    def "executeWorkFlo should add results when an exception is thrown"() {
        given: "a Workflo exists, where some of the steps throw exceptions"
            LinkedHashMap<String, String> metadata = ["foo": "bar"]
            WorkFlo workFloWithNoExceptions = new WorkFloBuilder()
                    .setMetadata(metadata)
                    .addStep("step1", { sleep(10) })
                    .addStep("step2", { sleep(20) })
                    .addStep("step3", { sleep(30) })
                    .build()

            WorkFlo workFloWithOneException = new WorkFloBuilder()
                    .setMetadata(metadata)
                    .addStep("step1", { sleep(10) })
                    .addStep("step2", { sleep(20); throw new Exception("exception 2") })
                    .addStep("step3", { sleep(30) })
                    .build()

            Map<String, Integer> stepCount = [:]
            stepCount["step1"] = 0
            stepCount["step2"] = 0
            stepCount["step3"] = 0
            stepCount["TotalSummary"] = 0

        when: "executeWorkFlo is invoked with no exceptions"
            sut.executeWorkFlo(workFloWithNoExceptions)
        and: "executeWorkFlo is invoked with an exception at the 2nd step"
            sut.executeWorkFlo(workFloWithOneException)

        then: "the workflow should exit when the exception is thrown"
        and: "results should still be added for the workflo with the exception"
        and: "the error should be stored in the step that failed as well as the TotalSummary"
            Exception e = thrown Exception
            e.message == "exception 2"

            7 * storageMock.addResult(_ as FloStep, _ as FloStepResult) >> { FloStep floStep, FloStepResult result ->
                stepCount[floStep.name] += 1
                if (
                (floStep.name == "step2" && stepCount["step2"] == 2) ||
                        (floStep.name == "TotalSummary" && stepCount["TotalSummary"] == 2)
                ) {
                    assert result.error != null
                    assert result.error.floStepName == "step2"
                    assert result.error.message == "exception 2"
                } else
                    assert result.error == null
            }
            stepCount["step1"] == 2
            stepCount["step2"] == 2
            //Because the workflo should exit
            stepCount["step3"] == 1
            stepCount["TotalSummary"] == 2
    }
}
