package terrails.statskeeper.api.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class SKCapabilities {

    @CapabilityInject(IHealth.class)
    public static final Capability<IHealth> HEALTH_CAPABILITY;

    public static IHealth getCapability(EntityPlayer player) {
        return player.getCapability(SKCapabilities.HEALTH_CAPABILITY, null);
    }

    static {
        HEALTH_CAPABILITY = null;
    }
}
