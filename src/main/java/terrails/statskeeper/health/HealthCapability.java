package terrails.statskeeper.health;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.capabilities.HealthManager;
import terrails.statskeeper.api.capabilities.SKCapabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HealthCapability {

    // TODO: Write your own way of syncing capabilities by saving the data into
    //  WorldSavedData and then copying it over in tick event List<UUID, IHealth>
    //  Or wait for Forge to fix the bug...

    public static final ResourceLocation NAME = new ResourceLocation(StatsKeeper.MOD_ID, "health");

    @SubscribeEvent
    public void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(NAME, new CapabilitySerializable<>(SKCapabilities.HEALTH_CAPABILITY));
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new HealthCapability());
        CapabilityManager.INSTANCE.register(HealthManager.class, new Capability.IStorage<HealthManager>() {

            @Override
            public INBTBase writeNBT(Capability<HealthManager> capability, HealthManager instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                instance.serialize(compound);
                return compound;
            }
            @Override
            public void readNBT(Capability<HealthManager> capability, HealthManager instance, EnumFacing side, INBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.deserialize(compound);
            }
        }, PlayerHealthManager::new);
    }

    private class CapabilitySerializable<C> implements ICapabilitySerializable<INBTBase> {

        private final Capability<C> capability;
        private final C instance;

        private CapabilitySerializable(Capability<C> capability) {
            this.capability = capability;
            this.instance = capability.getDefaultInstance();
        }

        private CapabilitySerializable(Capability<C> capability, C instance) {
            this.capability = capability;
            this.instance = instance;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
            return this.capability.orEmpty(capability, LazyOptional.of(() -> this.instance));
        }

        @Override
        public INBTBase serializeNBT() {
            return this.capability.writeNBT(this.instance, null);
        }

        @Override
        public void deserializeNBT(INBTBase nbt) {
            this.capability.readNBT(this.instance, null, nbt);
        }
    }

}
