package co.tala.performance.exception

class AggregateException extends Exception {
    AggregateException(List<Throwable> throwables) {
        super(createExceptionMessage(throwables))
    }

    private static String createExceptionMessage(List<Throwable> throwables) {
        final StringBuilder sb = new StringBuilder()
        throwables.eachWithIndex { Throwable entry, int i ->
            sb.append("\nException ${i + 1}\n")
            sb.append("Message: ${entry.message}\n")
            sb.append("StackTrace: ${entry.stackTrace}\n")
        }
        sb.toString()
    }
}
