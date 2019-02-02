package terrails.statskeeper.data.tan;

public class TAN implements ITAN {

    private double thirst;

    @Override
    public void setThirst(double thirst) {
        this.thirst = thirst;
    }

    @Override
    public double getThirst() {
        return thirst;
    }
}
