package co.tala.performance.utils

import java.time.Instant


class FloLogger {
    final boolean debugEnabled

    FloLogger(boolean debugEnabled) {
        this.debugEnabled = debugEnabled
    }

    def debug(String message) {
        if (this.debugEnabled) {
            println(
                "{\"asctime\": \"${Instant.now()}\", \"name\": \"FloLogger\", \"logLevel\": \"INFO\", \"message\": \"${message.toString()}\"}\n"
            )
        }
    }
}
