package terrails.statskeeper.data.capabilities.health;

import terrails.statskeeper.api.capabilities.health.IHealth;

public class Health implements IHealth {

    private double addedHealth;
    private boolean hasAddedHealth;
    private String lastItemName;

    @Override
    public void setHasAddedHealth(boolean maxHealth) {
        hasAddedHealth = maxHealth;
    }
    @Override
    public boolean getHasAddedHealth() {
        return hasAddedHealth;
    }

    @Override
    public void setAddedHealth(double health) {
        addedHealth = health;
    }
    @Override
    public double getAddedHealth() {
        return addedHealth;
    }

    @Override
    public void setLastItemName(String name) {
        lastItemName = name;
    }
    @Override
    public String getLastItemName() {
        if (lastItemName != null)
            return lastItemName;
        return "none";
    }
}