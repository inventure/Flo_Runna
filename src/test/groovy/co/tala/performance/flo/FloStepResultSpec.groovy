package co.tala.performance.flo

import spock.lang.Specification

import java.time.Instant

class FloStepResultSpec extends Specification {
    def "FloStepResult should initialize"() {
        given:
            Instant startTime = Instant.now()
            Instant endTime = startTime.plusSeconds(60)
            Map<String, String> metadata = ["foo": "bar"]
            FloError floError = new FloError("name", "message")
            String resultId = "resultId"

        when: "FloStepResult is initialized"
            FloStepResult result = new FloStepResult(startTime, endTime, metadata, floError, resultId)

        then: "elapsed should be endTime minus startTime"
        and: "all other properties should be equal to the constructor args"
            verifyAll(result) {
                it.elapsed == endTime.toEpochMilli() - startTime.toEpochMilli()
                it.startTime == startTime
                it.endTime == endTime
                it.error == error
                it.resultId == resultId
                it.metadata == metadata
            }
    }
}
