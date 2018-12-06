# Housemate Entitlement Service Design Document

## Introduction

This document defines the design for the Housemate Entitlement Service.

## Document Organization

This document begins with an overview of the purpose of the House Entitlement Service,  followed by the requirements that the system must fulfill. Next, a use case diagram, a class diagram, and a sequence diagram are presented to demonstrate the overall software architecture. Next, a class dictionary is listed as the reference for the implementation, followed by a paragraph of implementation details. Finally, exception handling, testing, and risks are discussed to warp up the whole design document.

## Overview

The House Mate System is designed to fully automate the home. The House Mate Service allows residents to control their home environment through voice commands. Sensors monitor the location of individuals within the home. Smart lights, doors, windows, thermostats, and other appliances are controlled through the House Mate system. Some of these appliances are controlled automatically based on the location of the residents, while voice commands can be used to activate or override others.

Security is an important aspect of the House Mate System. It is critical that the House Mate system only allow trusted parties to see the status of and control the IOT devices.

## Requirements

### House Mate Integration

The Entitlement Service will be used to control access to the Model Service. The Model Service will be updated to use the Access Token passed in to each of the service methods and pass this to the Entitlement Service to validate that the associated user has the permission required to invoke the method.

When new Occupants are created, a corresponding user will be created within the Entitlement Service with a default voice template.

Also, when occupants are associated with a house, a Resource Role will be created within the Entitlement Service that binds the occupant with the house and the appropriate Role.

The command script processor will be extended to support the Entitlement Service commands as documented later in this document. Script commands must be preceded with a login request using an administrator username and password in order to obtain an Access Token.

The Control Service will use the Entitlement Service to request an authentication token to access the House Mate Service API. For voice commands received from the Ava device, the Controller Service will use a voice print to obtain an access token for the user making the request. The resulting Auth Token will be used for calling the House Mate Model Service methods. In this way, the entitlements associated with the Occupant making the request will be used to authorize or deny the request.

### Entitlement Service

The Entitlement Service is responsible for controlling access to the House Mate Model Service interface. The Entitlement Service provides a central point for managing Users, Resources, Permissions, Roles, ResourceRoles, and Access Tokens.

The Entitlement Service supports 2 primary actors: Administrator and Occupants.

The Administrator is responsible for managing the Resources, Permissions, Roles, and Users maintained by the Entitlement Service. The Administrator also provisions houses within the HouseMate system and has full access to all resources within the HouseMate System. Occupants are identified by their voice print.

Occupants use voice commands to interact with the HouseMate System. The Entitlement Service supports identifying and authenticating users using the user’s voice print. When an Occupant issues a voice command, the Controller service sends the voice print to the Entitlement Service. The Entitlement Service finds the user with a matching voice print and returns an Authentication Token for the user. This Authentication token is used when invoking Model Service methods on behalf of the Occupant. In this way, only Occupants with the appropriate permissions are allowed to control appliances within the house.

### Entitlement Service API

The Entitlement Service API supports the following functions:

-   Creating Resources:
    -   A Resource represents a physical and logical entity, for example a Sensor or Appliance.
    -   A Resource has a unique ID, and a description.
-   Creating Permissions:
    -   Permissions represent an entitlement required to access a resource or function of the House Mate system.
    -   Permissions have a unique id, name, and description.
    -   A User may be associated with zero or more permissions.
-   Creating Roles:
    -   Roles are composites of Permissions.
    -   Roles provide a way to group Permissions and other Roles.
    -   Like Permissions, Roles have a unique id, name, and description.
    -   Users may be associated with Roles, where the user has all permissions included in the Role or sub Roles.
    -   Roles help simplify the administration of Users by providing reusable and logical groupings of Permissions and Roles.
-   Creating Users:
    -   Users represent registers users of the House Mate system.
    -   Users have an id, a name and a set of Credentials. Credentials include a username and a password. To protect the password, the password should be hashed.
    -   Users are associated with 0 or more Entitlements(Roles or Permissions).
-   Authentication
    -   The Authentication process provides users AccessTokens that can then be used to access restricted Service Methods.
    -   If authentication fails, an AuthenticationException should be thrown.
    -   If authentication succeeds, an AccessToken is created and returned to the caller.
    -   The accessToken binds the User to a set of permissions that can be used to grant or deny access to restricted methods.
    -   AccessTokens can timeout with inactivity.
    -   AccessTokens have a unique id, an expiration time, and a state (active or expired).
    -   Access tokens are associated with a User and a set of Permissions.
	    - Login
		    - Login accepts a User’s credentials (username, password).
            - A check is made to make sure that the username exists, and then that the hash of the password matches the known hashed password.
	    - VoicePrint
		    - Voiceprint supports authentication through voice recognition
			-  Voiceprint is simulated using a string in this format:  “--username--”. For example the voice print for sam is  “--sam--”.
		    -   The voice print signature is sufficient for identifying and authenticating a user.
    - Logout
	    -   Logout marks the given Access Token as invalid.
	    -   Subsequent attempts to use the AccessToken should result in a InvalidAccessTokenException.
    - House Mate Model Service
	    - All methods defined within the House Mate Model Service should accept a AccessToken
	    -   Each method should validate that the AccessToken is non null and non empty.
	    -   The method should pass the accessToken to the Entitlement Service with the permission required for the method.
	    -   The AuthenticationService should check to make sure that the AccessToken is active, and within the expiration period, and then check that the user associated with the AccessToken has the permission required by the method.
	    -   The AuthenticationService should throw an AccessDeniedException or InvalidAccessTokenException if any of the checks fail.

Exceptions should include useful information to help users understand the nature of the Exception.

## Use Cases

The admin uses Housemate Entitlement Service API to create resource, entitlements, users, etc. Both admin and created user can login/logout the system, and interact with IoT devices controlled by Housemate Controller Service which depends on Housemate Model Service. The difference between admin and user is that user's permissions are restricted so that not all actions can be initialized by user. This is achieved by checkAccess method.

The following use case diagram shows the use cases supported by the Housemate Entitlement Service.

![Housemate Entitlement Use Cases](https://raw.githubusercontent.com/honchao-w/housemate/master/images/entitlement-uses.png)

## Implementation

### Class Diagram

The following class diagram defines the Housemate Entitlement Service classes contained in the package "housemate.src.housemate.entitlement".

![Housemate Entitlement Class Diagram](https://raw.githubusercontent.com/honchao-w/housemate/master/images/entitlement-classes.png)

### Sequence Diagram

The following sequence diagram describes the interaction between the Entitlement Service and the Model Service.

![Housemate Entitlement Sequence Diagram](https://github.com/honchao-w/housemate/blob/master/images/entitlement-sequence.png)

## Class Dictionary

### AccessToken

AccessToken is a class that contains information about user login status and binded permission.

#### Methods

| Method Name | Signature | Description |
|-------------|-----------|-------------|
| invalidate | ():void | Public method for invalidating the token. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| guid | String | Globally-unique token ID. |
| lastUsed | String | Timestamp of last access. |
| state | String | Token state. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| user | User | Private association with the User. |


### EntitlementAPI

EntitlementAPI is a class that provides generic public methods for other modules to integrate authentication and authorization functions.

#### Methods

| Method Name | Signature | Description |
|-------------|-----------|-------------|
| getInstance | ():EntitlementAPI | Public method for getting the EntitlementAPI singleton instance. |
| checkAccess | (token:AccessToken):void | Public method for checking if token has access. |
| login | (id:String, type:String, key:String):AccessToken | Public method for logging in users with correct credential. |
| logout | (token:AccessToken):void | Public method for user to logout. |
| saveUser | (user:User):void | Public method for adding newly created user to the catalog. |
| saveEntitlement | (entitlement:Entitlement):void | Public method for adding newly created entitlement to the catalog. |
| saveResource | (resource:Resource):void | Public method for adding newly created resource to the catalog. |
| saveResourceRole | (resourceRole:ResourceRole):void | Public method for adding newly created resourceRole to the catalog. |
| addUserCredential | (user:User, credential:Credential):void | Public method for adding a credential to a user. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| factory | EntitlementFactory | Private association with EntitlementFactory to create entitlement service objects. |
| resourceMap | Map<String, Resource> | Private association with Resource to keep track of all resource entities. |
| resourceRoleMap | Map<String, ResourceRole> | Private association with ResourceRolw to keep track of all ResourceRole relations. |
| entitlementMap | Map<String, Entitlement> | Private association with Entitlement to keep track of all Roles and Permissions. |
| userCredentialMap | Map<User, Credential> | Private association with Credential to keep track of registered Users and their associated Credentials. |


### Visitable
*interface*

Visitable is an interface for most EntitlementService entities, exposing a common method for traversing objects of various data structures.

#### Methods

| Method Name | Signature | Description |
|-------------|-----------|-------------|
| accept | (visitor:Visitor):void | Public method for accepting a visitor. |


### Resource
*implements Visitable*

Resource is a class that represents a physical and logical entity.

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| id | String | Resource ID |
| name | String | Resource name |
| description | String | Resource description |


### ResourceRole
*implements Visitable*

ResourceRole is a class that represents an association between Resource and Role.

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| id | String | ResourceRole ID |
| name | String | ResourceRole name |
| description | String | ResourceRole description |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| role | Entitlement | Private association with Role as one end of the relation. |
| resource | Resource | Private association with Resource as the other end of the relation. |


### User
*implements Visitable*

User is a class that represents a registered user of the system.

#### Methods

| Method Name | Signature | Description |
|-------------|-----------|-------------|
| addCredential | (type:String, key:String):void | Public method for adding a credential to the user. |
| addRole | (role:Entitlement):void | Public method for adding a role to the user. |
| addResourceRole | (resourceRole:ResourceRole):void | Public method for adding a ResourceRole to the user. |
| hasEntitlement | (resource:Resource):boolean | Public method for deciding if user has entitlement for the specified resource. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| id | String | User ID. |
| name | String | User name. |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| credential | Credential | Private association with user's credential. |
| role | Entitlement | Private association with user's role. |
| resourceRoleMap | Map<String, ResourceRole> | Private association with defined ResourceRoles. |


### Visitor

Visitor is a class that provides methods to visit EntitlementService entities, such as User, Role, Permission, Resource, and ResourceRole.

#### Methods

| Method Name | Signature | Description |
|-------------|-----------|-------------|
| visitUser | (user:User):void | Public method for traversing User instance. |
| visitCredential | (credential:Credential):void | public method for traversing Credential instance. |
| visitResource | (resource:Resource):void | Public method for traversing Resource instance. |
| visitResourceRole | (resourceRole:ResourceRole):void | Public method for traversing ResourceRole instance. |
| visitEntitlement | (entitlement:Entitlement):void | Public method for traversing Entitlement instance. |


### EntitlementFactory
*interface*

EntitlementFactory is an interface for potential implementations of factories for EntitlementService. It is structured after the Abstract Factory Pattern.

#### Methods

| Method Name | Signature | Description |
|-------------|-----------|-------------|
| createUser | (id:String, name:String):User | Create User instance. |
| createPermission | (id:String, name:String, description:String):Permission | Create Permission instance. |
| createRole | (id:String, name:String, description:String):Role | Create Role instance. |
| createResourceRole | (name:String, role:Entitlement, resource:Resource):ResourceRole | Create ResourceRole instance. |


### HouseEntitlementFactory
*implements EntitlementFactory*

HouseEntitlementFactory is a class that provides EntitlementFactory for the Housemate Service.

#### Methods

| Method Name | Signature | Description |
|-------------|-----------|-------------|
| createUser | (id:String, name:String):User | Create User instance. |
| createPermission | (id:String, name:String, description:String):Permission | Create Permission instance. |
| createRole | (id:String, name:String, description:String):Role | Create Role instance. |
| createResourceRole | (name:String, role:Entitlement, resource:Resource):ResourceRole | Create ResourceRole instance. |


### Credential

Credential is a class that represents User login credential information.

#### Methods

| Method Name | Signature | Description |
|-------------|-----------|-------------|
| hashPassword | (password:String):String ||

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| guid | String | globally-unique ID |
| lastUsed | String lastUsed | lastest timestamp |


### UserNamePassword
*extends Credential*

UserNamePassword is a credential class based on username and password.


### VoicePrint
*extends Credential*

VoicePrint is a credential class based on user's voiceprint.


### Entitlement

Entitlement is a class that defines authorizations that can be assigned to User. It has two subclasses: Permission and Role. It is structured after the Composite Pattern.

#### Methods

| Method Name | Signature | Description |
|-------------|-----------|-------------|
| add | (entitlement:Entitlement):void | Add a sub-component (i.e. composite) to this composite instance. |
| hasEntitlement | (id:String):boolean | Check if this Entitlement composite contains the specified Entitlement composite. |

#### Properties

| Property Name | Type   | Description   |
|---------------|--------|---------------|
| id | String | Entitlement ID |
| name | String | Entitlement name |
| description | String | Entitlement description |

#### Associations

| Association Name | Type   | Description   |
|------------------|--------|---------------|
| compositeMap | Map<String, Entitlement> | Private association with objects of the same class as itself to form a composite. |


### Permission
*extends Entitlement*

Permission is a class that represents the minimum granularity of Entitlement.

### Role
*extends Entitlement*

Role is a class that represents higher-level Entitlement by aggregating a set of Permissions.


### InvalidAccessTokenException
*extends Exception*

InvalidAccessTokenException defines the error that the User has invalid AccessToken.


### Implementation Details

The implementation has two parts:
1. Modify Housemate Model Service. The objective is to add mandatory token support to most of the methods that might also call Controller Service, and the authentication and authorization functions are provided by Entitlement Service API, whose implementation is hidden from the Housemate Model Service.
2. Implement generic Entitlement Service. The architectures and functions are modeled closely following the requirement, e.g. four design patterns are utilized including Singleton Pattern, Visitor Pattern, Composite Pattern and Abstract Factory Pattern. More details can be found from class diagram, sequence diagram, and descriptions in the class dictionary.

## Exception Handling

Exception is handled by `InvalidAccessTokenException` class. It will be thrown by EntitlementService API when the User has invalid AccessToken.

## Testing

A test driver class called TestDriver with a static main() method is implemented. The main() method accepts one parameter, an input command script file. The main method will call the API class's executeFile method, passing in the name of the provided script file. The TestDriver class is defined within package "housemate.src.housemate.test". Most importantly, the test script attempts to cover all cases and functions implemented in the Entitlement Class.

## Risks

* It requires extra focus to ensure the generic Entitlement Service and Housemate Model Service are correctly integrated. The reason is that in theory, the Entitlement Service is implemented with zero knowledge of the Housemate Model Service, so some modules can either be overlapping with each other or missing.
* The Entitlement Service provides basic functions of authentication and authorization, but they has no security features as the requirement might be suggesting. Securing the Entitlement Service is whole new important concern if the service needs to be deployed.
