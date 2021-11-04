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
                it.iterations == 0
                it.rampup == 1000
                it.testName == testName
                it.outputEnabled
            }
    }

    def "FloRunnaSettings should initialize with settings from system properties"() {
        given: "system properties are set for threads, duration, and rampup"
            System.setProperty("threads", "1")
            System.setProperty("duration", "2")
            System.setProperty("iterations", "200")
            System.setProperty("rampup", "3")
            System.setProperty("outputEnabled", "false")
            String testName = "testName"

        when: "FloRunnaSettings is initialized"
            FloRunnaSettings result = new FloRunnaSettings(testName)

        then: "the threads, duration, and rampup should be equal to the values in system properties"
            verifyAll(result) {
                it.threads == 1
                it.duration == 2
                it.iterations == 200
                it.rampup == 3
                it.testName == testName
                !it.outputEnabled
            }
            // clear out to not interfere with other tests
            System.clearProperty("threads")
            System.clearProperty("duration")
            System.clearProperty("iterations")
            System.clearProperty("rampup")
            System.clearProperty("outputEnabled")
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

    def "FloRunnaSettings should initialize with override settings 2"() {
        given: "threads, duration, and rampup are set"
            int threads = 2
            long duration = 4
            long rampup = 6
            String testName = "testName"

        when: "FloRunnaSettings is initialized"
            FloRunnaSettings result = new FloRunnaSettings(threads, duration, rampup, testName, false)

        then: "the threads, duration, and rampup should be equal to the constructor overrides"
            verifyAll(result) {
                it.threads == threads
                it.duration == duration
                it.rampup == rampup
                it.testName == testName
                !it.outputEnabled
            }
    }

    def "FloRunnaSettings should handle cases where System property might be empty or null"() {
        given: "if the System has set a property to empty string"
            System.setProperty("threads", "")
            System.setProperty("outputEnabled", "")
            System.clearProperty("duration") // You can't set a property to have null value, so clear
            System.clearProperty("rampup")
            String testName = "testName"

        when: "FloRunnaSettings is initialized"
            FloRunnaSettings result = new FloRunnaSettings(testName)

        then: "the threads, duration, and rampup should be equal to the constructor overrides (values from FloRunnaSettings class)"
            verifyAll(result) {
                it.threads == 8
                it.duration == 8000
                it.iterations == 0
                it.rampup == 1000
                it.testName == testName
                it.outputEnabled
            }
    }

    def "FloRunnaSettings should initialize with settings from system for iteration run"() {
        given: "we have setup various inputs for FloRunnaSettings call"
            int threads = 2
            long duration = 10000
            int iterations = 10
            long rampup = 6
            String testName = "testName"

        when: "FloRunnaSettings is initialized for iteration run"
            FloRunnaSettings result = new FloRunnaSettings(
                threads,
                duration,
                rampup,
                iterations,
                testName
            ).setDebug(false)

        then: "the threads, duration, and rampup should be equal to the values in system properties"
            verifyAll(result) {
                it.threads == threads
                it.duration == duration
                it.iterations == iterations
                it.rampup == rampup
                it.testName == testName
                it.outputEnabled
                !it.debug
            }
    }

    def "FloRunnaSettings should initialize with default settings if invalid settings passed in"() {
        given: "we have setup various inputs for FloRunnaSettings call"
            int threads = -1
            long duration = -1
            int iterations = -1
            long rampup = -1
            String testName = null

        when: "FloRunnaSettings is initialized for iteration run"
            FloRunnaSettings result = new FloRunnaSettings(
                threads,
                duration,
                rampup,
                iterations,
                testName
            ).setDebug(false)

        then: "the threads, duration, and rampup should be equal to the values in system properties"
            verifyAll(result) {
                it.threads == 8
                it.duration == 8000
                it.iterations == 0
                it.rampup == 1000
                it.testName == "Undefined Test Name"
                it.outputEnabled
                !it.debug
            }
    }
}
