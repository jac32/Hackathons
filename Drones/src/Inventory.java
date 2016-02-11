import java.util.HashMap;

/**
 * Created by gregory on 11/02/16.
 */
public abstract class Inventory {

    private int locX, locY;

    static int MAX_DRONE_WEIGHT;

    private HashMap<Integer, Integer> inventory;

    public Inventory(int locX, int locY)
    {
        this.locX  = locX;
        this.locY = locY;

        inventory = new HashMap<>();
    }

    public Inventory(Inventory inventory){
        this(inventory.getLocX(), inventory.getLocY());
    }

    public void setInventory(int productId, int qty){

    }

    public int getLocX() {
        return locX;
    }

    public void setLocX(int locX) {
        this.locX = locX;
    }

    public int getLocY() {
        return locY;
    }

    public void setLocY(int locY) {
        this.locY = locY;
    }
}
