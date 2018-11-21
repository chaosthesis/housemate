package housemate.src.housemate.model;

import java.util.HashMap;
import java.util.Map;

public class Device {
    /**
     * Private unique non-mutable identifier for the Device.
     */
    private String uid;

    /**
     * The type of the Device.
     */
    private String type;

    /**
     * Private association for keeping track of which Room is the Device installed
     * in.
     */
    private Room room;

    /**
     * Private map for maintaining the set of statuses and their values. Map key is
     * the status name and value is the status value.
     */
    private Map<String, String> statusMap;

    /**
     * 
     * @param uid
     * @param type
     * @param room
     */
    public Device(String uid, String type, Room room) {
        this.uid = uid;
        this.type = type;
        this.room = room;
        statusMap = new HashMap<String, String>();
    }

    /**
     * Returns the unique identifier.
     * 
     * @return Unique identifier.
     */
    public String getUID() {
        return uid;
    }

    /**
     * Returns the device type.
     * 
     * @return Device type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns a Room instance for the given Room identifier. Use roomMap to look up
     * the Room. Room names are case insensitive.
     * 
     * @return Room instance.
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Returns current status value for the given status identifier. Use statusMap
     * to look up the status.
     * 
     * @return Device status.
     */
    public Map<String, String> getStatus() {
        return statusMap;
    }

    /**
     * Returns zero if the Device is not initialized as an Appliance, otherwise
     * returns its current energy consumption.
     * 
     * @return Energy use of the Device.
     */
    public int getEnergyConsumption() {
        return 0;
    }

    /**
     * Public method for setting a status by updating the statusMap. Map key is the
     * status identifier and value is the status value.
     * 
     * @param statusName The name of the status.
     * @param value      The value of the status.
     */
    public void setStatus(String statusName, String value) {
        statusMap.put(statusName, value);
    }

    /**
     * Public method for showing all configurations of this Device.
     */
    public void showConfiguration() {
        System.out.print(uid + ": {");
        for (Map.Entry<String, String> entry : statusMap.entrySet()) {
            System.out.print(entry.getKey() + ": " + entry.getValue() + " ");
        }
        System.out.print("}");
    }
}
