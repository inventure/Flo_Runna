package co.tala.performance.flo

/**
 * Data class to represent all settings for [FloRunna]
 */
class FloRunnaSettings {
    final int threads
    final long duration
    final long rampup
    final String testName

    FloRunnaSettings(
            int threads,
            long duration,
            long rampup,
            String testName
    ) {
        this.threads = threads
        this.duration = duration
        this.rampup = rampup
        this.testName = testName
    }

    FloRunnaSettings(
            String testName
    ) {
        def getPropValue = { String key, Object defaultValue ->
            System.getProperty(key, defaultValue.toString())
        }

        int defaultThreads = 8
        long defaultDuration = 8000
        long defaultRampup = 1000

        this.threads = getPropValue("threads", defaultThreads).toInteger()
        this.duration = getPropValue("duration", defaultDuration).toLong()
        this.rampup = getPropValue("rampup", defaultRampup).toLong()
        this.testName = testName
    }
}
