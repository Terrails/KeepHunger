package terrails.statskeeper.api.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class SKCapabilities {

    /** Use {@link HealthManager#getInstance(EntityPlayer)} to obtain it.
     * It makes sure to player is not null */
    @CapabilityInject(HealthManager.class)
    public static final Capability<HealthManager> HEALTH_CAPABILITY;

    static {
        HEALTH_CAPABILITY = null;
    }
}
