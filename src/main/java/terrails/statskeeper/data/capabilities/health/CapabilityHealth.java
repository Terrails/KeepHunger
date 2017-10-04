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
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.Constants;
import terrails.statskeeper.api.capabilities.health.IHealth;

/**
 * Capability for {@link IHealth}.
 *
 * @author Terrails
 */

public class CapabilityHealth implements ICapabilitySerializable<NBTBase> {

    @CapabilityInject(IHealth.class)
    public static final Capability<IHealth> HEALTH_CAPABILITY = null;
    public static final ResourceLocation CAPABILITY = new ResourceLocation(Constants.MOD_ID, "Health");

    private IHealth instance = HEALTH_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == HEALTH_CAPABILITY;
    }
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == HEALTH_CAPABILITY ? HEALTH_CAPABILITY.<T>cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return HEALTH_CAPABILITY.writeNBT(this.instance, null);
    }
    @Override
    public void deserializeNBT(NBTBase nbt) {
        HEALTH_CAPABILITY.readNBT(this.instance, null, nbt);
    }
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
        }, () -> new Health());
    }

    @Mod.EventBusSubscriber
    public static class Handler {
        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(CAPABILITY, new CapabilityHealth());
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
}
