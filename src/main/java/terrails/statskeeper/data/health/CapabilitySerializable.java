package terrails.statskeeper.data.health;

import net.minecraft.nbt.INBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilitySerializable<C> implements ICapabilitySerializable<INBTBase> {

    private final Capability<C> capability;
    private final C instance;

    public CapabilitySerializable(Capability<C> capability) {
        this.capability = capability;
        this.instance = capability.getDefaultInstance();
    }

    public CapabilitySerializable(Capability<C> capability, C instance) {
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
