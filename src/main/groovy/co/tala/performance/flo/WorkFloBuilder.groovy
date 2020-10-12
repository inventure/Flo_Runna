package co.tala.performance.flo

/**
 * Builder class to create a [WorkFlo]
 * @param < T >
 */
class WorkFloBuilder<T> {
    private final List<FloStep> floSteps
    private Closure<T> metadata
    private int count

    WorkFloBuilder() {
        this.floSteps = []
        this.count = 0
    }

    /**
     * Sets the generic metadata for a [WorkFlo]. Value is realized before the [WorkFlo] execution begins.
     * @param metadata
     * @return
     */
    @Deprecated
    WorkFloBuilder setMetadata(T metadata) {
        this.metadata = { metadata }
        this
    }

    /**
     * Sets the generic metadata for a [WorkFlo]. Value will be realized after [WorkFlo] is executed.
     * @param metadata
     * @return
     */
    WorkFloBuilder setMetadata(Closure<T> metadata) {
        this.metadata = metadata
        this
    }

    /**
     * Adds a [FloStep] to the end of the [WorkFlo]
     * @param name
     * @param action
     * @return
     */
    WorkFloBuilder addStep(String name, Closure action) {
        this.floSteps << new FloStep(name, action, count++)
        this
    }

    /**
     * builds the [WorkFlo]
     * @return
     */
    WorkFlo<T> build() { new WorkFlo(this.metadata, this.floSteps) }
}
