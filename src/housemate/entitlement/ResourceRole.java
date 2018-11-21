package housemate.src.housemate.entitlement;

import housemate.src.housemate.entitlement.composite.*;

public class ResourceRole implements Visitable {
    private String id;
    private String name;
    private String description;

    /**
     * Private association with Role as one end of the relation.
     */
    private Entitlement role;

    /**
     * Private association with Resource as the other end of the relation.
     */
    private Resource resource;

    public ResourceRole(String name, Entitlement role, Resource resource) {
        this.name = name;
        this.role = role;
        this.resource = resource;
        this.description = name + "_" + description;
    }

    public String getName() {
        return name;
    }

    public void print() {
        System.out.println("Info: {name: " + name + " descrption: " + description + " }");

        System.out.println("Resource:");
        resource.print();

        System.out.println("Role:");
        role.print();
    }

    public void accecpt(Visitor visitor) {
        visitor.visitResourceRole(this);
    }
}