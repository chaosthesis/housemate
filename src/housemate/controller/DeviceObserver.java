package housemate.src.housemate.controller;

import java.util.Queue;
import java.util.LinkedList;

import java.lang.NullPointerException;

import housemate.src.housemate.model.*;
import housemate.src.housemate.controller.commands.*;
import housemate.src.housemate.controller.ControllerException;

/**
 * This class acts as an observer for status changes happening to Devices. For
 * each update, certain Commands will be executed accordingly following
 * Housemate rules.
 */
public class DeviceObserver {
    private ModelAPI api;
    private House house;
    private Room room;
    private Device device;
    private String status;
    private String value;

    private Queue<Command> cmdQueue;

    public DeviceObserver(ModelAPI api) {
        this.api = api;
        cmdQueue = new LinkedList<Command>();
    }

    /**
     * Public method for updating the latest change to a device status by calling
     * dedicated private methods for different device types.
     * 
     * @param api    The Housemate Model Service ModelAPI
     * @param device The Device with status change
     * @param status The status that was updated
     * @param value  The updated value of a status
     */
    public void update(Device device, String status, String value) throws ControllerException {
        try {
            this.device = device;
            room = device.getRoom();
            house = room.getHouse();
            this.status = status;
            this.value = value;

            switch (device.getType()) {
            case "ava":
                AvaUpdate();
                break;
            case "camera":
                CameraUpdate();
                break;
            case "smoke_detector":
                SmokeDetectorUpdate();
                break;
            case "oven":
                OvenUpdate();
                break;
            case "refrigerator":
                RefrigeratorUpdate();
                break;
            }
            executeQueue();
        } catch (NullPointerException ex) {
            throw new ControllerException("Could not find housemate model object" + ex);
        } catch (Exception ex) {
            throw new ControllerException("Could not update to observer: " + ex);
        }
    }

    /**
     * Private method for executing a Command by evoking the execute() method of the
     * Command class.
     */
    private void executeQueue() {
        while (!cmdQueue.isEmpty()) {
            cmdQueue.poll().execute();
        }
    }

    /**
     * Private method for handling Ava status updates. It subscribes to voice
     * controls passed to Ava, and turn on/off/update door/light/devices
     * accordingly. It also reponds to queries requesting Occupant location.
     */
    private void AvaUpdate() {
        if (status.equals("voice_command")) {
            if (value.equals("open_door")) {
                cmdQueue.add(new ApplianceControl(room, "door", "status", "open"));
            } else if (value.equals("close_door")) {
                cmdQueue.add(new ApplianceControl(room, "door", "status", "closed"));
            } else if (value.equals("lights_off")) {
                cmdQueue.add(new ApplianceControl(room, "light", "power", "off"));
            } else if (value.equals("lights_on")) {
                cmdQueue.add(new ApplianceControl(room, "light", "power", "on"));
            } else if (value.startsWith("where:is")) {
                String[] token = value.split(":");
                cmdQueue.add(new NotificationControl("occupants", api.queryOccupantLocation(token[2])));
            } else {
                String[] token = value.split(":");
                cmdQueue.add(new ApplianceControl(room, token[0], token[1], token[2]));
            }
        }
    }

    /**
     * Private method for handling camera status updates. It subscribes to Occupant
     * status detected by camera, and update light/thermostat accordingly. It also
     * records Occupant location/state in the KnowledgeGraph.
     */
    private void CameraUpdate() {
        switch (status) {
        case "occupant_detected":
            cmdQueue.add(new ApplianceControl(room, "light", "power", "on"));
            cmdQueue.add(new ApplianceControl(room, "thermostat", "temperature", "higher"));
            cmdQueue.add(new OccupantControl(api, value, "is_in", house.getGUID() + ":" + room.getName()));
            break;
        case "occupant_left":
            cmdQueue.add(new ApplianceControl(room, "light", "power", "off"));
            cmdQueue.add(new ApplianceControl(room, "thermostat", "temperature", "lower"));
            break;
        case "occupant_inactive":
            cmdQueue.add(new OccupantControl(api, value, "is", "inactive"));
            break;
        case "occupant_active":
            cmdQueue.add(new OccupantControl(api, value, "is", "active"));
            break;
        }
    }

    /**
     * Private method for handling smoke status updates. It subscribes to smoke
     * status detected by the smoker detector, and notifying Occupants if the House
     * is not empty. It will always notify 911.
     */
    private void SmokeDetectorUpdate() {
        if (status.equals("fire") && value.equals("on")) {
            if (!house.getOccupantMap().isEmpty()) {
                cmdQueue.add(new ApplianceControl(house, "light", "power", "on"));
                cmdQueue.add(new NotificationControl("occupants", "fire in the kitchen, leave house now"));
            }
            cmdQueue.add(new NotificationControl("911", "fire alert"));
        }
    }

    /**
     * Private method for handling oven status updates. It subscribes to oven
     * TimeToCook parameter in order to notify Occupants and turn off the Oven when
     * the food is ready.
     */
    private void OvenUpdate() {
        if (status.equals("time_to_cook") && Integer.parseInt(value) == 0) {
            device.setStatus("power", "off");
            cmdQueue.add(new NotificationControl("occupants", "food is ready"));
        }
    }

    /**
     * Private method for handling refrigerator status change. It subscribes to beer
     * count so when the beer is running out, the Occupant will be notified and have
     * the option to reorder more from stores.
     */
    private void RefrigeratorUpdate() {
        if (status.equals("beer_count") && Integer.parseInt(value) < 4) {
            cmdQueue.add(new NotificationControl("store", "ordering more beer"));
        }
    }
}