package housemate.src.knowledge.engine;

import housemate.src.knowledge.engine.exception.ImportException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Importer class is responsible for reading triples from input files using
 * N-Triple format. The Importer class creates a Triple instance for each line
 * read from the input file and passes the resulting Triples to the
 * KnowledgeGraph.importTriples() method. Also, only fully qualified Triples
 * (i.e. subject, predicate, object all have identifiers) should be added to the
 * Knowledge Graph. Trim extra leading and trailing whitespace from identifier
 * names. The importTripleFile method throws an ImportException on error
 * processing the input file.
 */

public class Importer {
    private KnowledgeGraph knowledgeGraph;

    public Importer() {
        knowledgeGraph = KnowledgeGraph.getInstance();
    }

    /**
     * Public method for importing triples from N_Triple formatted file into the
     * KnowledgeGraph. Checks for valid input file name. Throws ImportException on
     * error accessing or processing the input Triple File.
     * 
     * @param fileName
     * @throws ImportException
     */
    public void importTripleFile(String fileName) throws ImportException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.length() != 0) {
                    line = line.toLowerCase();
                    String[] triples = line.split("[\\s* .\\s*]");
                    knowledgeGraph.importTriple(triples[0], triples[1], triples[2]);
                }
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            throw new ImportException("Could not find file: " + ex);
        } catch (IOException ex) {
            throw new ImportException("Could not  read/write: " + ex);
        } catch (Exception ex) {
            throw new ImportException("Could not import Triple file: " + ex);
        }
    }

}