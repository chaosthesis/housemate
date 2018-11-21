package housemate.src.housemate.model;

public class Appliance extends Device {
    /**
     * An integer denoting the energy consumption in Watts.
     */
    private int energyConsumption;

    /**
     * Returns current energy consumption of the Appliance.
     */
    @Override
    public int getEnergyConsumption() {
        return energyConsumption;
    }

    public Appliance(String name, String type, Room room, int energy_use) {
        super(name, type, room);
        this.energyConsumption = energy_use;
    }

}