package co.tala.performance.flo

import co.tala.performance.converter.InstantConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.json.JsonOutput
import spock.lang.Specification

import java.time.Instant

class FloWriterSpec extends Specification {
    private IFloIO floIOMock
    private FloRunnaSettings settings
    private IFloWriter sut
    private Gson gson

    def setup() {
        floIOMock = Mock()
        settings = ModelFactory.createFloRunnaSettings()
        sut = new FloWriter(settings, floIOMock)
        gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantConverter()).create()
    }

    def "it should initialize with default constructor"() {
        when: "FloWriter is initialized"
            FloWriter result = new FloWriter(settings)

        then: "it should not be null"
            result != null
    }

    def "writeResults should write all json files"() {
        given: "results exist for a workflo of 3 steps"
            GString expectedDir = "build/flo-runna-reports/${settings.testName.replace(" ", "_")}"
            Map<String, FloExecutionResult> results = ModelFactory.createFloExecutionResults(
                    3,
                    60000,
                    [
                            ModelFactory.createFloStepResult(10),
                            ModelFactory.createFloStepResult(20),
                            ModelFactory.createFloStepResult(30),
                            ModelFactory.createFloStepResult(40),
                            ModelFactory.createFloStepResult(50),
                    ]
            )

        when: "writeResults is invoked"
            sut.writeResults(results)

        then: "the directory should be created"
            1 * floIOMock.mkdirs(expectedDir)

        then: "results should be written for all steps and the floSteps.json, and the resource files should be copied"
            7 * floIOMock.writeToFile(_ as String, _ as String) >> { String fileName, String content ->
                assert fileName == "$expectedDir/step0.json"
                assertJson(content, results["step0"])
            } >> { String fileName, String content ->
                assert fileName == "$expectedDir/step1.json"
                assertJson(content, results["step1"])
            } >> { String fileName, String content ->
                assert fileName == "$expectedDir/step2.json"
                assertJson(content, results["step2"])
            } >> { String fileName, String content ->
                assert fileName == "$expectedDir/floSteps.json"
                assert content == gson.toJson([floSteps: ["step0", "step1", "step2"]])
            } >> { String fileName, String content ->
                assert fileName == "$expectedDir/index.html"
                assert content == "index mocked content"
            } >> { String fileName, String content ->
                assert fileName == "$expectedDir/plotly-1.52.3.min.js"
                assert content == "plotly mocked content"
            } >> { String fileName, String content ->
                assert fileName == "$expectedDir/jquery-1.11.1.min.js"
                assert content == "jquery mocked content"
            }

        and: "all resource files should be read from buffer"
            3 * floIOMock.readFromBufferedReader(_ as BufferedReader) >> { BufferedReader reader ->
                assertReaderContent(reader, "/index.html")
                "index mocked content"
            } >> { BufferedReader reader ->
                assertReaderContent(reader, "/plotly-1.52.3.min.js")
                "plotly mocked content"
            } >> { BufferedReader reader ->
                assertReaderContent(reader, "/jquery-1.11.1.min.js")
                "jquery mocked content"
            }
    }

    private void assertReaderContent(BufferedReader actualReader, String fileNameWithExpectedContent) {
        InputStream inputStream = this.class.getResourceAsStream(fileNameWithExpectedContent)
        String expectedContent = new BufferedReader(new InputStreamReader(inputStream)).withReader {
            it.readLines().join("\n")
        }
        String actualContent = actualReader.withReader { it.readLines().join("\n") }
        assert actualContent == expectedContent
    }

    private boolean assertJson(String content, FloExecutionResult result) {
        assert content == JsonOutput.prettyPrint(gson.toJson(result))
        true
    }

}
