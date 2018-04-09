package terrails.statskeeper.data.capabilities.tan;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.Constants;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.capabilities.tan.ITAN;
import terrails.terracore.capabilities.CapabilitySerializable;

/**
 * Capability for {@link ITAN}.
 *
 * @author Terrails
 */

public class CapabilityTAN {

    @CapabilityInject(ITAN.class)
    public static final Capability<ITAN> TAN_CAPABILITY;

    public static void register() {
        if (Loader.isModLoaded("ToughAsNails") || Loader.isModLoaded("toughasnails")) {
            CapabilityManager.INSTANCE.register(ITAN.class, new Capability.IStorage<ITAN>() {

                @Override
                public NBTBase writeNBT(Capability<ITAN> capability, ITAN instance, EnumFacing side) {
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setDouble("thirst", instance.getThirst());
                    return compound;
                }

                @Override
                public void readNBT(Capability<ITAN> capability, ITAN instance, EnumFacing side, NBTBase nbt) {
                    NBTTagCompound compound = (NBTTagCompound) nbt;
                    instance.setThirst(compound.getDouble("thirst"));
                }
            }, TAN::new);
        }
    }

    @Mod.EventBusSubscriber
    public static class Handler {
        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (Loader.isModLoaded("ToughAsNails") || Loader.isModLoaded("toughasnails")) {
                if (event.getObject() instanceof EntityPlayer) {
                    event.addCapability(new ResourceLocation(StatsKeeper.MOD_ID, "TAN"), new CapabilitySerializable<>(CapabilityTAN.TAN_CAPABILITY));
                }
            }
        }

        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            if (Loader.isModLoaded("ToughAsNails") || Loader.isModLoaded("toughasnails")) {
                ITAN tan = event.getEntityPlayer().getCapability(CapabilityTAN.TAN_CAPABILITY, null);
                ITAN oldTan = event.getOriginal().getCapability(CapabilityTAN.TAN_CAPABILITY, null);
                if (tan != null && oldTan != null) {
                    tan.setThirst(oldTan.getThirst());
                }
            }
        }
    }

    static {
        TAN_CAPABILITY = null;
    }
}
