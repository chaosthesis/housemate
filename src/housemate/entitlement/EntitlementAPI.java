package housemate.src.housemate.entitlement;

import java.util.HashMap;
import java.util.Map;

import housemate.src.housemate.entitlement.composite.*;
import housemate.src.housemate.entitlement.credential.*;
import housemate.src.housemate.entitlement.factory.*;

public class EntitlementAPI {
    private static EntitlementAPI instance;

    /**
     * Private association with EntitlementFactory to create entitlement service
     * objects.
     */
    private EntitlementFactory factory;

    /**
     * Private association with Resource to keep track of all resource entities.
     */
    private Map<String, Resource> resourceMap;

    /**
     * Private association with ResourceRolw to keep track of all ResourceRole
     * relations.
     */
    private Map<String, ResourceRole> resourceRoleMap;

    /**
     * Private association with Entitlement to keep track of all Roles and
     * Permissions.
     */
    private Map<String, Entitlement> entitlementMap;

    /**
     * Private association with Credential to keep track of registered Users and
     * their associated Credentials.
     */
    private Map<User, Credential> userCredentialMap;

    private EntitlementAPI() {
        this.factory = new HousemateEntitlementFactory();
        this.resourceMap = new HashMap<>();
        this.resourceRoleMap = new HashMap<>();
        this.entitlementMap = new HashMap<>();
        this.userCredentialMap = new HashMap<User, Credential>();
        createAdminUser();
    }

    /**
     * Public method for getting the EntitlementAPI singleton instance.
     * 
     * @return EntitlementAPI instance
     */
    public static EntitlementAPI getInstance() {
        if (instance == null) {
            instance = new EntitlementAPI();
        }
        return instance;
    }

    /**
     * Public method for checking if token has access.
     * 
     * @param token User passed token
     * @throws InvalidAccessTokenException
     */
    public void checkAccess(AccessToken token) throws InvalidAccessTokenException {
        if (token == null || !token.getState().equals("active")) {
            throw new InvalidAccessTokenException("token expired");
        }
    }

    /**
     * Public method for logging in users with correct credential.
     * 
     * @param id   User ID string
     * @param type Credential type string
     * @param key  Credential key string
     * @return Active token or NullPointer
     */
    public AccessToken login(String id, String type, String key) {
        User user = getUser(id);
        String realKey = user.getCredential().getKey();
        String passedKey = type.equals("password") ? Credential.hashPassword(key) : key;
        if (passedKey.equals(realKey)) {
            AccessToken token = new AccessToken(user);
            System.out.println(user.getID() + " login at: " + token.getLastUsed());
            return token;
        } else {
            System.err.println("Password/VoicePrint doesn't match");
            return null;
        }
    }

    /**
     * Public method for user to logout.
     * 
     * @param token User's active token.
     */
    public void logout(AccessToken token) {
        token.setTimeStamp();
        token.invalidate();
        System.out.println(token.getUser().getID() + " logout at: " + token.getLastUsed());
    }

    /**
     * Public method for adding newly created user to the catalog.
     * 
     * @param user User instance
     */
    public void saveUser(User user) {
        userCredentialMap.put(user, user.getCredential());
    }

    /**
     * Public method for adding newly created entitlement to the catalog.
     * 
     * @param entitlement Entitlement instance
     */
    public void saveEntitlement(Entitlement entitlement) {
        entitlementMap.put(entitlement.getID(), entitlement);
    }

    /**
     * Public method for adding newly created resource to the catalog.
     * 
     * @param resource Resource instance
     */
    public void saveResource(Resource resource) {
        resourceMap.put(resource.getName(), resource);
    }

    /**
     * Public method for adding newly created resourceRole to the catalog.
     * 
     * @param resource ResourceRole instance
     */
    public void saveResourceRole(ResourceRole resourceRole) {
        resourceRoleMap.put(resourceRole.getName(), resourceRole);
    }

    /**
     * Public method for adding a credential to a user.
     * 
     * @param user       User instance
     * @param credential Credential instance
     */
    public void addUserCredential(User user, Credential credential) {
        userCredentialMap.put(user, credential);
    }

    public EntitlementFactory getFactory() {
        return factory;
    }

    public User getUser(String id) {
        for (User user : userCredentialMap.keySet()) {
            if (id.equals(user.getID())) {
                return user;
            }
        }
        return null;
    }

    public Entitlement getEntitlement(String id) {
        return entitlementMap.get(id);
    }

    public Resource getResource(String name) {
        return resourceMap.get(name);
    }

    public ResourceRole getResourceRole(String name) {
        return resourceRoleMap.get(name);
    }

    private void createAdminUser() {
        User adminUser = factory.createUser("admin", "Administrator");
        adminUser.addCredential("password", "admin");
        saveUser(adminUser);
    }
}