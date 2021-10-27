package co.tala.performance.flo

import java.time.Instant

class ModelFactory {
    static FloStepResult createFloStepResult(int millisElapsed) {
        Instant startTime = Instant.now()
        Instant endTime = startTime.plusMillis(millisElapsed)
        Map<String, String> metadata = ["foo": "bar"]
        FloError floError = createFloError()
        String resultId = randomUUID()

        new FloStepResult(startTime, endTime, metadata, floError, resultId)
    }

    static Map<String, FloExecutionResult> createFloExecutionResults(int stepCount, long millisElapsed, List<FloStepResult> floStepResults) {
        FloRunnaSettings settings = createFloRunnaSettings()
        Instant startTime = Instant.now()
        Instant endTime = startTime.plusMillis(millisElapsed)
        Map<GString, FloExecutionResult> results = [:]
        stepCount.times {
            results["step$it"] = new FloExecutionResult(
                    it,
                    "step$it",
                    settings,
                    startTime,
                    endTime,
                    UUID.randomUUID().toString(),
                    floStepResults
            )
        }
        results
    }

    static FloRunnaSettings createFloRunnaSettings() {
        new FloRunnaSettings(1, 2, 3, "test name example")
    }

    static FloRunnaSettings createFloRunnaSettingsIterations() {
        new FloRunnaSettings("test name example", 1, 2, 3)
    }

    private static FloError createFloError() {
        new FloError(randomUUID(), randomUUID())
    }

    private static String randomUUID() {
        UUID.randomUUID().toString()
    }
}
