package co.tala.performance.flo

import spock.lang.Specification

class FloSourceSpec extends Specification{
    def "TOTAL_SUMMARY should be 'TotalSummary"(){
        expect: "FloSource.TOTAL_SUMMARY should be 'TotalSummary'"
            FloSource.TOTAL_SUMMARY == "TotalSummary"
    }
}
