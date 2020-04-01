package co.tala.performance.flo

import groovy.transform.PackageScope

/**
 * Wrapper for File class to improve unit testability
 */
@PackageScope
class FloIO implements IFloIO {

    @Override
    void writeToFile(String fileName, String content) {
        new File(fileName).withWriter { it.write(content) }
    }

    @Override
    String readFromFile(String fileName) {
        new File(fileName).withReader {
            readLines(it)
        }
    }

    @Override
    String readFromBufferedReader(BufferedReader bufferedReader) {
        bufferedReader.withReader {
            readLines(it)
        }
    }

    @Override
    void mkdirs(String dir) {
        new File(dir).mkdirs()
    }

    private static String readLines(Reader reader) {
        reader.readLines().join("\n")
    }
}
