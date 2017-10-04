package terrails.statskeeper.api.capabilities.health;

/**
 * A capabilities to provide the custom health of a player.
 *
 * @author Terrails
 */

public interface IHealth {

    /**
     * @param hasAddedHealth the boolean if player got his added health
     */
    void setHasAddedHealth(boolean hasAddedHealth);

    /**
     * @return the boolean if player got his added health
     */
    boolean getHasAddedHealth();

    /**
     * @param health that is added to players base health
     */
    void setAddedHealth(double health);

    /**
     * @return added health to players base health
     */
    double getAddedHealth();

    /**
     * @param name the last health item name
     */
    void setLastItemName(String name);

    /**
     * @return the last health item name
     */
    String getLastItemName();
}
