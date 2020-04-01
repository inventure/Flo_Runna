package co.tala.performance.flo

import spock.lang.Specification

class FloRunnaSettingsSpec extends Specification {
    def "FloRunnaSettings should initialize with default settings"() {
        given:
            String testName = "testName"

        when:
            FloRunnaSettings result = new FloRunnaSettings(testName)

        then:
            verifyAll(result) {
                it.threads == 8
                it.duration == 8000
                it.rampup == 1000
                it.testName == testName
            }
    }

    def "FloRunnaSettings should initialize with settings from system properties"() {
        given: "system properties are set for threads, duration, and rampup"
            System.setProperty("threads", "1")
            System.setProperty("duration", "2")
            System.setProperty("rampup", "3")
            String testName = "testName"

        when: "FloRunnaSettings is initialized"
            FloRunnaSettings result = new FloRunnaSettings(testName)

        then: "the threads, duration, and rampup should be equal to the values in system properties"
            verifyAll(result) {
                it.threads == 1
                it.duration == 2
                it.rampup == 3
                it.testName == testName
            }
    }

    def "FloRunnaSettings should initialize with override settings"() {
        given: "threads, duration, and rampup are set"
            int threads = 2
            long duration = 4
            long rampup = 6
            String testName = "testName"

        when: "FloRunnaSettings is initialized"
            FloRunnaSettings result = new FloRunnaSettings(threads, duration, rampup, testName)

        then: "the threads, duration, and rampup should be equal to the constructor overrides"
            verifyAll(result) {
                it.threads == threads
                it.duration == duration
                it.rampup == rampup
                it.testName == testName
            }
    }
}
