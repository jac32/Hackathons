package HashCode.Event;


import HashCode.*;

public class Deload {
    Drone drone;
    Warehouse warehouse;
    Product product;
    int quantity;


    public Deload(Drone drone, Warehouse warehouse, Product product, int quantity) {
        this.drone = drone;
        this.warehouse = warehouse;
        this.product = product;
        this.quantity = quantity;
    }
}