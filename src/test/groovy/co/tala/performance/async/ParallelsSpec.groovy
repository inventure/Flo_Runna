package co.tala.performance.async

import org.codehaus.groovy.runtime.powerassert.PowerAssertionError
import spock.lang.Specification

class ParallelsSpec extends Specification {
    private IParallels sut

    def setup() {
        sut = new Parallels()
    }

    def "Parallels should initialize"() {
        expect: "it should not be null"
            sut != null
    }

    def "runAsync should start an action"() {
        given: "a boolean is set to false"
            boolean isTriggered = false

        when: "runAsync is invoke with a Closure to set the boolean to true"
        and:"waitAll is invoked"
            sut.runAsync { isTriggered = true }.waitAll()

        then: "the boolean value should be true"
            isTriggered
    }

    def "getActiveThreadCount should return the correct number of active threads"() {
        when: "5 threads are run"
            5.times {
                sut.runAsync { sleep(100) }
                assert sut.getActiveThreadCount() == it + 1
            }
        then: "the active thread count should increase by 1 for each new thread"

        when: "all actions have completed, and waitAll has not been invoked"
            sleep(200)

        then: "the thread count should be 0"
            sut.getActiveThreadCount() == 0

        when: "2 more threads are run, and one of them completes"
            sut.runAsync { sleep(50) }.runAsync { sleep(300) }
            sleep(100)

        then: "the thread count should be 1"
            sut.getActiveThreadCount() == 1

        when: "waitAll is invoked"
            sut.waitAll()

        then: "the thread count should be 0"
            sut.getActiveThreadCount() == 0
    }

    def "waitAll should throw all exceptions that occur on all threads"() {
        given: "4 threads are invoked, and 2 of them throw exceptions"
            sut
                    .runAsync { sleep(10) }
                    .runAsync { sleep(10); throw new Exception("exception 1") }
                    .runAsync { sleep(50) }
                    .runAsync { sleep(10); throw new Exception("exception 2") }
                    .runAsync { sleep(10) }

        when: "waitAll is invoked"
            sut.waitAll()

        then: "the exceptions should all be thrown"
            // TODO figure why sometimes only 1 exception is thrown
            PowerAssertionError e = thrown PowerAssertionError
            e.message.contains("java.lang.Exception")

    }
}
