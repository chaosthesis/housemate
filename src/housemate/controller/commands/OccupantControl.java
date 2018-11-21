package housemate.src.housemate.controller.commands;

import housemate.src.housemate.model.ModelAPI;

/**
 * This class will be initialized when the Housemate Controller needs a Command
 * to update or query the Occupant locaiton/status information in the
 * KnowledgeGraph.
 */
public class OccupantControl extends Command {
    private ModelAPI api;
    private String occupant;
    private String status;
    private String value;

    public OccupantControl(ModelAPI api, String occupant, String status, String value) {
        this.api = api;
        this.occupant = occupant;
        this.status = status;
        this.value = value;
    }

    /**
     * Records Occupant location/state information in the KnowledgeGraph.
     */
    @Override
    public void execute() {
        api.setOccupant(occupant, status, value);
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "Set " + occupant + " as " + status + " " + value;
    }
}