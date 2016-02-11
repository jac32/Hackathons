import java.util.*;
/**
 * Created by gregory on 11/02/16.
 */
public abstract class Inventory {

    private int locX, locY;

    static int MAX_DRONE_WEIGHT;

    private HashMap<Integer, Integer> inventory;

    static List<Warehouse> warehouses = new ArrayList<Warehouse>();
    static List<Customer> customers = new ArrayList<Customer>();
    static List<Drone> drones = new ArrayList<Drone>();

    public Inventory(int locX, int locY)
    {
        this.locX  = locX;
        this.locY = locY;

        inventory = new HashMap<>();
    }

    private static void removeProducts()
    {
        for(Warehouse warehouse : warehouses)
        {

        }

    }

    public Inventory(Inventory inventory){
        this(inventory.getLocX(), inventory.getLocY());
    }

    public void setInventory(int productId, int qty){
        inventory.put(productId, qty);
    }

    public int getInventory(int productId){
        return inventory.get(productId);
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
