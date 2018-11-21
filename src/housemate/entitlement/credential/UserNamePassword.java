package housemate.src.housemate.entitlement.credential;

public class UserNamePassword extends Credential {
    private String type;
    private String userName;
    private String passwordHash;

    public UserNamePassword(String userName, String passwordHash) {
        this.type = "password";
        this.userName = userName;
        this.passwordHash = passwordHash;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return passwordHash;
    }

    @Override
    public void print() {
        System.out.println("Password: " + passwordHash);
    }
}