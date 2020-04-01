package co.tala.performance.flo

import groovy.transform.PackageScope

@PackageScope
interface IFlotility<T> {
    void executeWorkFlo(WorkFlo<T> workFlo)

    IFloStepResultStorage<T> getResults()
}
