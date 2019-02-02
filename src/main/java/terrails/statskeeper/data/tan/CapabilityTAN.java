package terrails.statskeeper.data.tan;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.StatsKeeper;
import terrails.terracore.capabilities.CapabilitySerializable;

public class CapabilityTAN {

    // This doesn't go into SKCapabilities since its used for internal updating
    @CapabilityInject(ITAN.class)
    public static final Capability<ITAN> TAN_CAPABILITY;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new CapabilityTAN.Handler());
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

    public static class Handler {

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(StatsKeeper.MOD_ID, "TAN"), new CapabilitySerializable<>(CapabilityTAN.TAN_CAPABILITY));
            }
        }

        @SubscribeEvent
        public void playerClone(PlayerEvent.Clone event) {
            ITAN tan = event.getEntityPlayer().getCapability(CapabilityTAN.TAN_CAPABILITY, null);
            ITAN oldTan = event.getOriginal().getCapability(CapabilityTAN.TAN_CAPABILITY, null);
            if (tan != null && oldTan != null) {
                tan.setThirst(oldTan.getThirst());
            }
        }
    }

    static {
        TAN_CAPABILITY = null;
    }
}
