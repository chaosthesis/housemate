package housemate.src.test;

import housemate.src.knowledge.engine.Importer;
import housemate.src.knowledge.engine.QueryEngine;

public class TestDriver {

    /**
     * method should accept 2 parameters, an input Triple file, and an Input Query
     * file. The main method will call the Importer.importFile() method, passing in
     * the name of the provided triple file. After loading the input triples, the
     * main() method will invoke the executeQuery() method passing in the provided
     * query file name.
     * 
     * @throws Exception
     **/
    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            Importer importer = new Importer();
            importer.importTripleFile(args[0]);

            QueryEngine queryEngine = new QueryEngine();
            queryEngine.executeQueryFile(args[1]);
        } else {
            throw new Exception("Usage: TestDriver [Triple_file] [Query_file]");
        }
    }
}