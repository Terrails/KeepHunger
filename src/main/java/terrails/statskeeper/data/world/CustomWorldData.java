package terrails.statskeeper.data.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.WorldSavedData;
import terrails.statskeeper.config.ConfigHandler;

public class CustomWorldData extends WorldSavedData {

    private static final String DATA_NAME = "StatsKeeper_MaxHealth";

    public CustomWorldData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        setOldMaxHealth(nbt.getDouble("oldMaxHealth"));
        setOldMinHealth(nbt.getDouble("oldMinHealth"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setDouble("oldMaxHealth", getOldMaxHealth());
        compound.setDouble("oldMinHealth", getOldMinHealth());
        compound.setDouble("maxHealth", getMaxHealth());
        compound.setDouble("minHealth", getMinHealth());
        compound.setDouble("removeHealth", getRemoveHealth());
        return compound;
    }

    public static CustomWorldData get(World world) {
        MapStorage storage = world.getMapStorage();
        CustomWorldData instance = (CustomWorldData) storage.getOrLoadData(CustomWorldData.class, DATA_NAME);

        if (instance == null) {
            instance = new CustomWorldData(DATA_NAME);
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    private double oldMaxHealthValue;
    private double oldMinHealthValue;

    public void setOldMinHealth(double health) {
        oldMinHealthValue = health;
        markDirty();
    }
    public double getOldMinHealth() {
        return oldMinHealthValue;
    }
    public void setOldMaxHealth(double health) {
        oldMaxHealthValue = health;
        markDirty();
    }
    public double getOldMaxHealth() {
        return oldMaxHealthValue;
    }
    public double getMaxHealth() {
        return ConfigHandler.maxHealth;
    }
    public double getMinHealth() {
        return ConfigHandler.minHealth;
    }
    public double getRemoveHealth() {
        return ConfigHandler.removedHealthOnDeath;
    }
}
