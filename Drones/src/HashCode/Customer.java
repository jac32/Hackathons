package HashCode;

/**
 * Created by gregory on 11/02/16.
 */
public class Customer extends Inventory {

    public Customer(int locX, int locY) {
        super(locX, locY);
    }

    public Customer(Inventory inventory) {
        super(inventory);
    }
}
