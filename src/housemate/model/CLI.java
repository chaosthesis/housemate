package housemate.src.housemate.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.NullPointerException;

import housemate.src.housemate.model.exception.CLIException;
import housemate.src.housemate.controller.ControllerException;
import housemate.src.housemate.entitlement.*;
import housemate.src.housemate.entitlement.factory.*;
import housemate.src.housemate.entitlement.composite.*;

public class CLI {
    private String token[];

    private ModelAPI api;
    private EntitlementAPI entAPI;
    private EntitlementFactory entFactory;
    private AccessToken accessToken;

    public CLI() {
        api = ModelAPI.getInstance();
        entAPI = EntitlementAPI.getInstance();
        entFactory = entAPI.getFactory();
    }

    /**
     * Public method for executing commands read from a script file. Checks for
     * valid input file name. Calls executeCommand to process individual command.
     * Throws CLIException on error accessing or processing the input script file.
     */
    public void executeFile(String fileName) throws CLIException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.length() != 0 && !line.startsWith("#")) {
                    line = line.toLowerCase();
                    System.out.println(line);
                    executeCommand(line);
                }
            }
            reader.close();
        } catch (NullPointerException ex) {
            throw new CLIException("Could not find housemate model object");
        } catch (FileNotFoundException ex) {
            throw new CLIException("Could not find file: " + ex);
        } catch (IOException ex) {
            throw new CLIException("Could not read/write: " + ex);
        } catch (Exception ex) {
            throw new CLIException("Could not execute script file: " + ex);
        }
    }

    /**
     * Private method for executing a single command by the CLI. Calls execute*
     * methods to process different kinds of commands. Checks for non-null and
     * non-comment command string.
     * 
     * @param command the command string
     */
    private void executeCommand(String command) throws ControllerException {
        if (isEntitlementCommand(command)) {
            executeEntitlementCommand(command);
        } else {
            token = command.trim().split("\\s+");
            switch (token[0]) {
            case "login":
                executeLogin();
                break;
            case "logout":
                executeLogout();
                break;
            case "define":
                executeDefine();
                break;
            case "add":
                executeAdd();
                break;
            case "set":
                executeSet();
                break;
            case "show":
                executeShow();
                break;
            }
        }
        token = null;
        System.out.println();
    }

    /**
     * Private method for executing an entitlement command.
     * 
     * @param command the command string
     */
    private void executeEntitlementCommand(String command) {
        token = command.trim().split("\\s*,\\s*");
        switch (token[0]) {
        case "define_permission":
            executeDefinePermission();
            break;
        case "define_role":
            executeDefineRole();
            break;
        case "add_entitlement":
            executeAddEntilement();
            break;
        case "create_user":
            executeCreateUser();
            break;
        case "add_user_credential":
            executeAddUserCredential();
            break;
        case "add_role_to_user":
            executeAddRoleToUser();
            break;
        case "create_resource_role":
            executeCreateResourceRole();
            break;
        case "add_resource_role_to_user":
            executeAddResourceRoleToUser();
            break;
        }
    }

    private void executeLogin() {
        accessToken = entAPI.login(token[1], token[2], token[3]);
    }

    private void executeLogout() {
        entAPI.logout(accessToken);
    }

    private void executeDefinePermission() {
        Permission permission = entFactory.createPermission(token[1], token[2], token[3]);
        entAPI.saveEntitlement(permission);
    }

    private void executeDefineRole() {
        Role role = entFactory.createRole(token[1], token[2], token[3]);
        entAPI.saveEntitlement(role);
    }

    private void executeAddEntilement() {
        Entitlement role = entAPI.getEntitlement(token[1]);
        Entitlement entitlement = entAPI.getEntitlement(token[2]);
        role.add(entitlement);
    }

    private void executeCreateUser() {
        User user = entFactory.createUser(token[1], token[2]);
        entAPI.saveUser(user);
    }

    private void executeAddUserCredential() {
        User user = entAPI.getUser(token[1]);
        user.addCredential(token[2], token[3]);
        entAPI.saveUser(user);
    }

    private void executeAddRoleToUser() {
        User user = entAPI.getUser(token[1]);
        Entitlement role = entAPI.getEntitlement(token[2]);
        user.addRole(role);
    }

    private void executeCreateResourceRole() {
        Resource resource = entAPI.getResource(token[3]);
        Entitlement role = entAPI.getEntitlement(token[2]);
        ResourceRole resourceRole = entFactory.creatResourceRole(token[1], role, resource);
        entAPI.saveResourceRole(resourceRole);
    }

    private void executeAddResourceRoleToUser() {
        User user = entAPI.getUser(token[1]);
        ResourceRole resourceRole = entAPI.getResourceRole(token[2]);
        user.addResourceRole(resourceRole);
    }

    /**
     * Private method for checking if a command is for entitlement purpose.
     * 
     * @param command the command string
     */
    private boolean isEntitlementCommand(String command) {
        return command.startsWith("define_") || command.startsWith("add_") || command.startsWith("create_");
    }

    /**
     * Private method for executing a command that defines Housemate models. For
     * each model that is defined, update catalogs of other models that associate
     * with this model.
     */
    private void executeDefine() {
        switch (token[1]) {
        case "house": {
            api.defineHouse(token[2], token[4], accessToken);
            break;
        }
        case "room": {
            api.defineRoom(token[2], Integer.parseInt(token[4]), token[6], token[8], Integer.parseInt(token[10]),
                    accessToken);
            break;
        }
        case "occupant": {
            api.defineOccupant(token[2], token[4], accessToken);
            break;
        }
        case "sensor": {
            String[] roomToken = token[6].split(":");
            api.defineSensor(token[2], token[4], roomToken[0], roomToken[1], accessToken);
            break;
        }
        case "appliance": {
            String[] roomToken = token[6].split(":");
            if (token.length == 9) {
                api.defineAppliance(token[2], token[4], roomToken[0], roomToken[1], Integer.parseInt(token[8]),
                        accessToken);
            } else {
                api.defineAppliance(token[2], token[4], roomToken[0], roomToken[1], 0, accessToken);
            }
        }
        }
    }

    /**
     * Private method for executing a command that adds an Occupant to a House by
     * updating houseMap the the House object.
     */
    private void executeAdd() {
        api.addOccupant(token[2], token[4], accessToken);
    }

    /**
     * Private method for executing a command that sets the status of a Device,
     * which may be the detected location of an Occupant, or the volume of a
     * speaker.
     */
    private void executeSet() throws ControllerException {
        String[] deviceToken = token[2].split(":");
        api.setDevice(deviceToken[0], deviceToken[1], deviceToken[2], token[4], token[6], accessToken);
    }

    /**
     * Private method for executing a command that queries information from the
     * ModelAPI and displaying the output in a readable format. Delegates to show*
     * methods for showing different kinds of results.
     */
    private void executeShow() {
        switch (token[1]) {
        case "sensor":
        case "appliance":
            showDevice();
            break;
        case "occupant":
            api.showOccupant(accessToken);
            break;
        case "configuration":
            showConfiguration();
            break;
        case "energy-use":
            showEnegyUse();
            break;
        }
        System.out.println();
    }

    /**
     * Private method for showing all or one specific status of a device.
     */
    private void showDevice() {
        String[] deviceToken = token[2].split(":");
        if (token.length == 5) {
            api.showDevice(deviceToken[0], deviceToken[1], deviceToken[2], token[4], accessToken);
        } else if (token.length == 3) {
            api.showDevice(deviceToken[0], deviceToken[1], deviceToken[2], null, accessToken);
        }
    }

    /**
     * Private method for showing the configuration of the whole Housemate Model
     * Service domain or any specific level of the model hierarchy.
     */
    private void showConfiguration() {
        String houseID = null;
        String roomID = null;
        String deviceID = null;
        if (token.length > 2) {
            String[] configToken = token[2].split(":");
            houseID = configToken[0];
            if (configToken.length > 1) {
                roomID = configToken[1];
            }
            if (configToken.length > 2) {
                deviceID = configToken[2];
            }
        }
        api.showConfiguration(houseID, roomID, deviceID, accessToken);
    }

    /**
     * Private method for showing the energy consumption of the whole Housemate
     * model domain or any specific level of the Housemate model hierarchy.
     */
    private void showEnegyUse() {
        String houseID = null;
        String roomID = null;
        String deviceID = null;
        if (token.length > 2) {
            String[] deviceToken = token[2].split(":");
            houseID = deviceToken[0];
            if (deviceToken.length > 1) {
                roomID = deviceToken[1];
            }
            if (deviceToken.length > 2) {
                deviceID = deviceToken[2];
            }
        }
        api.showEnergyUse(houseID, roomID, deviceID, accessToken);
    }

}
