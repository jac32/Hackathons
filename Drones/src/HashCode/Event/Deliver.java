package HashCode.Event;


import HashCode.*;

public class Deliver extends Event{
    Drone drone;
    Customer customer;
    Product product;
    int quantity;
}

public Deliver (Drone drone, Customer customer, Product product, int quantity) {
    this.drone = drone;
    this.customer = customer;
    this.product = product;
    this.quantity = quantity;
}
