package co.tala.performance.async

interface IParallels {
    IParallels runAsync(Closure action)
    int getActiveThreadCount()
    void waitAll()
}
