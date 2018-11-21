package housemate.src.housemate.entitlement;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AccessToken {
    /**
     * Globally-unique token ID.
     */
    private String guid;

    /**
     * Timestamp of last access.
     */
    private String lastUsed;

    /**
     * Token state.
     */
    private String state;

    /**
     * Private association with the User.
     */
    private User user;

    public AccessToken(User user) {
        this.state = "active";
        this.user = user;
        setTimeStamp();
    }

    /**
     * Public method for updating token timestamp.
     */
    public void setTimeStamp() {
        lastUsed = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    /**
     * Public method for invalidating the token.
     */
    public void invalidate() {
        state = "expired";
    }

    public String getState() {
        return state;
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public User getUser() {
        return user;
    }
}