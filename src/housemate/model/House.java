package housemate.src.housemate.model;

import java.util.HashMap;
import java.util.Map;

import housemate.src.housemate.model.Occupant;

public class House {
    /**
     * Private globally unique non-mutable identifier for the House.
     */
    private String guid;

    /**
     * Address of the House.
     */
    private String address;

    /**
     * Private association for maintaining the set of Occupants known to the House.
     * Map key is the Occupant name and value is the associated Occupant. Occupant
     * identifiers are case insensitive.
     */
    private Map<String, Occupant> occupantMap;

    /**
     * Private association for maintaining the set of Rooms in the House. Map key is
     * the Room identifier and value is the associated Room. Room identifiers are
     * case insensitive.
     */
    private Map<String, Room> roomMap;

    /**
     * Private association for maintaining the set of Devices in the House. Map key
     * is the Device identifier and value is the associated Device. Device
     * identifiers are case insensitive.
     */
    private Map<String, Device> deviceMap;

    public House(String house_name, String address) {
        this.guid = house_name;
        this.address = address;
        occupantMap = new HashMap<String, Occupant>();
        roomMap = new HashMap<String, Room>();
        deviceMap = new HashMap<String, Device>();
    }

    /**
     * Returns the globally unique identifier.
     * 
     * @return Globally unique identifier.
     */
    public String getGUID() {
        return guid;
    }

    /**
     * Returns the occupantMap.
     * 
     * @return occupantMap.
     */
    public Map<String, Occupant> getOccupantMap() {
        return occupantMap;
    }

    /**
     * Returns the roomMap.
     * 
     * @return roomMap.
     */
    public Map<String, Room> getRoomMap() {
        return roomMap;
    }

    /**
     * Returns a Room instance for the given Room identifier. Use roomMap to look up
     * the Room. Room names are case insensitive.
     * 
     * @param roomName
     * @return Room instance.
     */
    public Room getRoom(String roomName) {
        return roomMap.get(roomName);
    }

    /**
     * Returns current energy consumption computed by adding up energy consumed by
     * every Room.
     * 
     * @return Total energy consumption in the House.
     */
    public int getEnergyConsumption() {
        int energyConsumption = 0;
        for (Room room : roomMap.values()) {
            energyConsumption += room.getEnergyConsumption();
        }
        return energyConsumption;
    }

    /**
     * Public method for adding an Occupant instance by updating the occupantMap.
     * Map key is the Occupant name and value is the Occupant.
     * 
     * @param occupant Occupant instance.
     */
    public void addOccupant(Occupant occupant) {
        occupantMap.put(occupant.getName(), occupant);
    }

    /**
     * Public method for adding a Room instance by updating the roomMap. Map key is
     * the Room identifier and value is the Room.
     * 
     * @param room Room instance.
     */
    public void addRoom(Room room) {
        roomMap.put(room.getName(), room);
    }

    /**
     * Public method for adding a Device instance by updating the deviceMap. Map key
     * is the Device unique identifier and value is the Device.
     * 
     * @param device Device instance.
     */
    public void addDevice(Device device) {
        deviceMap.put(device.getUID(), device);
    }

    /**
     * Public method for showing the configurations of all associated Rooms.
     */
    public void showConfiguration() {
        System.out.print(guid + ": {");
        for (Room room : roomMap.values()) {
            room.showConfiguration();
            System.out.print(" ");
        }
        System.out.print("}");
    }
}
