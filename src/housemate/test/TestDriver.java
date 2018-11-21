package housemate.src.housemate.test;

import housemate.src.housemate.model.CLI; 

public class TestDriver {
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            CLI cli = new CLI(); 
            cli.executeFile(args[0]);
        } else {
            throw new Exception("Usage: TestDriver [SetupFile]");
        }
    }
}
