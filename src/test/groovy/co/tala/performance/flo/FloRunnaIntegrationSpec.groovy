package co.tala.performance.flo

import org.codehaus.groovy.runtime.powerassert.PowerAssertionError
import spock.lang.Specification

class FloRunnaIntegrationSpec extends Specification {
    private Random random
    private Object metadata

    def setup() {
        random = new Random()
        metadata = [resourceId: UUID.randomUUID().toString(), map: [innerId: nextLong(), flo: "runna"]]
    }

    def "flo runna integration spec"() {
        given:
            FloRunna floRunna = new FloRunna(new FloRunnaSettings(8, 3000, 1000, "Flo Runna Integration Test"))

        when: "flo runna is executed with 5 steps, each with various execution times, and some randomly throw exceptions"
            Map<String, FloExecutionResult> results = floRunna.execute { WorkFloBuilder workFloBuilder ->

                workFloBuilder
                        .setMetadata(metadata)
                        .addStep("very fast step") {
                            sleepFor(10, 30)
                        }
                        .addStep("fast step") {
                            sleepFor(50, 200)
                        }
                        .addStep("medium step") {
                            sleepFor(250, 500)
                        }
                        .addStep("slow step") {
                            sleepFor(500, 1000)
                        }
                        .addStep("very slow step") {
                            sleepFor(1000, 2000)
                        }
                        .build()
            }

        then: "a PowerAssertionError should be thrown"
            thrown PowerAssertionError
    }

    def "flo runna repeat step spec"() {
        given:
            FloRunna floRunna = new FloRunna(new FloRunnaSettings(8, 3000, 1000, "Flo Runna Repeat Step Test"))

        when: "flo runna is executed with 5 steps, each with various execution times, and some randomly throw exceptions"
            Map<String, FloExecutionResult> results = floRunna.execute { WorkFloBuilder workFloBuilder ->
                workFloBuilder
                        .setMetadata(metadata)
                        .addStep("repeat step") {
                            sleepFor(10, 30)
                        }
                        .addStep("non repeat step") {
                            sleepFor(50, 200)
                        }
                        .addStep("repeat step") {
                            sleepFor(10, 30)
                        }
                        .build()
            }

        then: "a PowerAssertionError should be thrown"
            thrown PowerAssertionError
    }

    private void sleepFor(long lowerBound, long upperBound) {
        def duration = (nextLong() % lowerBound) + (upperBound - lowerBound)
        sleep(duration)
        def divisibleBy = 10
        if (duration % divisibleBy == 0) throw new Exception("${duration} is divisible by ${divisibleBy}!")
    }

    private long nextLong() {
        Math.abs(random.nextLong())
    }

}
