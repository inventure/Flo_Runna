package co.tala.performance.flo

import java.time.Instant

interface IFloStepResultStorage<T> {
    void addResult(FloStep floStep, FloStepResult<T> result)

    Map<String, FloExecutionResult<T>> toFloExecutionResults(Instant startTime, Instant endTime)
}
