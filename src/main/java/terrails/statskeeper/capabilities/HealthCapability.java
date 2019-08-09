package terrails.statskeeper.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
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
import terrails.statskeeper.feature.HealthFeature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HealthCapability {

    private static final ResourceLocation NAME = new ResourceLocation(StatsKeeper.MOD_ID, "health");

    @SubscribeEvent
    public void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(NAME, new CapabilitySerializable<>(SKCapabilities.HEALTH_CAPABILITY));
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new HealthCapability());
        CapabilityManager.INSTANCE.register(HealthManager.class, new Capability.IStorage<HealthManager>() {

            @Override
            public INBT writeNBT(Capability<HealthManager> capability, HealthManager instance, Direction side) {
                CompoundNBT compound = new CompoundNBT();
                instance.serialize(compound);
                return compound;
            }
            @Override
            public void readNBT(Capability<HealthManager> capability, HealthManager instance, Direction side, INBT nbt) {
                CompoundNBT compound = (CompoundNBT) nbt;
                instance.deserialize(compound);
            }
        }, HealthFeature.Handler::new);

    }

    private static class CapabilitySerializable<C> implements ICapabilitySerializable<INBT> {

        private final Capability<C> capability;
        private final C instance;

        private CapabilitySerializable(Capability<C> capability) {
            this.capability = capability;
            this.instance = capability.getDefaultInstance();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
            return this.capability.orEmpty(capability, LazyOptional.of(() -> this.instance));
        }

        @Override
        public INBT serializeNBT() {
            return this.capability.writeNBT(this.instance, null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            this.capability.readNBT(this.instance, null, nbt);
        }
    }

}
