package housemate.src.housemate.model;

public class Occupant {
    /**
     * Private unique identifier for the Occupant. Occupant names are case
     * insensitive.
     */
    private String name;

    /**
     * Private identifier of the Room which indicates where is the Occupant.
     */
    private String location;

    /**
     * The Occupant type.
     */
    private String type;

    /**
     * Private String that indicates whether the Occupant is active or sleeping.
     */
    private String status;

    public Occupant(String occupantName, String type) {
        this.name = occupantName;
        this.type = type;
    }

    /**
     * Returns the name of the Occupant.
     * 
     * @return Occupant name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the identifier of the Room which the Occupant is currently in.
     * 
     * @return Room identifier where the Occupant is in.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the Occupant type, determining whether the Occupant is resident or
     * guest.
     * 
     * @return Occupant type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the Occupant status, which can be active or sleeping.
     * 
     * @return Occupant status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Public method for setting current location of the Occupant.
     * 
     * @param location The location of the Occupant.
     */
    public void setLocation(String location) {
        this.location = location;
    }
}