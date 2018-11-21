package housemate.src.housemate.entitlement.factory;

import housemate.src.housemate.entitlement.*;
import housemate.src.housemate.entitlement.composite.*;

public interface EntitlementFactory {

    User createUser(String id, String name);
    
    Permission createPermission(String id, String name, String description);

    Resource createResource(String name, String description);

    Role createRole(String id, String name, String description);
    
    ResourceRole creatResourceRole(String name, Entitlement role, Resource resource);
}