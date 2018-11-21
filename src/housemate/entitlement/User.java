package housemate.src.housemate.entitlement;

import java.util.HashMap;
import java.util.Map;

import housemate.src.housemate.entitlement.composite.*;
import housemate.src.housemate.entitlement.credential.*;

public class User implements Visitable {
    private String id;
    private String name;

    /**
     * Private association with user's credential.
     */
    private Credential credential;

    /**
     * Private association with user's role.
     */
    private Entitlement role;

    /**
     * Private association with defined ResourceRoles.
     */
    private Map<String, ResourceRole> resourceRoleMap;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.credential = createPassword("123456");
        resourceRoleMap = new HashMap<String, ResourceRole>();
    }

    /**
     * Public method for adding a credential to the user.
     * 
     * @param type Credential type; can be VoicePrint or UserNamePassword
     * @param key  Credential key
     */
    public void addCredential(String type, String key) {
        switch (type) {
        case "voice_print":
            credential = createVoicePrint(key);
            break;
        case "password":
            credential = createPassword(key);
            break;
        }
    }

    /**
     * Public method for adding a role to the user.
     * 
     * @param role Entitlements aggregated as Role
     */
    public void addRole(Entitlement role) {
        this.role = role;
    }

    /**
     * Public method for adding a ResourceRole to the user.
     * 
     * @param resourceRole ResourceRole object
     */
    public void addResourceRole(ResourceRole resourceRole) {
        resourceRoleMap.put(resourceRole.getName(), resourceRole);
    }

    /**
     * Public method for deciding if user has entitlement for the specified
     * resource.
     * 
     * @param resource Resource the user attempts to access
     * @return Boolean value of whether user has permission
     */
    public boolean hasEntitlement(Resource resource) {
        return true;
    }

    private UserNamePassword createPassword(String key) {
        return new UserNamePassword(id, Credential.hashPassword(key));
    }

    private VoicePrint createVoicePrint(String key) {
        return new VoicePrint(key);
    }

    public String getID() {
        return id;
    }

    public Credential getCredential() {
        return credential;
    }

    public void print() {
        System.out.println("Info: {id: " + id + " name: " + name + " }");

        System.out.print("Credential: ");
        credential.print();

        System.out.print("ResourceRole: {");
        for (String rr : resourceRoleMap.keySet()) {
            System.out.print(rr + ", ");
        }
        System.out.println("}");

        if (role != null) {
            System.out.println("Role: " + role.getName());
        }
    }

    public void accecpt(Visitor visitor) {
        visitor.visitUser(this);
    }
}