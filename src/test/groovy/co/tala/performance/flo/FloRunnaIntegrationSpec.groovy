package co.tala.performance.flo

import co.tala.performance.exception.AggregateException
import co.tala.performance.utils.FloLogger
import spock.lang.Specification

import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class FloRunnaIntegrationSpec extends Specification {
    private Random random
    private Closure<Object> metadata
    FloLogger logger = new FloLogger(true)

    def setup() {
        random = new Random()
        metadata = { [resourceId: UUID.randomUUID().toString(), map: [innerId: nextLong(), flo: "runna"]] }
    }

    def "flo runna integration spec"() {
        given:
            FloRunna floRunna = new FloRunna(new FloRunnaSettings(8, 3000, 1000, "Flo Runna Integration Test"))

        when: "flo runna is executed with 5 steps, each with various execution times, and some randomly throw exceptions"
            floRunna.execute { WorkFloBuilder workFloBuilder ->
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

        then: "an AggregateException should be thrown"
            thrown AggregateException
    }

    def "flo runna repeat step spec"() {
        given:
            FloRunna floRunna = new FloRunna(new FloRunnaSettings(8, 3000, 1000, "Flo Runna Repeat Step Test"))

        when: "flo runna is executed with 5 steps, each with various execution times, and some randomly throw exceptions"
            floRunna.execute { WorkFloBuilder workFloBuilder ->
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

        then: "an AggregateException should be thrown"
            thrown AggregateException
    }

    def "flo runna with iterations amount small and duration long spec"() {
        Long duration = 15000
        Integer iterations = 10
        given: "setting up FloRunna to run small amount of iterations while duration is set for long time"
            FloRunna floRunna = new FloRunna(
                new FloRunnaSettings(
                    3,
                    duration,
                    1000,
                    10,
                    "Flo Runna Repeat Step Test",
                    true
                ).setDebug(true)
            )

        when: "flo runna is executed with some randomly throw exceptions and it should stop relatively quick"
            Instant startTime = getNow()
            Map<String, FloExecutionResult> results = floRunna.execute { WorkFloBuilder workFloBuilder ->
                workFloBuilder
                    .setMetadata(metadata)
                    .addStep("repeat step") {
                        logger.debug("repeat step 1")
                    }
                    .addStep("non repeat step") {
                        logger.debug("non repeat step")
                    }
                    .addStep("repeat step") {
                        logger.debug("repeat step 2")
                    }
                    .build()
            }
            Instant finishTime = getNow()
            results.each { key, result ->
                assert(result.iterations == iterations)
            }

        then: "the total runtime should be less than the duration"
            (finishTime.toEpochMilli() - startTime.toEpochMilli()) < duration
    }

    def "flo runna with iterations amount large and duration small spec"() {
        Long duration = 10
        Integer iterations = 100
        given: "setting up FloRunna to run large amount of iterations while duration is set for short period of time"
            FloRunna floRunna = new FloRunna(
                new FloRunnaSettings(
                    3,
                    duration,
                    1000,
                    iterations,
                    "Flo Runna Repeat Step Test",
                    true
                ).setDebug(false)
            )

        when: "flo runna is executed with some randomly throw exceptions and it should stop relatively quick"
            Instant startTime = getNow()
            Map<String, FloExecutionResult<WorkFloBuilder>> results = floRunna.execute { WorkFloBuilder workFloBuilder ->
                workFloBuilder
                    .setMetadata(metadata)
                    .addStep("repeat step") {
                        logger.debug("repeat step 1")
                    }
                    .addStep("non repeat step") {
                        logger.debug("non repeat step")
                    }
                    .addStep("repeat step") {
                        logger.debug("repeat step 2")
                    }
                    .build()
            }
            Instant finishTime = getNow()
            results.each { key, result ->
                assert(result.iterations == iterations)
            }

        then: "the execution time for the steps should be less than specified duration"
            (finishTime.toEpochMilli() - startTime.toEpochMilli()) > duration
    }

    def "flo runna with no iteration count specified and duration time set spec"() {
        Long duration = 3000
        Integer iterations = 0
        given: "setting up FloRunna to run with a duration set, but no iterations specified"
            FloRunna floRunna = new FloRunna(
                new FloRunnaSettings(
                    4,
                    duration,
                    1000,
                    "Flo Runna Repeat Step Test"
                ).setDebug(false)
            )

        when: "flo runna is executed with some randomly throw exceptions and it should stop relatively quick"
            Instant startTime = getNow()
            AtomicInteger execution = new AtomicInteger(0)
            Map<String, FloExecutionResult<WorkFloBuilder>> results = floRunna.execute { WorkFloBuilder workFloBuilder ->
                workFloBuilder
                    .setMetadata(metadata)
                    .addStep("increment counter") {
                        execution.set(execution.getAndIncrement())
                    }
                    .addStep("repeat step") {
                        logger.debug("${execution.get()} repeat step 1")
                    }
                    .addStep("non repeat step") {
                        logger.debug("${execution.get()} non repeat step")
                    }
                    .addStep("repeat step") {
                        logger.debug("${execution.get()} repeat step 2")
                    }
                    .build()
            }
            Instant finishTime = getNow()
            results.each { key, result ->
                assert(result.iterations == iterations)
            }

        then: "the execution time for the steps should be greater than specified duration"
            (finishTime.toEpochMilli() - startTime.toEpochMilli()) > duration
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

    private static Instant getNow() {
        Instant.now()
    }
}
