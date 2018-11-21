package housemate.src.housemate.model;

import java.util.HashMap;
import java.util.Map;

public class Room {
    /**
     * The type of the Room.
     */
    private String type;

    /**
     * Private identifier for the Room.
     */
    private String name;

    /**
     * The floor number that the Room is on.
     */
    private int floor;

    /**
     * The number of windows.
     */
    private int windowCount;

    /**
     * Private association for keeping track of which House the Room belongs to.
     */
    private House house;

    /**
     * Private association for maintaining the set of Devices in the Room. Map key
     * is the Device identifier and value is the associated Device. Device
     * identifiers are case insensitive.
     */
    private Map<String, Device> deviceMap;

    public Room(String roomName, int floor, String type, House house, int windowCount) {
        this.name = roomName;
        this.floor = floor;
        this.house = house;
        this.windowCount = windowCount;
        deviceMap = new HashMap<String, Device>();
    }

    /**
     * Returns the identifier of the Room.
     * 
     * @return Room identifier.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the House which the Room belongs to.
     * 
     * @return House.
     */
    public House getHouse() {
        return house;
    }

    /**
     * Returns the deviceMap.
     * 
     * @return deviceMap.
     */
    public Map<String, Device> getDeviceMap() {
        return deviceMap;
    }

    /**
     * Returns a Device instance for the given Device identifier. Use deviceMap to
     * look up the Device. Device names are case insensitive.
     * 
     * @param deviceName Device identifier.
     * @return Device instance.
     */
    public Device getDevice(String deviceName) {
        return deviceMap.get(deviceName);
    }

    /**
     * Public method for adding a Device instance by updating the deviceMap. Map key
     * is the Device identifier and value is the Device.
     * 
     * @param device Device instance.
     */
    public void addDevice(Device device) {
        deviceMap.put(device.getUID(), device);
    }

    /**
     * Returns current energy consumption computed as the sum of energy consumed by
     * each of the Appliances.
     * 
     * @return Total enery consumption of the Room.
     */
    public int getEnergyConsumption() {
        int energyConsumption = 0;
        for (Device device : deviceMap.values()) {
            if (device instanceof Appliance) {
                energyConsumption += device.getEnergyConsumption();
            }
        }
        return energyConsumption;
    }

    /**
     * Public method for showing the configurations of all associated Devices.
     */
    public void showConfiguration() {
        System.out.print(name + ": {");
        for (Device device : deviceMap.values()) {
            device.showConfiguration();
            System.out.print(" ");
        }
        System.out.print("}");
    }
}
