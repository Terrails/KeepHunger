package terrails.statskeeper.data.health;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.capabilities.IHealth;
import terrails.statskeeper.api.capabilities.SKCapabilities;
import terrails.terracore.capabilities.CapabilitySerializable;

public class CapabilityHealth {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new CapabilityHealth.Handler());
        CapabilityManager.INSTANCE.register(IHealth.class, new Capability.IStorage<IHealth>() {

            @Override
            public NBTBase writeNBT(Capability<IHealth> capability, IHealth instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setBoolean("sk:is_enabled", instance.isHealthEnabled());
                compound.setInteger("sk:additional_health", instance.getAdditionalHealth());
                compound.setInteger("sk:max_health", instance.getMaxHealth());
                compound.setInteger("sk:min_health", instance.getMinHealth());
                return compound;
            }
            @Override
            public void readNBT(Capability<IHealth> capability, IHealth instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound)nbt;
                if (compound.hasKey("sk:is_enabled")) {
                    instance.setHealthEnabled(compound.getBoolean("sk:is_enabled"));
                }

                if (compound.hasKey("sk:additional_health")) {
                    instance.setAdditionalHealth(compound.getInteger("sk:additional_health"));
                }

                if (compound.hasKey("sk:max_health")) {
                    instance.setMaxHealth(compound.getInteger("sk:max_health"));
                }

                if (compound.hasKey("sk:min_health")) {
                    instance.setMinHealth(compound.getInteger("sk:min_health"));
                }
            }
        }, Health::new);
    }

    public static class Handler {

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(StatsKeeper.MOD_ID, "Health"), new CapabilitySerializable<>(SKCapabilities.HEALTH_CAPABILITY));
            }
        }

    }
}
