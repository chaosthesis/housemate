package housemate.src.housemate.entitlement;

public interface Visitable {
    /**
     * Public method for accepting a visitor.
     * 
     * @param visitor Visitor instance that can traverse other data structures
     */
    public void accecpt(Visitor visitor);
}