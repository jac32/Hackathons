package Event;
public class Deliver extends Event{
    Drone drone;
    Customer customer;
    Product product;
    Int quantity;
}

public Deliver (Drone drone, Customer customer, Product product, Int quantity) {
    this.drone = drone;
    this.customer = customer;
    this.product = product;
    this.quantity = quantity;
}
