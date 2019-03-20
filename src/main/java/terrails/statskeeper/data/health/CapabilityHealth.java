package terrails.statskeeper.data.health;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.capabilities.IHealth;
import terrails.statskeeper.api.capabilities.SKCapabilities;

public class CapabilityHealth {

    // TODO: Write your own way of syncing capabilities by saving the data into
    //  WorldSavedData and then copying it over on respawn List<UUID, IHealth>

    @SubscribeEvent
    public void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(StatsKeeper.MOD_ID, "health"), new CapabilitySerializable<>(SKCapabilities.HEALTH_CAPABILITY));
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new CapabilityHealth());
        CapabilityManager.INSTANCE.register(IHealth.class, new Capability.IStorage<IHealth>() {

            @Override
            public INBTBase writeNBT(Capability<IHealth> capability, IHealth instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.putBoolean("sk:is_enabled", instance.isHealthEnabled());
                compound.putInt("sk:additional_health", instance.getAdditionalHealth());
                compound.putInt("sk:max_health", instance.getMaxHealth());
                compound.putInt("sk:min_health", instance.getMinHealth());
                compound.putInt("sk:starting_health", instance.getStartingHealth());
                compound.putInt("sk:health_threshold", instance.getCurrentThreshold());
                return compound;
            }
            @Override
            public void readNBT(Capability<IHealth> capability, IHealth instance, EnumFacing side, INBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                if (compound.contains("sk:is_enabled")) {
                    instance.setHealthEnabled(compound.getBoolean("sk:is_enabled"));
                }

                if (compound.contains("sk:starting_health")) {
                    instance.setStartingHealth(compound.getInt("sk:starting_health"));
                }

                if (compound.contains("sk:additional_health")) {
                    instance.setAdditionalHealth(compound.getInt("sk:additional_health"));
                }

                if (compound.contains("sk:max_health")) {
                    instance.setMaxHealth(compound.getInt("sk:max_health"));
                }

                if (compound.contains("sk:min_health")) {
                    instance.setMinHealth(compound.getInt("sk:min_health"));
                }

                if (compound.contains("sk:health_threshold")) {
                    instance.setCurrentThreshold(compound.getInt("sk:health_threshold"));
                }
            }
        }, HealthHandler::new);
    }
}
