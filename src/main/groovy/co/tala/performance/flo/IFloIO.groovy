package co.tala.performance.flo

import groovy.transform.PackageScope

@PackageScope
interface IFloIO {
    void writeToFile(String fileName, String content)
    String readFromFile(String fileName)
    String readFromBufferedReader(BufferedReader bufferedReader)
    void mkdirs(String dir)
}
