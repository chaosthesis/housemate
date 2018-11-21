# Housemate Model Design Document

## Introduction

This document defines the design for the Housemate Model Service.

## Overview

The Housemate Model Service is responsible for maintaining the state of the sensors and appliances within the home. Sensors are able to collect and share data. Like Sensors, Appliances can collect and share data. However, appliances can also be controlled. In order to enable the whole workflow, entities such House, Room, Occupant also needs to be defined.

## Requirements

This section defines the requirements for the Housemate Model Service.

The objective of Housemate Model Service is to deliver an **API** interface, so the administrator will have the ability to manage Housemate models. The API should support the following functions: `define`, `add`, `set`, and `show`. For example, these functions can be used for modifying the house configuration, controlling appliance state, accessing sensor and appliance state, and determining current energy consumption of the house, room or appliance, etc. A auth_token parameter should also be included for later support of access control.

The following entities needs to be modeled, and made accessible by API:

- **House**
The House is used to model a house. Note that Housemate is a cloud based service and should be able to manage multiple Houses. A House contains: globally unique identifier, address (street, city, state), zero or more occupants, one or more rooms, zero or more IOT Devices, and current energy consumption of the House (sum of energy consumed by each of the rooms).

- **Room** 
The Room is used to model a room of the house. Room identifiers are used to track the location of occupants. A Room contains the following: type of Room (Kitchen, Closet, Dining Room, etc), floor of the house that the room is on, unique name of the room within the scope of the house, number of windows, and current current energy consumption as an aggregate of appliances in the room that are turned on.

- **Occupant**
Occupant represents a person or animal. Occupants are recognized by the HouseMate system through facial and voice recognition. Cameras and Microphones located in each room of the house monitor the location of all occupants. Persons can be either Adults or Children. Animals are usually pets. Occupants can be known (family member or friend) or unknown (e.g. guest or burglar). All occupants have a name for reference. Occupants also have a status, either active or sleeping. Note that the same occupant can be recognized by more than one house.

- **Sensor**
Sensors are IoT devices and capture and share data about the conditions within the house. Examples of Sensors include: smoke detector, camera (monitors location of occupants), and Ava (listening device for receiving voice commands from occupants). Each sensor records data specific to its type. The data recorded by the sensor is automatically sent to the Housemate System. Each sensor has a unique identifier. Sensors are also located within a room of the house. In summary Sensors have the following features: unique identifier, state, room, location within the house, and sensor type.
    
- **Appliance**
An Appliance is similar to a Sensor since it is able to record and share data about itself or its surroundings. An Appliance differs from a Sensor since it can be also be controlled. Examples of Appliances include: thermostat (adjust room temperature), window (open, close), door (open, close, lock), light (on, off, dim, brighten), TV (channel, power, volume), Pandora (channel, power, volume), oven (power, temperature, time to cook), and refrigerator (temperature, beer count, clean). Appliances have a property that specifies their energy consumption in Watts.

###  Command API
    
The Housemate Model Service supports a Command Line Interface (CLI) for configuring houses. The commands can be listed in a file to provide a configuration script. The CLI should be use the service interface to implement the commands.

#### Command Syntax:

	define house [house_name] address [address]
	# define a new house instance

	define room [room_name] floor [floor] type [room_type] house [house_name] windows [window_count]
	# define a new room instance and attach to a house

	  

	define occupant [occupant_name] type [occupant_type]
	# define a new occupant instance

	add occupant [occupant_name] to_house [house_name]
	#associate an occupant to a house, note that occupants can be associated with more than one house

	define sensor [name] type [sensor_type] room [house_name]:[room_name]
	# create a new sensor instance

	define appliance [name] type [sensor_type] room [house_name]:[room_name] energy-use [energy-use]
	# create a new appliance instance

	set sensor|appliance [house_name]:[room_name]:[name] status [status_name] value [value]
	# set the sensor value

	show sensor|appliance [house_name]:[room_name]:[name] status [status]
	# get the sensor value

	show sensor|appliance [house_name]:[room_name]:[name]
	# show the entire sensor status
	
	show configuration
	# show all houses and their configuration

	show configuration [house_name]
	# show configuration for the given house

	show configuration [house_name]:[room_name]
	# show configuration for specified room

	show configuration [house_name]:[room_name]:[appliance]
	# show configuration for specified appliance

	show energy-use
	# show the current energy cosumption in Watts of all houses

	show energy-use [house_name]
	# show the current energy cosumption in Watts of a house

	show energy-use [house_name]:[room_name]
	# show the current energy cosumption in Watts of a room

	show energy-use [house_name]:[room_name]:[appliance]
	# show the current energy cosumption in Watts of a appliance

## Use Cases

The Housemate Model Service Class supports one primary use case: the administrator interact with API to add/query Housemate information.

![](https://raw.githubusercontent.com/honchao-w/housemate/master/images/model-uses.jpg)

## Implementation

### Class Diagram

The following class diagram defines the Housemate model implementation classes contained within the package "housemate.src.housemate.model".

![Housemate Model Class Diagram](https://raw.githubusercontent.com/honchao-w/housemate/master/images/model-classes.jpg)

## Class Dictionary

### API

The Housemate Model Service provides a service interface for managing the state of the house.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| getInstance | ():API | Public method for returning a reference to the single static API instance. |
| defineHouse | (id:String, addr:String):void | Public method for defining a House object and updating houseMap. |
| defineRoom | (id:String, floor:int, type:String, houseID:String, window:int):void | Public method for defining a Room object and updating the parent House. |
| defineOccupant | (name:String, type:String):void | Public method for defining an Occupant object and updating occupantMap. |
| defineSensor | (id:String, type:String, houseID:String, roomID:String):void | Public method for defining a Sensor object and updating the corresponding Room and House. |
| defineAppliance | (id:String, type:String, houseID:String, roomID:String, energy:int):void | Public method for defining a Appliance object and updating the corresponding Room and House. |
| addOccupant | (occupantID:String, houseID:String):void | Public method for adding an Occupant to a House. |
| setDevice | (houseID:String, roomID:String, deviceID:String, status:String, value:String):void | Public method for setting the status of a Device. If the Device detects an Occupant, update the location information to the KnowledgeGraph. |
| showDevice | (houseID:String, roomID:String, deviceID:String, status:String):void | Public method for showing device status. Show all the status if the input parameter "status" is null; otherwise only show the specified status. |
| showOccupant | ():void | Private method for showing location and status of an occupant. |
| showConfiguration | (houseID:String, roomID:String, deviceID:String):void | Public method for showing the configuration of the whole Housemate Model Service domain or any specific object inside the model hierarchy. |
| showEnergyUse | ():void| Public method for showing the energy consumption of the whole Housemate model domain or any specific object inside the model hierarchy. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| auth_token | String | Private token reserved to support access control later. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| houseMap | Map<String, House> | Private association for maintaining the active set of Houses. Map key is the globally unique identifier and value is the associated House. House identifiers are case insensitive. |
| occupantMap | Map<String, Occupant> | Private association for maintaining the active set of Occupants. Map key is the Occupant name and value is the associated Occupant. Occupant identifiers are case insensitive. |
| occupantStatus | KnowledgeGraph | Private association with KnowledgeGraph to keep track of Occupant location and status. |

### CLI

The CLI interface can be used to interact with public API methods to add/modify Housemate Model Service objects.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| executeFile | (fileName:String):void | Public method for executing commands read from a script file. Checks for valid input file name. Calls executeCommand to process individual command. Throws APIException on error accessing or processing the input script file. |
| executeCommand | (command:String):void | Private method for executing a single command by the CLI. Calls execute* methods to process different kinds of commands. Checks for non-null and non-comment command string. |
| executeDefine | ():void | Private method for executing a command that defines Housemate models. For each model that is defined, update catalogs of other models that associate with this model. |
| executeAdd | ():void | Private method for executing a command that adds an Occupant to a House by updating houseMap the the House object. |
| executeSet | ():void | Private method for executing a command that sets the status of a Device, which may be the detected location of an Occupant, or the volume of a speaker. |
| executeShow | ():void | Private method for executing a command that queries information from the API and displaying the output in a readable format. Delegates to show* methods for showing different kinds of results. |
| showDevice | ():void | Private method for showing all or one specific status of a device. |
| showConfiguration | ():void | Private method for showing the configuration of the whole Housemate Model Service domain or any specific level of the model hierarchy. |
| showEnergyUse | ():void| Private method for showing the energy consumption of the whole Housemate model domain or any specific level of the Housemate model hierarchy. |

### House

The House is used to model a house. Note that Housemate is a cloud based service and should be able to manage multiple Houses.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| getGUID | ():String | Returns the globally unique identifier. |
| getRoom | (roomName:String):Room | Returns a Room instance for the given Room identifier. Use roomMap to look up the Room. Room names are case insensitive. |
| getEnergyConsumption | ():int | Returns current energy consumption computed by adding up energy consumed by every Room. |
| addOccupant | (occupant:Occupant):void | Public method for adding an Occupant instance by updating the occupantMap. Map key is the Occupant name and value is the Occupant. |
| addRoom | (room:Room):void | Public method for adding a Room instance by updating the roomMap. Map key is the Room identifier and value is the Room. |
| addDevice | (device:Device):void | Public method for adding a Device instance by updating the deviceMap. Map key is the Device unique identifier and value is the Device. |
| showConfiguration | ():void | Public method for showing the configurations of all associated Rooms. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| guid | String | Private globally unique non-mutable identifier for the House. |
| address | String | Address of the House. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| occupantMap | Map<String, Occupant> | Private association for maintaining the set of Occupants known to the House. Map key is the Occupant name and value is the associated Occupant. Occupant identifiers are case insensitive. |
| roomMap | Map<String, Room> | Private association for maintaining the set of Rooms in the House. Map key is the Room identifier and value is the associated Room. Room identifiers are case insensitive. |
| deviceMap | Map<String, Device> | Private association for maintaining the set of Devices in the House. Map key is the Device identifier and value is the associated Device. Device identifiers are case insensitive. |

### Occupant

Occupant represents a person or animal. Occupants are recognized by the HouseMate system through facial and voice recognition. Cameras and Microphones located in each room of the house monitor the location of all occupants. Persons can be either Adults or Children. Animals are usually pets. Occupants can be known (family member or friend) or unknown (e.g. guest or burglar). All occupants have a name for reference. Occupants also have a status, either active or sleeping. Note that the same occupant can be recognized by more than one house.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| getName | ():String | Returns the name of the Occupant. |
| getLocation | ():String | Returns the identifier of the Room which the Occupant is currently in. |
| getType | ():String | Returns the Occupant type, determining whether the Occupant is resident or guest. |
| getStatus | ():String | Returns the Occupant status, which can be active or sleeping. |
| setLocation | (location:String):void | Public method for setting current location of the Occupant. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| name | String | Private unique identifier for the Occupant. Occupant names are case insensitive. |
| location | String | Private identifier of the Room which indicates where is the Occupant. |
| type | String | The Occupant type. |
| status | String | Private String that indicates whether the Occupant is active or sleeping. |

### Room

The Room is used to model a room of the house. A Room is maintained by a House through its unique identifier within the House. A Room also consists of any number of Devices.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| getName | ():String | Returns the identifier of the Room. |
| getDevice | (deviceName:String):Device | Returns a Device instance for the given Device identifier. Use deviceMap to look up the Device. Device names are case insensitive. |
| addDevice | (device:Device):void | Public method for adding a Device instance by updating the deviceMap. Map key is the Device identifier and value is the Device. |
| getEnergyConsumption | ():int | Returns current energy consumption computed as the sum of energy consumed by each of the Appliances. |
| showConfiguration | ():void | Public method for showing the configurations of all associated Devices. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| type | String | The type of the Room. |
| name | String | Private identifier for the Room. |
| floor | int | The floor number that the Room is on. |
| windowCount | int | The number of windows. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| house | House | Private association for keeping track of which House the Room belongs to. |
| deviceMap | Map<String, Device> | Private association for maintaining the set of Devices in the Room. Map key is the Device identifier and value is the associated Device. Device identifiers are case insensitive. |

### Device

The Device class is used to model the IoT devices managed by the Housemate Model Service. It maintains a statusMap to keep track of its own status key and status value.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| getUID | ():String | Returns the unique identifier. |
| getRoom | ():Room | Returns a Room instance for the given Room identifier. Use roomMap to look up the Room. Room names are case insensitive. |
| getStatus | ():Map<String, String> | Returns current status value for the given status identifier. Use statusMap to look up the status. |
| getEnergyConsumption | ():int | Returns zero if the Device is not initialized as an Appliance, otherwise returns its current energy consumption. |
| setStatus | (statusName:String, value:String):void | Public method for setting a status by updating the statusMap. Map key is the status identifier and value is the status value. |
| showConfiguration | ():void | Public method for showing all configurations of this Device. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| uid | String | Private unique non-mutable identifier for the Device. |
| type | String | The type of the Device. |
| statusMap | Map<String, Device> | Private map for maintaining the set of statuses and their values. Map key is the status name and value is the status value. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| room | Room | Private association for bookkeeping which Room is the Device installed in. |

### Sensor
*extends Device*

Sensors are IoT devices and capture and share data about the conditions within the house. Each sensor records data specific to its type. The data recorded by the sensor is automatically sent to the Housemate System. Each sensor has a unique identifier. Sensors are also located within a room of the house.

### Appliance

*extends Device*

An Appliance is similar to a Sensor since it is able to record and share data about itself or its surroundings. An Appliance differs from a Sensor since it can be also be controlled.

#### Methods

| Method Name | Signature | Description |
|---------------|--------|---------------|
| getEnergyConsumption | ():int | Returns current energy consumption of the Appliance. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| energyConsumption | int | An integer denoting the energy consumption in Watts. |

## Exception Handling

Exception is handled by CLIException class. It will be thrown on error accessing or processing the input script file.

## Testing

A test driver class called TestDriver with a static main() method is implemented. The main() method accepts one parameter, an input command script file. The main method will call the API class's executeFile method, passing in the name of the provided script file. The TestDriver class is defined within package "housemate.src.housemate.test"

## Risks

* Because of the in memory implementation, the number of Housemate models that can be initialized is limited by the memory allocated to the JVM.
* Because the API class  encapsulates the KnowledgeGraph class which is defined in another package, we need to make sure KnowledgeGraph is in the classpath and working as intended.
