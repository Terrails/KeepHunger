package terrails.statskeeper.health;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import terrails.statskeeper.api.capabilities.HealthManager;
import terrails.statskeeper.config.SKHealthConfig;

public class PlayerHealthManager implements HealthManager {

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

    @Override
    public void update(ServerPlayerEntity playerEntity) {
        if (!playerEntity.isAlive() || playerEntity.isCreative() || !SKHealthConfig.ENABLED) {
            return;
        }

        if (HealthHelper.hasConfigChanged(this.min, this.max, this.start)) {
            this.reset(playerEntity);
            return;
        }

        int prevThreshold = this.threshold;
        int prevHealthAmount = this.amount;

        if (!HealthHelper.hasModifier(playerEntity)) {
            this.amount = this.start;
        }

        Integer integer = SKHealthConfig.HEALTH_THRESHOLDS.floor(this.amount);
        this.threshold = integer != null ? Math.abs(integer) <= this.amount ? Math.abs(integer) : integer : 0;

        if (this.start == this.max && this.min <= 0) {
            this.amount = this.max;
        } else {
            int min = Math.max(this.min, this.threshold);
            this.amount = MathHelper.clamp(this.amount, min, this.max);
        }

        if (prevHealthAmount != this.amount) {
            this.setHealth(playerEntity, this.amount);
        }

        if (prevThreshold != this.threshold && prevThreshold != 0 && this.threshold > 0) {
            HealthHelper.playerMessage(playerEntity, "health.statskeeper.threshold", Math.abs(this.threshold));
        }
    }

    @Override
    public boolean setHealth(ServerPlayerEntity playerEntity, int amount) {
        if (!playerEntity.isAlive() || playerEntity.isCreative() || !SKHealthConfig.ENABLED) {
            return false;
        }

        amount = MathHelper.clamp(amount, this.min, this.max);
        HealthHelper.addModifier(playerEntity, amount);

        if (this.amount != amount) {
            this.amount = amount;
            playerEntity.setHealth(amount);
            this.update(playerEntity);
            return true;
        }

        return false;
    }

    @Override
    public boolean addHealth(ServerPlayerEntity playerEntity, int amount, boolean threshold) {
        int prevThreshold = this.threshold;
        int prevHealth = this.amount;
        amount = MathHelper.clamp(this.amount + amount, this.min, this.max);

        int min = Math.max(this.min, threshold ? this.threshold : 0);
        if (amount < min || amount > this.max) {
            return false;
        }

        boolean ret = this.setHealth(playerEntity, amount);

        // In case that the threshold value changed, don't overwrite the message it sent
        if (ret && prevThreshold == this.threshold) {
            String key = (this.amount - prevHealth) > 0 ? "health.statskeeper.item_add" : "health.statskeeper.item_lose";
            HealthHelper.playerMessage(playerEntity, key, Math.abs((this.amount - prevHealth)));
        }

        return ret;
    }

    @Override
    public boolean addHealth(ServerPlayerEntity playerEntity, int amount) {
        return this.addHealth(playerEntity, amount, true);
    }

    @Override
    public void reset(ServerPlayerEntity playerEntity) {
        this.threshold = 0;
        this.start = SKHealthConfig.STARTING_HEALTH;
        this.max = SKHealthConfig.MAX_HEALTH;
        this.min = SKHealthConfig.MIN_HEALTH;
        this.amount = this.start;
        this.setHealth(playerEntity, this.start);
        playerEntity.setHealth(playerEntity.getMaxHealth());
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
    public void serialize(CompoundNBT tag) {
        tag.putInt("sk:additional_health", this.amount - 20);
        tag.putInt("sk:max_health", this.max);
        tag.putInt("sk:min_health", this.min);
        tag.putInt("sk:starting_health", this.start);
        tag.putInt("sk:health_threshold", this.threshold);
    }
    @Override
    public void deserialize(CompoundNBT tag) {
        if (tag.contains("sk:starting_health")) {
            this.start = tag.getInt("sk:starting_health");
        }

        if (tag.contains("sk:additional_health")) {
            this.amount = tag.getInt("sk:additional_health") + 20;
        }

        if (tag.contains("sk:max_health")) {
            this.max = tag.getInt("sk:max_health");
        }

        if (tag.contains("sk:min_health")) {
            this.min = tag.getInt("sk:min_health");
        }

        if (tag.contains("sk:health_threshold")) {
            this.threshold = tag.getInt("sk:health_threshold");
        }
    }
}