package terrails.statskeeper.data.health;

import terrails.statskeeper.api.capabilities.IHealth;

public class HealthHandler implements IHealth {

    private int additional_health = 0;
    private boolean is_health_enabled = false;
    private int health_threshold = 0;
    private int starting_health = 0;
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
    public int getStartingHealth() {
        return this.starting_health;
    }

    @Override
    public void setStartingHealth(int health) {
        this.starting_health = health;
    }

    @Override
    public int getCurrentThreshold() {
        return this.health_threshold;
    }

    @Override
    public void setCurrentThreshold(int health) {
        this.health_threshold = health;
    }
}