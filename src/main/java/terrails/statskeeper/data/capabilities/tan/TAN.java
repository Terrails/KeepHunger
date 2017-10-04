package terrails.statskeeper.data.capabilities.tan;

import terrails.statskeeper.api.capabilities.tan.ITAN;

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
