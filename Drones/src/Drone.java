/**
 * Created by gregory on 11/02/16.
 */
public class Drone extends Inventory {

    public Drone(int locX, int locY) {
        super(locX, locY);
    }

    public Drone(Inventory inventory) {
        super(inventory);
    }

    public int distance(Inventory goal){

        int xDist = goal.getLocX() - getLocX();
        int yDist = goal.getLocY() - getLocY();

        double dist = Math.sqrt(xDist*xDist+yDist*yDist);

        return (int) Math.ceil(dist);
    }
}
