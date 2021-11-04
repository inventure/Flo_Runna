package co.tala.performance.flo

import spock.lang.Specification

import java.time.Instant

class FloExecutionResultSpec extends Specification {
    def "FloExecutionResult should initialize"() {
        given: "100 results exist, with each result 1 second longer than the previous"
            int floStepOrder = 10
            String floStepName = "floStepName"
            FloRunnaSettings settings = ModelFactory.createFloRunnaSettings()
            Instant startTime = Instant.now()
            Instant endTime = startTime.plusSeconds(500)
            String executionId = UUID.randomUUID().toString()
            List<FloStepResult<Object>> results = []
            100.times {
                results.add(ModelFactory.createFloStepResult(it))
            }

        when: "the FloExecutionResult is initialized"
            FloExecutionResult result = new FloExecutionResult(floStepOrder, floStepName, settings, startTime, endTime, executionId, results)


        then: "the percentiles should be calculated correctly"
        and: "all properties should be set correctly"
            List<FloStepResult<Object>> expectedResults = []
            expectedResults.addAll(results)
            expectedResults.sort { it.elapsed }
            List<FloStepResult<Object>> expectedSlowestResults = expectedResults.takeRight(10).reverse()
            verifyAll(result) {
                it.testName == settings.testName
                it.floStepName == floStepName
                it.threads == 1
                it.duration == 2
                it.rampup == 3
                it.perc50 == 50
                it.perc80 == 80
                it.perc90 == 90
                it.perc95 == 95
                it.perc99 == 99
                it.totalExecutions == 100
                it.executionsPerSecond == (100 / (500000 / 1000)).toDouble()
                it.startTime == startTime
                it.endTime == endTime
                it.executionTime == 500000
                it.executionId == executionId
                it.floStepOrder == floStepOrder
                it.results == expectedResults
                it.slowestResults == expectedSlowestResults
            }
    }

    def "FloExecutionResult should return 0 percentile if there are no executions"() {
        given:
            int floStepOrder = 10
            String floStepName = "floStepName"
            FloRunnaSettings settings = ModelFactory.createFloRunnaSettings()
            Instant startTime = Instant.now()
            Instant endTime = startTime.plusSeconds(500)
            String executionId = UUID.randomUUID().toString()
            List<FloStepResult<Object>> results = []

        when: "the FloExecutionResult is initialized"
            FloExecutionResult result = new FloExecutionResult(floStepOrder, floStepName, settings, startTime, endTime, executionId, results)

        then: "totalExecutions should be 0"
        and: "all percentiles should be 0"
            verifyAll(result) {
                result.totalExecutions == 0
                it.perc50 == 0
                it.perc80 == 0
                it.perc90 == 0
                it.perc95 == 0
                it.perc99 == 0
            }
    }

    def "FloExecutionResult should initialize for FloRunna iterations run"() {
        given: "100 results exist, with each result 1 second longer than the previous"
            int floStepOrder = 10
            String floStepName = "floStepIterationsName"
            FloRunnaSettings settings = ModelFactory.createFloRunnaSettingsIterations()
            Instant startTime = Instant.now()
            Instant endTime = startTime.plusSeconds(500)
            String executionId = UUID.randomUUID().toString()
            List<FloStepResult<Object>> results = []
            100.times {
                results.add(ModelFactory.createFloStepResult(it))
            }

        when: "the FloExecutionResult is initialized for iterations run"
            FloExecutionResult result = new FloExecutionResult(
                floStepOrder, floStepName, settings, startTime, endTime, executionId, results
            )

        then: "the percentiles should be calculated correctly"
        and: "all properties should be set correctly"
            List<FloStepResult<Object>> expectedResults = []
            expectedResults.addAll(results)
            expectedResults.sort { it.elapsed }
            List<FloStepResult<Object>> expectedSlowestResults = expectedResults.takeRight(10).reverse()
            verifyAll(result) {
                it.testName == settings.testName
                it.floStepName == floStepName
                it.threads == 1
                it.iterations == 3
                it.rampup == 100
                it.perc50 == 50
                it.perc80 == 80
                it.perc90 == 90
                it.perc95 == 95
                it.perc99 == 99
                it.totalExecutions == 100
                it.executionsPerSecond == (100 / (500000 / 1000)).toDouble()
                it.startTime == startTime
                it.endTime == endTime
                it.executionTime == 500000
                it.executionId == executionId
                it.floStepOrder == floStepOrder
                it.results == expectedResults
                it.slowestResults == expectedSlowestResults
            }
    }
}
