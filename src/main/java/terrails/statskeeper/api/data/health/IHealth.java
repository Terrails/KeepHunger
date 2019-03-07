package terrails.statskeeper.api.data.health;

public interface IHealth {

    boolean isHealthEnabled();
    void setHealthEnabled(boolean val);

    boolean hasAdditionalHealth();

    void setAdditionalHealth(int health);
    int getAdditionalHealth();

    int getMaxHealth();
    void setMaxHealth(int health);

    int getMinHealth();
    void setMinHealth(int health);
    
    int getStartingHealth();
    void setStartingHealth(int health);
    
    int getCurrentThreshold();
    void setCurrentThreshold(int health);
}
