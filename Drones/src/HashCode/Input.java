import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by pschrempf on 11/02/16.
 */
public class Input {

    private static final String SEPARATOR = " ";
    private static final String FILE_NAME = "lol";
    private static BufferedReader in;

    public Input() throws FileNotFoundException {
        in = new BufferedReader(new FileReader(FILE_NAME));
    }

    public static void main(String[] args) throws IOException {
        readInput();
    }

    private static void readInput() throws IOException {
        String[] parameters = in.readLine().split(SEPARATOR);
        parseInt(parameters[0]); //set no. rows
        parseInt(parameters[1]); //set no. cols
        Inventory.drones = new Drone[parseInt(parameters[2])]; //set no. drones
        parseInt(parameters[3]); //set max turns
        Inventory.MAX_DRONE_WEIGHT = parseInt(parameters[4]); //set max drone load

        Product[] ps = new Product[parseInt(in.readLine())]; //set no. products
        String[] products = in.readLine().split(SEPARATOR);
        for (int i = 0; i < products.length; i++) {
            ps[i].setWeight(products[i]); //set weights of products
        }
        Inventory.products = ps;

        Warehouse[] ws = new Warehouse[parseInt(in.readLine())]; //set no. warehouses
        for (int i = 0; i < ws.length; i++) {
            String[] line = in.readLine().split(SEPARATOR);
            int y = parseInt(line[0]); //get row
            int x = parseInt(line[1]); //get col
            ws[i] = new Warehouse(x, y); //set x, y of warehouse

            line = in.readLine().split(SEPARATOR);
            for (int j = 0; j < line.length; j++) {
                ws[i].setInventory(j, parseInt(line[j])); //set warehouse product supply
            }
        }
        Inventory.warehouses = ws;

        Customer[] cs = new Customer[parseInt(in.readLine())]; //set no. customers
        for (int i = 0; i < cs.length; i++) {
            String[] line = in.readLine().split(SEPARATOR);
            int y = parseInt(line[0]); //get row
            int x = parseInt(line[1]); //get col
            cs[i] = new Customer(x, y); //set x, y of customer

            parseInt(in.readLine()); //set number of ordered products

            line = in.readLine().split(SEPARATOR);
            for (int j = 0; j < line.length; j++) {
                cs[i].setInventory(j, parseInt(line[j])); //set inventory products
            }
        }
        Inventory.customers = cs;

    }

    private int parseInt(String string) {
        return Integer.parseInt(string.trim());
    }
}
