package housemate.src.housemate.entitlement.composite;

import java.util.HashMap;

public class Role extends Entitlement {
    public Role(String id, String name, String description) {
        super(id, name, description);
        this.compositeMap = new HashMap<String, Entitlement>();
    }

    @Override
    public void print() {
        super.print();

        System.out.print("Permissions: {");
        for (String en : compositeMap.keySet()) {
            System.out.print(en);
        }
        System.out.println("}");
    }
}