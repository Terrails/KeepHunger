package terrails.statskeeper.api.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

public class SKCapabilities {

    @CapabilityInject(IHealth.class)
    public static final Capability<IHealth> HEALTH_CAPABILITY;

    public static IHealth getCapability(EntityPlayer player) {
        LazyOptional<IHealth> optional = player.getCapability(SKCapabilities.HEALTH_CAPABILITY);
        return optional.orElseThrow(() -> new RuntimeException("Health capability missing, can not continue!"));
    }

    static {
        HEALTH_CAPABILITY = null;
    }
}
