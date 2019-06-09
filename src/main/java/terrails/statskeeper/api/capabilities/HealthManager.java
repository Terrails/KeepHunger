package terrails.statskeeper.api.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;

public interface HealthManager {

    static LazyOptional<HealthManager> getInstance(PlayerEntity player) {
        LazyOptional<HealthManager> optional = player.getCapability(SKCapabilities.HEALTH_CAPABILITY);
        optional.ifPresent(health -> health.with((ServerPlayerEntity) player));
        return optional;
    }

    /**
     * Used internally, make sure to use the {@link #getInstance(PlayerEntity)}
     * method since it sets the player each time its accessed
     * @param player which to set the manager to
     * @return the manager with the player
     */
    HealthManager with(ServerPlayerEntity player);

    /**
     * @return the current amount of health the player has.
     * This might not be accurate if some other mod is
     * messing with the health system.
     */
    int getHealth();

    /**
     * @return the current health threshold the player achieved.
     * If value < 0, the health is not going to be decreased on death
     * If value = 0, has not been set
     * If value > 0, the health is not going to go below this value
     */
    int getThreshold();

    /**
     * @return is the health at the highest value possible
     */
    boolean isHighest();
    /**
     * @return is the health at the lowest value possible
     */
    boolean isLowest();

    /**
     * @return is the health possible to be decreased
     */
    boolean isHealthRemovable();

    /**
     * Changes the players health to the specified value
     * @param amount the amount of health
     * @return success
     */
    boolean setHealth(int amount);

    /**
     * Increases/Decreases players health
     * @param amount the amount of health to increase (+) or decrease (-)
     * @return success
     */
    boolean addHealth(int amount);

    /**
     * Increases/Decreases players health
     * @param amount the amount of health to increase (+) or decrease (-)
     * @param threshold should the method care about the threshold when decreasing
     * @return success
     */
    boolean addHealth(int amount, boolean threshold);

    /**
     * Runs the default update method which checks if the current
     * health values are in range, checks the threshold and saves
     * data to the player file if it has been changed.
     */
    void update();

    /**
     * Resets the whole manager to default values
     */
    void reset();

    /**
     * Serializes the data to the given NBTTagCompound
     * @param compound the tag to which the data will be saved
     */
    void serialize(CompoundNBT compound);

    /**
     * Reads the data from the given NBTTagCompound
     * @param compound the tag from which the data will be read
     */
    void deserialize(CompoundNBT compound);
}
