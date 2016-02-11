package Event;

public class Load extends Event {
    Drone drone;
    Warehouse warehouse;
    Int product;
    Int quantity;
}


public Load (Drone drone, Customer customer, Product product, Int quantity) {
    this.drone = drone;
    this.customer = customer;
    this.product = product;
    this.quantity = quantity;
}
