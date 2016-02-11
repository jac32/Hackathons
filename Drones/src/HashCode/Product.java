package HashCode;

/**
 * Created by kf39 on 11/02/16.
 */
public class Product
{
    private int num;
    private int weight;

    public Product(int weight, int num){
        setNum(num);
        setWeight(weight);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
