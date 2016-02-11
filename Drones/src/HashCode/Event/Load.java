package HashCode.Event;

import HashCode.*;

public class Load extends Event {
    Drone drone;
    Warehouse warehouse;
    Product product;
    int quantity;


    public Load(Drone drone, Warehouse warehouse, Product product, int quantity) {
        this.drone = drone;
        this.warehouse = warehouse;
        this.product = product;
        this.quantity = quantity;
    }
}