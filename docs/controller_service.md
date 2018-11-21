# Housemate Controller Service Design Document

## Introduction

This document defines the design for the Housemate Controller Service.

## Document Organization

This document begins with an overview of the purpose of the House Controller Service,  followed by the requirements that the system must fulfill. Next, a use case diagram, a class diagram, and a sequence diagram are presented to demonstrate the overall software architecture. Next, a class dictionary is listed as the reference for the implementation, followed by a paragraph of implementation details. Finally, exception handling, testing, and risks are discussed to warp up the whole design document.

## Overview

The House Mate Controller Service is responsible for monitoring the state of the sensors and appliances within the home. In addition, the Controller Service is able to generate actions to control the appliances based on rules, in response to status updates from the sensors and appliances.

## Requirements

This section defines the requirements for the House Mate Controller Service. The House Mate Controller Service should support the following functions:

* Monitor Sensor and Appliances for status updates.
* Apply rules that respond to the status updates from sensors and appliances and generate actions.
* Sensor input includes voice commands received via the Ava devices. Note that Ava devices are now considered appliances since they can provide voice feedback to occupants.
* In response to actions, generate and send control messages to Appliances. The House Mate Controller Service should use the interface of the House Mate Model Service to monitor the status of each of the IOT devices installed within the houses. In response to inputs, the Controller Service will use rules to invoke actions. The actions will be executed through the appliance controls.

All rule execution and resulting actions should be logged.

### Design Input

* Apply the Command Pattern and Observer Pattern to implement the interaction between the Model Service and the Controller Service.
* Use the Knowledge Graph to track location and status of occupants.

### Sensor, Stimulus, Rules, Action

The following table defines the behavior for the Controller Service. The Controller Service will monitor all sensors and appliances for each of the houses and rooms. For each stimulus, apply the appropriate rule and action.

| Sensor or Appliance | Stimulus | Rule | Action |
|---------------------|----------|------|--------|
| Ava | Command: "open door" | open the door to the room | set door status to open. |
| Ava | Command: "close door" | close the door to the room. | set door status to closed |
| Ava | Command: "lights off" | turn off all the lights in the room | set light status to off |
| Ava | Command: "lights on" | turn on all the lights in the room | set light status to on |
| Ava | Generic Command: "applicance_type status_name value" | send the command to appliance with matching type in the current room. | forward command to appliance and echo command via Ava. |
| Ava | Question: "where is occupant_name?" For example, "where is Rover?" | use the Knowledge Graph to determine location of occupant. | send response to Ava. "Rover is_located_in kitchen" |
| Camera | Occupant detected | turn on the lights in the room, and increase the thermostat | send command to turn lights on, increase the temperature of the thermostat, update location of occupant in the KG. |
| Camera | Occupant leaving | if no more occupants are in the room, then turn the lights off, and lower the thermostat | turn off lights, decrease the temperature of the thermostat, Update the location of occupant in the KG. |
| Camera | Occupant is inactive | if the only occupant, dim the lights and update the status of the occupant to resting. | update occupant status in KG to resting. |
| Camera | Occupant is active | update occupant status to active | update occupant status in KG to be active. |
| Smoke Detector | Mode Fire | if occupants are in the house, turn on all lights in the house and ask occupants to leave the house. If room has a window and is on the first floor, recommend exiting through the window. Call 911 to let them know there is a fire. | send command to turn on lights. send AVA text to speech: "Fire in the Kitchen, please leave the house immediately". Call 911. |
| Oven | TimeToCook goes to 0 | if oven is on, turn oven off and alert occupants that food is ready. | Turn oven off. send Ava text to speach. "Food is ready" |
| Refrigerator | Beer count changes. | If beer count is less than 4, ask Occupant if they would like to order more beer. If occupant says yes, order more beer. | Send email to store requesting more beer. |

## Use Cases

The admin/occupant interact with API to update Housemate Model Service information both actively or passively. The Housemate Controller Service is notified when any Device status change occurs and takes action following predefined rules. The Controller might send messages to actors of interest per requirements.

![](https://raw.githubusercontent.com/honchao-w/housemate/master/images/controller-uses.jpg)

## Implementation

### Class Diagram

The following class diagram defines the Housemate Controller Service classes contained in the package "housemate.src.housemate.controller".

![Housemate Controller Class Diagram](https://raw.githubusercontent.com/honchao-w/housemate/master/images/controller-classes.jpg)

### Sequence Diagram

The following sequence diagram describes the interaction between the Controller Service and the Model Service.

![Housemate Controller Sequence Diagram](https://raw.githubusercontent.com/honchao-w/housemate/master/images/controller-sequence.jpg)

## Class Dictionary

### DeviceObserver

DeviceObserver acts as an observer for status changes happening to Devices. For each update, certain Commands will be executed accordingly following Housemate rules.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| update | (api:API, device:Device, status:String, value:String):void | Public method for updating the latest change to a device status by calling dedicated private methods for different device types. |
| executeQueue | ():void | Private method for executing all the queued Commands. |
| AvaUpdate | ():void | Private method for handling Ava status updates. It subscribes to voice controls passed to Ava, and turn on/off/update door/light/devices accordingly. It also reponds to queries requesting Occupant location. |
| CameraUpdate | ():void | Private method for handling camera status updates. It subscribes to Occupant status detected by camera, and update light/thermostat accordingly. It also records Occupant location/state in the KnowledgeGraph. |
| SmokeDetectorUpdate | ():void | Private method for handling smoke status updates. It subscribes to smoke status detected by the smoker detector, and notifying Occupants if the House is not empty. It will always notify 911. |
| OvenUpdate | ():void | Private method for handling oven status updates. It subscribes to oven TimeToCook parameter in order to notify Occupants and turn off the Oven when the food is ready. |
| RefrigeratorUpdate | ():void | Private method for handling refrigerator status change. It subscribes to beer count so when the beer is running out, the Occupant will be notified and have the option to reorder more from stores. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| status | String | The changed status that is observed. |
| value | String |  The updated status value. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| api | API | Private association for keeping track of the API instance in order to support some potential Commands. |
| device | Device | Private association for keeping track of the Device with lastest status change. |
| cmdQueue | Queue<Command> | Private association for queueing Commands for subsequtial execution. | 

### Command

Command is an abstract class that has an `execute()` method. It support queueing and logging using queue container and toString() respectively.

Three different types of Commands are identified to handle all the required rules, namely `ApplianceControl`, `OccupantControl`, and `NotificationControl`.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| execute | ():void | Executes self. |
| toString | ():String | Returns a String representing the activities initialized by the Command for logging purpose. |

### ApplianceControl
*extends Command*

`ApplianceControl` will be initialized when the Housemate Controller needs a `Command` to modify the status of a certain type of `Device` in a specific `Room` or `House`.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| execute | ():void | Changes statuses of Devices of a certain type to the specified value for the whole Room or House. |
| executeForRoom | (room:Room):void | Changes statuses of Devices of a certain type to the specified value for the whole Room. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| deviceType | String | The type of Device of interest. |
| status | String | The changed status that is observed. |
| value | String |  The updated status value. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| room | Room | Private association for keeping track of which Room the Appliances are in. |
| house | House | Private association for keeping track of which House the Appliances are in. |

### OccupantControl
*extends Command*

`OccupantControl` will be initialized when the Housemate Controller needs a `Command` to update or query the `Occupant` locaiton/status information in the `KnowledgeGraph`.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| execute | ():void | Records Occupant location/state information in the KnowledgeGraph. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| occupant | Occupant | The name of the detected Occupant. |
| status | String | The changed status that is observed. |
| value | String |  The updated status value. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| api | API | Private association for keeping track of the API instance to use KnowledgeGraph functionalities. |

### NotificationControl
*extends Command*

`NotificationControl` will be initialized when the Housemate Controller needs a `Command` to send message/alert to recipients of interest.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| execute | ():void | Notifies the specified receiver with a message. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| receiver | String | The receiver of the message. |
| msg | String | The message to be passed. |

### Implementation Details

The `DeviceObserver` acts as the core component of the Housemate Controller Service, as it keeps observing any changes happened in the house environment, following principles of the Observer Pattern. When receiving a status update, the `DeviceObserver` will initialize and execute commands according to rules, and the commands are designed based on the Command Pattern. Therefore, the system satisfies all the requirements both function-wise and pattern-wise.

## Exception Handling

Exception is handled by `ControllerException` class. It will be thrown on error observing Housemate Model Service or executing `Commands`.

## Testing

A test driver class called TestDriver with a static main() method is implemented. The main() method accepts one parameter, an input command script file. The main method will call the API class's executeFile method, passing in the name of the provided script file. The TestDriver class is defined within package "housemate.src.housemate.test". Most importantly, the test script attempts to cover all cases and functions implemented in the Controller Class.

## Risks

* A infinite loop might be triggered if multiple events occurs at once and they can trigger each other.
* Scaling current solution can be difficult when the scope of rules grows, which requires a rewrite of `DeviceObserver`.
