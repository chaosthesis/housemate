package housemate.src.housemate.entitlement.credential;

import java.util.Base64;

import housemate.src.housemate.entitlement.Visitor;
import housemate.src.housemate.entitlement.Visitable;

public abstract class Credential implements Visitable {
    private String guid;
    private String lastUsed;

    public static String hashPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    public String getType() {
        return null;
    }

    public String getKey() {
        return null;
    }

    public void print() {}

    public void accecpt(Visitor visitor) {
        visitor.visitCredential(this);
    }
}