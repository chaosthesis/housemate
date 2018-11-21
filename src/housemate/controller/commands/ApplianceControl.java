package housemate.src.housemate.controller.commands;

import housemate.src.housemate.model.*;

/**
 * This class will be initialized when the Housemate Controller needs a Command
 * to modify the status of a certain type of Device in a specific Room or House.
 * 
 */
public class ApplianceControl extends Command {
    private House house;
    private Room room;
    private String deviceType;
    private String status;
    private String value;

    public ApplianceControl(Room room, String deviceType, String status, String value) {
        this.room = room;
        this.deviceType = deviceType;
        this.status = status;
        this.value = value;
    }

    public ApplianceControl(House house, String deviceType, String status, String value) {
        this.house = house;
        this.deviceType = deviceType;
        this.status = status;
        this.value = value;
    }

    /**
     * Changes statuses of Devices of a certain type to the specified value for the
     * whole Room or House.
     */
    @Override
    public void execute() {
        if (house != null) {
            for (Room r : house.getRoomMap().values()) {
                executeForRoom(r);
            }
        } else if (room != null) {
            executeForRoom(room);
        }
        System.out.println(this);
    }

    /**
     * Changes statuses of Devices of a certain type to the specified value for the
     * whole Room.
     * 
     * @param room The Room that the Appliances are in
     */
    private void executeForRoom(Room room) {
        for (Device d : room.getDeviceMap().values()) {
            if (d.getType().equals(deviceType)) {
                d.setStatus(status, value);
            }
        }
    }

    @Override
    public String toString() {
        return "Adjusted " + deviceType + " " + status + " to " + value;
    }
}
