package terrails.statskeeper.data.capabilities.health;

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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.Constants;
import terrails.statskeeper.api.capabilities.health.IHealth;
import terrails.terracore.capabilities.CapabilitySerializable;

/**
 * Capability for {@link IHealth}.
 *
 * @author Terrails
 */

public class CapabilityHealth {

    @CapabilityInject(IHealth.class)
    public static final Capability<IHealth> HEALTH_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(IHealth.class, new Capability.IStorage<IHealth>() {

            @Override
            public NBTBase writeNBT(Capability<IHealth> capability, IHealth instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setBoolean("hasMaxHealth", instance.getHasAddedHealth());
                compound.setDouble("addedHealth", instance.getAddedHealth());
                compound.setString("lastHealthItem", instance.getLastItemName());
                return compound;
            }
            @Override
            public void readNBT(Capability<IHealth> capability, IHealth instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound)nbt;
                instance.setHasAddedHealth(compound.getBoolean("hasMaxHealth"));
                instance.setAddedHealth(compound.getDouble("addedHealth"));
                instance.setLastItemName(compound.getString("lastHealthItem"));
            }
        }, Health::new);
    }

    @Mod.EventBusSubscriber
    public static class Handler {
        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(Constants.MOD_ID, "Health"), new CapabilitySerializable<>(CapabilityHealth.HEALTH_CAPABILITY));
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void playerClone(PlayerEvent.Clone event) {
            IHealth health = event.getEntityPlayer().getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
            IHealth oldHealth = event.getOriginal().getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
            if (health != null && oldHealth != null) {
                health.setHasAddedHealth(oldHealth.getHasAddedHealth());
                health.setAddedHealth(oldHealth.getAddedHealth());
                health.setLastItemName(oldHealth.getLastItemName());
            }
        }
    }

    static {
        HEALTH_CAPABILITY = null;
    }
}
