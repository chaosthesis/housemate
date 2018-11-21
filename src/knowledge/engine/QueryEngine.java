package housemate.src.knowledge.engine;

import housemate.src.knowledge.engine.exception.QueryEngineException;

import java.util.Set;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class QueryEngine {
    private KnowledgeGraph knowledgeGraph;

    public QueryEngine() {
        knowledgeGraph = KnowledgeGraph.getInstance();
    }

    /**
     * Public method for executing a single query on the knowledge graph. Checks for
     * non null and well formed query string. Throws QueryEngineException on error.
     * 
     * @param query
     * @throws QueryEngineException
     */
    public void executeQuery(String query) throws QueryEngineException {
        try {
            System.out.println(query);
            Triple queryTriple = new Triple(query.toLowerCase());
            String subject = queryTriple.getSubject().getIdentifier();
            String predicate = queryTriple.getPredicate().getIdentifier();
            String object = queryTriple.getObject().getIdentifier();
            Set<Triple> triples = knowledgeGraph.executeQuery(subject, predicate, object);
            if (triples == null) {
                System.out.println("<null>");
            } else {
                for (Triple triple : triples) {
                    System.out.println(triple.getIdentifier());
                }
            }
            System.out.println();
        } catch (Exception ex) {
            throw new QueryEngineException("Could not execute query file: " + ex);
        }
    }

    /**
     * Public method for executing a set of queries read from a file. Checks for
     * valid file name. Delegates to executeQuery for processing individual queries.
     * Throws QueryEngineException on error.
     * 
     * @param fileName
     * @throws QueryEngineException
     */
    public void executeQueryFile(String fileName) throws QueryEngineException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.length() != 0) {
                    executeQuery(line);
                }
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            throw new QueryEngineException("Could not find file: " + ex);
        } catch (IOException ex) {
            throw new QueryEngineException("Could not read/write: " + ex);
        } catch (Exception ex) {
            throw new QueryEngineException("Could not execute query file: " + ex);
        }
    }
}