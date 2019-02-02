package terrails.statskeeper.api.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class SKCapabilities {

    @CapabilityInject(IHealth.class)
    public static final Capability<IHealth> HEALTH_CAPABILITY;

    static {
        HEALTH_CAPABILITY = null;
    }
}
