/**
 * Created by gregory on 11/02/16.
 */
public class Warehouse extends Inventory {
    public Warehouse(int locX, int locY) {
        super(locX, locY);
    }

    public Warehouse(Inventory inventory) {
        super(inventory);
    }
}
