package terrails.statskeeper.api.data;

public interface IPlayerHealth {

    boolean isSKHealthEnabled();
    void setSKHealthEnabled(boolean val);

    boolean hasSKAdditionalHealth();

    void setSKAdditionalHealth(int health);
    int getSKAdditionalHealth();

    int getSKMaxHealth();
    void setSKMaxHealth(int health);

    int getSKMinHealth();
    void setSKMinHealth(int health);

    boolean isSKMinStart();
    void setSKMinStart(boolean val);
}
