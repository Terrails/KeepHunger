package terrails.statskeeper.api.capabilities.tan;

/**
 * A capabilities to provide the TAN stats of a player.
 *
 * @author Terrails
 */

public interface ITAN {

    /**
     * @param thirst the thirst of player
     */
    void setThirst(double thirst);

    /**
     * @return the thirst of player
     */
    double getThirst();
}
