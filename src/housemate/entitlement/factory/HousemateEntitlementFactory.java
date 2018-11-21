package housemate.src.housemate.entitlement.factory;

import housemate.src.housemate.entitlement.*;
import housemate.src.housemate.entitlement.composite.*;

public class HousemateEntitlementFactory implements EntitlementFactory {
    
    public User createUser(String id, String name) {
        return new User(id, name);
    }

    public Permission createPermission(String id, String name, String description) {
        return new Permission(id, name, description);
    }

    public Resource createResource(String name, String description) {
        return new Resource(name, description);
    }

    public Role createRole(String id, String name, String description) {
        return new Role(id, name, description);
    }

    public ResourceRole creatResourceRole(String name, Entitlement role, Resource resource) {
        return new ResourceRole(name, role, resource);
    }
}