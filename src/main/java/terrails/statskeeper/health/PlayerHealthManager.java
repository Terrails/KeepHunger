package terrails.statskeeper.health;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.data.HealthManager;
import terrails.statskeeper.config.SKHealthConfig;

public class PlayerHealthManager implements HealthManager {

    /** PlayerEntity that is bound to this manager */
    private final ServerPlayerEntity playerEntity;
    /** Base value of the MAX_HEALTH attribute */
    private final int baseValue;

    /** The amount of health the player has */
    private int amount = 0;
    /** Current health "threshold", can be a negative
     * number if the health is not being removed */
    private int threshold = 0;

    /** The amount of health the player will have in the beginning */
    private int start = 0;
    /** The highest amount of health a player can have */
    private int max = 0;
    /** The lowest amount of health a player can have */
    private int min = 0;

    public PlayerHealthManager(ServerPlayerEntity player) {
        this.playerEntity = player;
        this.baseValue = (int) HealthHelper.getAttribute(player).getBaseValue();
    }

    @Override
    public void update() {
        if (!this.playerEntity.isAlive() || !SKHealthConfig.enabled) {
            return;
        }

        if (HealthHelper.hasConfigChanged(this.min, this.max, this.start)) {
            this.reset();
            return;
        }

        int prevThreshold = this.threshold;
        int prevHealthAmount = this.amount;

        if (!HealthHelper.hasModifier(this.playerEntity)) {
            this.amount = this.start;
        }

        Integer integer = SKHealthConfig.health_thresholds.floor(this.amount);
        this.threshold = integer != null ? Math.abs(integer) <= this.amount ? Math.abs(integer) : integer : 0;

        if (this.start == this.max && this.min <= 0) {
            this.amount = this.max;
        } else {
            int min = Math.max(this.min, this.threshold);
            this.amount = MathHelper.clamp(this.amount, min, this.max);
        }

        if (prevHealthAmount != this.amount) {
            this.setHealth(this.amount);
        }

        if (prevThreshold != this.threshold && prevThreshold != 0 && this.threshold > 0) {
            HealthHelper.playerMessage(this.playerEntity, "health.statskeeper.threshold", Math.abs(this.threshold));
        }

//        if (prevHealthAmount != this.amount || prevThreshold != this.threshold) {
//            ServerWorld serverWorld = (ServerWorld) this.playerEntity.getEntityWorld();
//            PlayerManagerAccessor playerManager = (PlayerManagerAccessor) serverWorld.getServer().getPlayerManager();
//            playerManager.saveDataForPlayer(this.playerEntity);
//        }
    }

    @Override
    public boolean setHealth(int amount) {
        amount = MathHelper.clamp(amount, this.min, this.max);
        HealthHelper.addModifier(this.playerEntity, amount);

        if (this.amount != amount) {
            this.amount = amount;
            this.playerEntity.setHealth(amount);
            this.update();
            return true;
        }

        return false;
    }

    @Override
    public boolean addHealth(int amount, boolean threshold) {
        int prevThreshold = this.threshold;
        int prevHealth = this.amount;
        amount = MathHelper.clamp(this.amount + amount, this.min, this.max);

        int min = Math.max(this.min, threshold ? this.threshold : 0);
        if (amount < min || amount > this.max) {
            return false;
        }

        boolean ret = this.setHealth(amount);

        // In case that the threshold value changed, don't overwrite the message it sent
        if (ret && prevThreshold == this.threshold) {
            String key = (this.amount - prevHealth) > 0 ? "health.statskeeper.item_add" : "health.statskeeper.item_lose";
            HealthHelper.playerMessage(this.playerEntity, key, Math.abs((this.amount - prevHealth)));
        }

        return ret;
    }

    @Override
    public boolean addHealth(int amount) {
        return this.addHealth(amount, true);
    }

    @Override
    public void reset() {
        this.threshold = 0;
        this.start = SKHealthConfig.starting_health;
        this.max = SKHealthConfig.max_health;
        this.min = SKHealthConfig.min_health;
        this.amount = this.start;
        this.setHealth(this.start);
        this.playerEntity.setHealth(playerEntity.getHealthMaximum());
    }

    @Override
    public int getHealth() {
        return this.amount;
    }
    @Override
    public int getThreshold() {
        return this.threshold;
    }
    @Override
    public boolean isHighest() {
        return this.amount == this.max;
    }
    @Override
    public boolean isLowest() {
        return this.amount == this.min;
    }
    @Override
    public boolean isHealthRemovable() {
        int min = Math.max(this.min, Math.abs(this.threshold));
        return this.min > 0 && this.amount > min;
    }

    @Override
    public void serialize(CompoundTag compound) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("sk:additional_health", this.amount - this.baseValue);
        tag.putInt("sk:max_health", this.max);
        tag.putInt("sk:min_health", this.min);
        tag.putInt("sk:starting_health", this.start);
        tag.putInt("sk:health_threshold", this.threshold);
        compound.put(StatsKeeper.MOD_ID, tag);
    }
    @Override
    public void deserialize(CompoundTag compound) {
        CompoundTag tag = compound.containsKey(StatsKeeper.MOD_ID) ? compound.getCompound(StatsKeeper.MOD_ID) : compound;

        if (tag.containsKey("sk:starting_health")) {
            this.start = tag.getInt("sk:starting_health");
        }

        if (tag.containsKey("sk:additional_health")) {
            this.amount = tag.getInt("sk:additional_health") + this.baseValue;
        }

        if (tag.containsKey("sk:max_health")) {
            this.max = tag.getInt("sk:max_health");
        }

        if (tag.containsKey("sk:min_health")) {
            this.min = tag.getInt("sk:min_health");
        }

        if (tag.containsKey("sk:health_threshold")) {
            this.threshold = tag.getInt("sk:health_threshold");
        }

        // Compatibility for older versions
        if (tag.containsKey("sk:is_min_start")) {
            boolean min_start = tag.getBoolean("sk:is_min_start");
            this.start = min_start ? this.min : this.max;
        }
    }
}
