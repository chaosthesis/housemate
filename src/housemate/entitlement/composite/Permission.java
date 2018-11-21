package housemate.src.housemate.entitlement.composite;

public class Permission extends Entitlement {
    
    public Permission(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void add(Entitlement entitlement) {}
}