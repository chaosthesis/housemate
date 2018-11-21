package housemate.src.housemate.entitlement.composite;

import java.util.Map;

import housemate.src.housemate.entitlement.Visitor;
import housemate.src.housemate.entitlement.Visitable;

public abstract class Entitlement {
    /**
     * Entitlement ID
     */
    String id;

    /**
     * Entitlement name
     */
    String name;

    /**
     * Entitlement description
     */
    String description;

    /**
     * Private association with objects of the same class as itself to form a
     * composite.
     */
    Map<String, Entitlement> compositeMap;

    public Entitlement(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Add a sub-component (i.e. composite) to this composite instance.
     * 
     * @param entitlement Entitlement instance
     */
    public void add(Entitlement entitlement) {
        compositeMap.put(entitlement.getID(), entitlement);
    }

    /**
     * Check if this Entitlement composite contains the specified Entitlement
     * composite.
     * 
     * @param id Entitlement ID
     * @return Boolean value suggesting if an Entitlement exists
     */
    public boolean hasEntitlement(String id) {
        return compositeMap.containsKey(id);
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void print() {
        System.out.println("Info: {id: " + id + " descrption: " + description + " }");
    }

    public void accecpt(Visitor visitor) {
        visitor.visitEntitlement(this);
    }
}