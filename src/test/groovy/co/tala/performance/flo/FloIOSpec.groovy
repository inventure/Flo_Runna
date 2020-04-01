package co.tala.performance.flo

import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

@Unroll
class FloIOSpec extends Specification {
    private String dir
    private String file
    private String content
    private IFloIO sut

    def setup() {
        long now = Instant.now().toEpochMilli()
        dir = "build/flo-io-test"
        file = "$dir/test${now}.txt"
        content = """"this is a test\nthis is line 2"""
        sut = new FloIO()
    }

    def "FloIO should initialize"() {
        expect: "it should not be null"
            sut != null
    }

    def "write and read using '#readType' should succeed"() {
        when: "mkdirs is invoked"
            sut.mkdirs(dir)

        and: "writeToFile is invoked with content '#content'"
            sut.writeToFile(file, content)

        and: "the file is read with'#readType'"
            String readResult = ""
            switch (readType) {
                case ReadType.READ_FROM_FILE:
                    readResult = sut.readFromFile(file)
                    break
                case ReadType.READ_FROM_BUFFERED_READER:
                    InputStream inputStream = new FileInputStream(new File(file));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    readResult = sut.readFromBufferedReader(reader)
                    break
            }

        then: "the content from reading should be correct"
            readResult == content

        and: "the file should exist with content '#content'"
            new File(file).withReader {
                assert it.readLines().join("\n") == content
                true
            }

        where:
            readType << [ReadType.READ_FROM_FILE, ReadType.READ_FROM_BUFFERED_READER]
    }

    private enum ReadType {
        READ_FROM_FILE,
        READ_FROM_BUFFERED_READER
    }
}
