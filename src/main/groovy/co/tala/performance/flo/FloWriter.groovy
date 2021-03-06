package co.tala.performance.flo

import co.tala.performance.converter.InstantConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.transform.PackageScope

import java.time.Instant

/**
 * Writer class for [FloRunner]
 * @param < T >
 */
@PackageScope
class FloWriter<T> implements IFloWriter<T> {
    private final Gson gson
    private final FloRunnaSettings settings
    private final IFloIO floIO

    FloWriter(FloRunnaSettings settings) {
        this.settings = settings
        this.gson = initGson()
        this.floIO = new FloIO()
    }

    FloWriter(FloRunnaSettings settings, IFloIO floIO) {
        this.settings = settings
        this.gson = initGson()
        this.floIO = floIO
    }

    private static Gson initGson() {
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Instant.class, new InstantConverter())
            .create()
    }

    /**
     * Writes all performance test results to the `build/flo-runna-reports` folder.
     * @param results
     */
    @Override
    void writeResults(Map<String, FloExecutionResult<T>> results) {
        GString dir = getDir()
        floIO.mkdirs(dir)
        writeJson(results, dir)
        writeHtml(dir)
    }

    private GString getDir() {
        "build/flo-runna-reports/${settings.testName.replace(" ", "_")}"
    }

    private void writeJson(Map<String, FloExecutionResult<T>> results, String dir) {
        List<String> floStepNames = []
        results
            .sort { it.value.floStepOrder }
            .each { Map.Entry<String, FloExecutionResult<T>> entry ->
                floStepNames.add(entry.key)
                floIO.writeToFile("${dir}/${entry.key}.json", gson.toJson(entry.value))
            }
        floIO.writeToFile("${dir}/floSteps.json", gson.toJson([floSteps: floStepNames]))
    }

    private void writeHtml(String dir) {
        [
            "index.html",
            "plotly-1.52.3.min.js",
            "jquery-1.11.1.min.js"
        ].each { String fileName ->
            this.class.getResourceAsStream("/${fileName}").withCloseable { InputStream inputStream ->
                new BufferedReader(new InputStreamReader(inputStream)).withCloseable { BufferedReader reader ->
                    String content = floIO.readFromBufferedReader(reader)
                    floIO.writeToFile("${dir}/${fileName}", content)
                }
            }
        }
    }
}
