package housemate.src.housemate.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import housemate.src.knowledge.engine.*;
import housemate.src.housemate.controller.*;
import housemate.src.housemate.entitlement.*;
import housemate.src.housemate.entitlement.factory.*;

public class ModelAPI {
    private static ModelAPI instance;
    private EntitlementAPI entAPI;
    private EntitlementFactory entFactory;

    private ModelAPI() {
        houseMap = new HashMap<String, House>();
        occupantMap = new HashMap<String, Occupant>();
        occupantStatus = KnowledgeGraph.getInstance();
        observer = new DeviceObserver(this);
        entAPI = EntitlementAPI.getInstance();
        entFactory = entAPI.getFactory();
    }

    /**
     * Public method for returning a reference to the single static ModelAPI
     * instance.
     * 
     * @return single static ModelAPI instance
     */
    public static ModelAPI getInstance() {
        if (instance == null) {
            instance = new ModelAPI();
        }
        return instance;
    }

    /**
     * Private association for maintaining the active set of Houses. Map key is the
     * globally unique identifier and value is the associated House. House
     * identifiers are case insensitive.
     */
    private Map<String, House> houseMap;

    /**
     * Private association for maintaining the active set of Occupants. Map key is
     * the Occupant name and value is the associated Occupant. Occupant identifiers
     * are case insensitive.
     */
    private Map<String, Occupant> occupantMap;

    /**
     * Private association with KnowledgeGraph to keep track of Occupant location
     * and status.
     */
    private KnowledgeGraph occupantStatus;

    /**
     * Private association with DeviceOberver for updating Device status change.
     */
    private DeviceObserver observer;

    /**
     * Public method for defining a House object and updating houseMap.
     * 
     * @param id        globally unique non-mutable identifier for the House
     * @param addr      address of the House
     * @param authToken user token for authentication
     */
    public void defineHouse(String id, String addr, AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }

        House house = new House(id, addr);
        houseMap.put(id, house);

        Resource resource = entFactory.createResource(id, "house");
        entAPI.saveResource(resource);
    }

    /**
     * Public method for defining a Room object and updating the parent House.
     * 
     * @param id        Room identifier
     * @param floor     Which floor the Room is on
     * @param type      Room type
     * @param houseID   Idetifier of the House
     * @param window    The number of windows
     * @param authToken user token for authentication
     */
    public void defineRoom(String id, int floor, String type, String houseID, int window, AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }

        House house = houseMap.get(houseID);
        Room room = new Room(id, floor, type, house, window);
        house.addRoom(room);

        Resource resource = entFactory.createResource(id, "room");
        entAPI.saveResource(resource);
    }

    /**
     * Public method for defining an Occupant object and updating occupantMap.
     * 
     * @param name      Occupant name
     * @param type      Occupant type
     * @param authToken user token for authentication
     */
    public void defineOccupant(String name, String type, AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }

        Occupant occupant = new Occupant(name, type);
        occupantMap.put(name, occupant);

        Resource resource = entFactory.createResource(name, "occupant");
        entAPI.saveResource(resource);
    }

    /**
     * Public method for defining a Sensor object and updating the corresponding
     * Room and House.
     * 
     * @param id        Sensor identifier
     * @param type      Sensor type
     * @param houseID   House identifier
     * @param roomID    Room identifier
     * @param authToken user token for authentication
     */
    public void defineSensor(String id, String type, String houseID, String roomID, AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }

        House house = houseMap.get(houseID);
        Room room = house.getRoom(roomID);
        Sensor sensor = new Sensor(id, type, room);
        room.addDevice(sensor);
        house.addDevice(sensor);

        Resource resource = entFactory.createResource(id, "sensor");
        entAPI.saveResource(resource);
    }

    /**
     * Public method for defining a Appliance object and updating the corresponding
     * Room and House.
     * 
     * @param id        Appliance identifier
     * @param type      Appliance type
     * @param houseID   House identifier
     * @param roomID    Room identifier
     * @param energy    Energy consumption
     * @param authToken user token for authentication
     */
    public void defineAppliance(String id, String type, String houseID, String roomID, int energy,
            AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }

        House house = houseMap.get(houseID);
        Room room = house.getRoom(roomID);
        Appliance appliance = new Appliance(id, type, room, energy);
        room.addDevice(appliance);
        house.addDevice(appliance);

        Resource resource = entFactory.createResource(id, "appliance");
        entAPI.saveResource(resource);
    }

    /**
     * Public method for adding an Occupant to a House.
     * 
     * @param occupantID Occupant name
     * @param houseID    House identifier
     * @param authToken  user token for authentication
     */
    public void addOccupant(String occupantID, String houseID, AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }
        Occupant occupant = occupantMap.get(occupantID);
        House house = houseMap.get(houseID);
        house.addOccupant(occupant);
    }

    /**
     * Public method for setting the status of a Device. If the Device detects an
     * Occupant, update the location information to the KnowledgeGraph.
     * 
     * @param houseID   House identifier
     * @param roomID    Room identifier
     * @param deviceID  Device identifier
     * @param status    Device status key
     * @param value     Device status value
     * @param authToken user token for authentication
     */
    public void setDevice(String houseID, String roomID, String deviceID, String status, String value,
            AccessToken authToken) throws ControllerException {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }
        House house = houseMap.get(houseID);
        Room room = house.getRoom(roomID);
        Device device = room.getDevice(deviceID);
        device.setStatus(status, value);
        observer.update(device, status, value);
    }

    /**
     * Public method for adding occupant location or status into KnowledgeGraph as
     * two Triples.
     * 
     * @param occupant The Occupant's name
     * @param status   The Occupant's status
     * @param value    The value of the Occupant's status
     */
    public void setOccupant(String occupant, String status, String value) {
        occupantStatus.importTriple(occupant, status, value);
    }

    /**
     * Public method for showing device status. Show all the status if the input
     * parameter "status" is null; otherwise only show the specified status.
     * 
     * @param houseID   House identifier
     * @param roomID    Room identifier
     * @param deviceID  Device identifier
     * @param status    Which status to show
     * @param authToken user token for authentication
     */
    public void showDevice(String houseID, String roomID, String deviceID, String status, AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }
        House house = houseMap.get(houseID);
        Room room = house.getRoom(roomID);
        Device device = room.getDevice(deviceID);
        if (status != null) {
            System.out.print(device.getStatus().get(status));
        } else {
            device.showConfiguration();
        }
    }

    /**
     * Public method for getting location of an occupant.
     * 
     * @param occupantName
     * @return Occupant location
     */
    public String queryOccupantLocation(String occupantName) {
        Set<Triple> triples = occupantStatus.executeQuery(occupantName, "is_in", "?");
        if (triples == null) {
            return "not found";
        } else {
            return triples.iterator().next().getIdentifier();
        }
    }

    /**
     * Public method for showing location and status of an occupant.
     * 
     * @param authToken user token for authentication
     */
    public void showOccupant(AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }
        Set<Triple> triples = occupantStatus.executeQuery("?", "?", "?");
        if (triples == null) {
            System.out.print("<null>");
        } else {
            for (Triple triple : triples) {
                System.out.print("{" + triple.getIdentifier() + "} ");
            }
        }
    }

    /**
     * Public method for showing the configuration of the whole Housemate Model
     * Service domain or any specific object inside the model hierarchy.
     * 
     * @param houseID   House identifier
     * @param roomID    Room identifier
     * @param deviceID  Device identifier
     * @param authToken user token for authentication
     */
    public void showConfiguration(String houseID, String roomID, String deviceID, AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }
        if (houseID == null) {
            for (House house : houseMap.values()) {
                house.showConfiguration();
            }
            return;
        }
        House house = houseMap.get(houseID);
        if (roomID == null) {
            house.showConfiguration();
            return;
        }
        Room room = house.getRoom(roomID);
        if (deviceID == null) {
            room.showConfiguration();
            return;
        }
        Device device = room.getDevice(deviceID);
        device.showConfiguration();
    }

    /**
     * Public method for showing the energy consumption of the whole Housemate model
     * domain or any specific object inside the model hierarchy.
     * 
     * @param houseID   House identifier
     * @param roomID    Room identifier
     * @param deviceID  Device identifier
     * @param authToken user token for authentication
     */
    public void showEnergyUse(String houseID, String roomID, String deviceID, AccessToken authToken) {
        try {
            entAPI.checkAccess(authToken);
        } catch (InvalidAccessTokenException ex) {
            System.err.println("Invalid Access");
            return;
        }
        if (houseID == null) {
            for (House house : houseMap.values()) {
                System.out.print(house.getGUID() + ":" + house.getEnergyConsumption() + " ");
            }
            return;
        }
        House house = houseMap.get(houseID);
        if (roomID == null) {
            System.out.print(house.getEnergyConsumption());
            return;
        }
        Room room = house.getRoom(roomID);
        if (deviceID == null) {
            System.out.print(room.getEnergyConsumption());
            return;
        }
        Device device = room.getDevice(deviceID);
        System.out.print(device.getEnergyConsumption());
    }
}
