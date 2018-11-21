package housemate.src.housemate.entitlement;

import housemate.src.housemate.entitlement.*;
import housemate.src.housemate.entitlement.composite.*;
import housemate.src.housemate.entitlement.credential.*;

public class Visitor {

    /**
     * Public method for traversing User instance.
     * 
     * @param user user to be printed
     */
    public void visitUser(User user) {
        user.print();
    }

    /**
     * public method for traversing Credential instance.
     * 
     * @param credential credential to be printed
     */
    public void visitCredential(Credential credential) {
        credential.print();
    }

    /**
     * Public method for traversing Resource instance.
     * 
     * @param resource resource to be printed
     */
    public void visitResource(Resource resource) {
        resource.print();
    }

    /**
     * Public method for traversing ResourceRole instance.
     * 
     * @param resourceRole resourceRole to be printed
     */
    public void visitResourceRole(ResourceRole resourceRole) {
        resourceRole.print();
    }

    /**
     * Public method for traversing Entitlement instance.
     * 
     * @param entitlement entitlement to be printed
     */
    public void visitEntitlement(Entitlement entitlement) {
        entitlement.print();
    }

}