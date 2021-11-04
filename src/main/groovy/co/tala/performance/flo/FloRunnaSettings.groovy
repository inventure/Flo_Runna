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
    private boolean debug = false

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
        int threads,
        long duration,
        long rampup,
        int iterations,
        String testName,
        boolean outputEnabled = true
    ) {
        // using default values in-case negative values passed in
        this.threads = threads > 0 ? threads : 8
        this.duration = duration > 0 ? duration : 8000
        this.iterations = iterations > 0 ? iterations : 0
        this.rampup = rampup > 0 ? rampup : 1000
        this.testName = testName?.trim() ? testName : "Undefined Test Name"
        this.outputEnabled = outputEnabled
    }

    FloRunnaSettings setDebug(boolean debugEnabled) {
        this.debug = debugEnabled
        this
    }

    def getDebug() {
        this.debug
    }
}
