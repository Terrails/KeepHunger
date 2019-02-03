package terrails.statskeeper.data.health;

import terrails.statskeeper.api.capabilities.IHealth;

public class Health implements IHealth {

    private int additional_health = 0;
    private boolean is_health_enabled = false;
    private boolean is_min_start = false;
    private int max_health = 0;
    private int min_health = 0;

    @Override
    public boolean isHealthEnabled() {
        return this.is_health_enabled;
    }

    @Override
    public void setHealthEnabled(boolean val) {
        this.is_health_enabled = val;
    }

    @Override
    public boolean hasAdditionalHealth() {
        return this.additional_health > 0;
    }

    @Override
    public void setAdditionalHealth(int health) {
        this.additional_health = health;
    }

    @Override
    public int getAdditionalHealth() {
        return this.additional_health;
    }

    @Override
    public int getMaxHealth() {
        return this.max_health;
    }

    @Override
    public void setMaxHealth(int health) {
        this.max_health = health;
    }

    @Override
    public int getMinHealth() {
        return this.min_health;
    }

    @Override
    public void setMinHealth(int health) {
        this.min_health = health;
    }

    @Override
    public boolean isMinStart() {
        return this.is_min_start;
    }

    @Override
    public void setMinStart(boolean val) {
        this.is_min_start = val;
    }
}