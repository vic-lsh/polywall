package packetfilter.firebench.results;

import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Stores benchmark results on dist. */
class BenchResultPersister {

    private BenchmarkResult results;

    private String fileName;

    public BenchResultPersister(BenchmarkResult results, String fileName) {
        this.results = results;
        this.fileName = fileName;
    }

    public void persist() throws StreamWriteException, DatabindException, IOException {

        var mapper = new ObjectMapper();

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(results));
        } catch (IOException e) {
            throw e;
        }
    }

}
