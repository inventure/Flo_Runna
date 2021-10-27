package co.tala.performance.flo

/**
 * Data class to represent all settings for [FloRunna]
 */
class FloRunnaSettings {
    final int threads
    final long duration
    // for use in non-time based runs
    final int iterations
    final long rampup
    final String testName
    final boolean outputEnabled

    FloRunnaSettings(
        int threads,
        long duration,
        long rampup,
        String testName
    ) {
        this.threads = threads
        this.duration = duration
        this.iterations = 0
        this.rampup = rampup
        this.testName = testName
        this.outputEnabled = true
    }

    FloRunnaSettings(
        int threads,
        long duration,
        long rampup,
        String testName,
        boolean outputEnabled
    ) {
        this.threads = threads
        this.duration = duration
        this.iterations = 0
        this.rampup = rampup
        this.testName = testName
        this.outputEnabled = outputEnabled
    }

    FloRunnaSettings(
        String testName
    ) {
        def getPropValue = { String key, Object defaultValue ->
            Optional.of(System.getProperty(key, defaultValue.toString()))
                .filter({ value -> value != null && !value.isEmpty() })
                .orElse(defaultValue as String)
        }

        int defaultThreads = 8
        int defaultIterations = 0
        long defaultDuration = 8000
        long defaultRampup = 1000

        this.threads = getPropValue("threads", defaultThreads).toInteger()
        this.iterations = getPropValue("iterations", defaultIterations).toInteger()
        this.duration = getPropValue("duration", defaultDuration).toLong()
        this.rampup = getPropValue("rampup", defaultRampup).toLong()
        this.outputEnabled = getPropValue("outputEnabled", "true").toBoolean()
        this.testName = testName
    }

    FloRunnaSettings(
        String testName,
        int threads,
        int iterations,
        long rampup
    ) {
        this.threads = threads
        this.iterations = iterations
        this.duration = 0
        this.rampup = rampup
        this.testName = testName
        this.outputEnabled = true
    }
}
