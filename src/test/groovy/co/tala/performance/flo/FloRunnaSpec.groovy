package co.tala.performance.flo

import co.tala.performance.async.IParallels
import spock.lang.Specification

import java.time.Instant

class FloRunnaSpec extends Specification {
    private IParallels parallelsMock
    private IFlotility flotilityMock
    private IFloWriter floWriterMock
    private IFloStepResultStorage storageMock
    private FloRunnaSettings settings
    private FloRunna sut

    def setup() {
        settings = new FloRunnaSettings(8, 1000, 200, "testName")
        parallelsMock = Mock()
        flotilityMock = Mock()
        floWriterMock = Mock()
        storageMock = Mock()
        sut = new FloRunna(parallelsMock, settings, flotilityMock, floWriterMock)
    }

    def "FloRunna should initialize with ctor 1"() {
        when:
            FloRunna result = new FloRunna(settings)

        then:
            result != null
    }

    def "execute should succeed"() {
        given:
            1 * flotilityMock.results >> storageMock
            Map<String, FloExecutionResult> floExecutionResults = ModelFactory.createFloExecutionResults(
                    3,
                    10,
                    [
                            ModelFactory.createFloStepResult(10),
                            ModelFactory.createFloStepResult(20)
                    ]
            )
            1 * storageMock.toFloExecutionResults(_ as Instant, _ as Instant) >> floExecutionResults
            (3.._) * parallelsMock.runAsync(_ as Closure) >> { Closure action ->
                action()
                parallelsMock
            }
            parallelsMock.activeThreadCount >>> [0, 0, 0, 0, 0, 0, 0, 0, 0, 10]
            WorkFlo<Object> workFlo = new WorkFlo([:], [new FloStep("fake", {}, 0)])

        when: "the workflo is executed"
            Map<String, FloExecutionResult> results = sut.execute { workFlo }

        then: "the workflo should be executed by Flotility several times"
            (3.._) * flotilityMock.executeWorkFlo(workFlo)

        and: "Parallels.waitAll should be invoked"
            1 * parallelsMock.waitAll()

        and: "the results should be written"
            1 * floWriterMock.writeResults(floExecutionResults)

        and: "it should return valid execution results"
            results == floExecutionResults
    }
}
