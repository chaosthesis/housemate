package housemate.src.housemate.entitlement;

public class Resource implements Visitable {
    private String id;
    private String name;
    private String description;

    public Resource(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = name + ":" + description;
    }

    public String getName() {
        return name;
    }

    public void print() {
        System.out.println("Info: {name: " + name + " descrption: " + description + " }");
    }

    public void accecpt(Visitor visitor) {
        visitor.visitResource(this);
    }
}