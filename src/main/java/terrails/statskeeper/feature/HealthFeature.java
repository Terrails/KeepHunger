package terrails.statskeeper.feature;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.registries.ForgeRegistries;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.capabilities.HealthManager;
import terrails.statskeeper.capabilities.HealthCapability;
import terrails.statskeeper.helper.HealthHelper;

import java.util.*;

public class HealthFeature extends Feature {

    public static final HealthFeature INSTANCE = new HealthFeature();

    private ForgeConfigSpec.BooleanValue enabled;
    private ForgeConfigSpec.BooleanValue message;
    private ForgeConfigSpec.ConfigValue<List<? extends String>> on_change_reset;

    private ForgeConfigSpec.IntValue max_health;
    private ForgeConfigSpec.IntValue min_health;
    private ForgeConfigSpec.IntValue health_decrease;
    private int starting_health;

    private Map<ResourceLocation, Tuple<Integer, Boolean>> items;
    private NavigableSet<Integer> thresholds;

    public class Handler implements HealthManager {

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
            if (!playerEntity.isAlive() || playerEntity.isCreative() || !enabled.get()) {
                return;
            }

            if (hasConfigChanged()) {
                this.reset(playerEntity);
                return;
            }

            int prevThreshold = this.threshold;
            int prevHealthAmount = this.amount;

            if (!HealthHelper.hasModifier(playerEntity)) {
                this.amount = this.start;
            }

            Integer integer = thresholds.floor(this.amount);
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
            if (!playerEntity.isAlive() || playerEntity.isCreative() || !enabled.get()) {
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
            this.start = starting_health;
            this.max = max_health.get();
            this.min = min_health.get();
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

        private boolean hasConfigChanged() {
            for (String string : on_change_reset.get()) {
                string = string.toUpperCase();

                if (string.equals("MIN_HEALTH") && min_health.get() != this.min) {
                    return true;
                }

                if (string.equals("MAX_HEALTH") && max_health.get() != this.max) {
                    return true;
                }

                if (string.equals("STARTING_HEALTH") && starting_health != this.start) {
                    return true;
                }
            }
            return false;
        }
    }

    @SubscribeEvent
    public void join(PlayerLoggedInEvent event) {
        if (!this.enabled.get()) {
            HealthHelper.removeModifier(event.getPlayer());
            return;
        }

        HealthManager.getInstance((ServerPlayerEntity) event.getPlayer(), HealthManager::update);
    }

    @SubscribeEvent
    public void clone(PlayerEvent.Clone event) {
        if (!this.enabled.get()) {
            return;
        }

        HealthManager.getInstance((ServerPlayerEntity) event.getEntityPlayer(), (manager, player) -> {

            CompoundNBT compound = event.getOriginal().writeWithoutTypeId(new CompoundNBT());
            if (compound.contains("ForgeCaps", Constants.NBT.TAG_COMPOUND)) {
                manager.deserialize(compound.getCompound("ForgeCaps").getCompound(HealthCapability.NAME.toString()));
                manager.setHealth(player, manager.getHealth());
            }

            if (this.starting_health == this.max_health.get() && this.min_health.get() <= 0) {
                manager.update(player);
                return;
            }

            int decrease = this.health_decrease.get();
            if (event.isWasDeath() && decrease > 0 && manager.isHealthRemovable()) {
                int prevHealth = manager.getHealth();
                manager.addHealth(player, -decrease);
                double removedAmount = manager.getHealth() - prevHealth;
                if (this.message.get() && removedAmount > 0) {
                    HealthHelper.playerMessage(event.getEntityPlayer(), "health.statskeeper.death_remove", removedAmount);
                }
            }
        });
    }

    @SubscribeEvent
    public void itemInteract(PlayerInteractEvent.RightClickItem event) {
        if (!this.enabled.get() || event.getWorld().isRemote())
            return;

        HealthManager.getInstance((ServerPlayerEntity) event.getEntityPlayer(), (manager, player) -> {

            ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
            Food food = stack.getItem().getFood();

            if (food != null && event.getEntityPlayer().canEat(food.canEatWhenFull())) {
                return;
            }

            if (stack.getUseAction() == UseAction.DRINK) {
                return;
            }

            for (Map.Entry<ResourceLocation, Tuple<Integer, Boolean>> entry : items.entrySet()) {

                Item item = stack.getItem();
                if (item.getRegistryName() == null || !item.getRegistryName().equals(entry.getKey())) {
                    continue;
                }

                if (manager.addHealth(player, entry.getValue().getA(), !entry.getValue().getB())) {
                    stack.shrink(1);
                    return;
                }

                break;
            }
        });
    }

    @SubscribeEvent
    public void itemInteractFinished(LivingEntityUseItemEvent.Finish event) {
        if (!this.enabled.get() || !(event.getEntity() instanceof ServerPlayerEntity)) {
            return;
        }

        for (Map.Entry<ResourceLocation, Tuple<Integer, Boolean>> entry : this.items.entrySet()) {

            Item item = event.getItem().getItem();
            if (item.getRegistryName() == null || !item.getRegistryName().equals(entry.getKey())) {
                continue;
            }

            HealthManager.getInstance((ServerPlayerEntity) event.getEntity(), (manager, player) -> manager.addHealth(player, entry.getValue().getA(), !entry.getValue().getB()));
            break;
        }
    }

    @Override
    public String name() {
        return "health";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {
        enabled = builder.worldRestart().define("enabled", true);

        builder.push("values");

        max_health = builder
                .comment("The highest amount of health a player can have")
                .worldRestart()
                .defineInRange("maxHealthAmount", 20, 1, 1024);

        min_health = builder
                .comment("The lowest amount of health a player can have. Can be set to 0 in case only maxHealthAmount is wanted")
                .worldRestart()
                .defineInRange("minHealthAmount", 6, 0, 1024);

        health_decrease = builder
                .comment("The amount of health lost on each death. It will only work if minHealthAmount is higher than 0")
                .worldRestart()
                .defineInRange("deathDecreasedHealthAmount", 1, 0, 1024);

        ForgeConfigSpec.ConfigValue<String> startingValue = builder
                .comment("The starting health for the player. Possible values are MIN, MAX or just a number")
                .worldRestart()
                .define("startingHealthAmount", "MIN");

        runnables.add(() -> {
            switch (startingValue.get()) {
                case "MIN":
                    this.starting_health = min_health.get();
                    break;
                case "MAX":
                    this.starting_health = max_health.get();
                    break;
                default:
                    int i = Integer.parseInt(startingValue.get().replaceAll("[^0-9]", ""));
                    if (i > max_health.get() || i < min_health.get()) {
                        StatsKeeper.LOGGER.error("Starting health '{}' is out of bounds! Using default value...", i);
                        this.starting_health = min_health.get();
                        break;
                    }
                    this.starting_health = i;
                    break;
            }
        });

        builder.pop();

        builder.push("additional");

        List<String> defaults = Arrays.asList("MIN_HEALTH", "MAX_HEALTH", "STARTING_HEALTH");
        on_change_reset = builder
                .comment("Config options which should be considered for the reset of health. All available are used by default")
                .worldRestart()
                .defineList("configChangeReset", defaults, o -> o != null && String.class.isAssignableFrom(o.getClass()) && defaults.contains(o.toString().toUpperCase()));

        message = builder
                .comment("Show a message when a threshold is reached and when health is gained or lost")
                .define("healthChangeMessage", true);

        ForgeConfigSpec.ConfigValue<List<? extends Integer>> thresholdsValue = builder
                .comment("Values which, when achieved, move the lowest health of the player to the achieved value.\n" +
                        "The first threshold can also be non-removable, meaning that the health won't be removed till the player is over that threshold.\n" +
                        "This can only be used on the first threshold. To use it make the number negative. Make sure the values are in ascending order!")
                .worldRestart()
                .defineList("healthThresholds", Lists.newArrayList(-8, 16), o -> o != null && Integer.class.isAssignableFrom(o.getClass()));

        runnables.add(() -> this.thresholds = ImmutableSortedSet.copyOf(thresholdsValue.get()));


        ForgeConfigSpec.ConfigValue<List<? extends String>> itemsValue = builder
                .comment("Items that increase health when used. Use a equal sign to define how much health is gained or lost.\n" +
                        "e.g. 'minecraft:apple = 1', the health gets increased by 0.5 hearts.\n" +
                        "Appending a ':' after the number will make the item which decreases health bypass thresholds")
                .worldRestart()
                .defineList("regenerativeItems", Lists.newArrayList("minecraft:nether_star = 1"), o -> o != null && String.class.isAssignableFrom(o.getClass()));

        runnables.add(() -> {
            this.items = new HashMap<>();
            for (String string : itemsValue.get()) {
                string = string.replaceAll("[\\s+]", "");

                String name = string.substring(0, string.indexOf("="));

                if (!ForgeRegistries.ITEMS.containsKey(new ResourceLocation(name))) {
                    StatsKeeper.LOGGER.error("Regenerative Item '{}' could not be found in the item registry. Skipping...", name);
                    continue;
                }

                int amount = Integer.parseInt(string.substring(string.indexOf("=") + 1, (string.endsWith(":") ? string.lastIndexOf(":") : string.length())));

                if (amount == 0) {
                    StatsKeeper.LOGGER.error("Regenerative Item '{}' cannot have health set to 0. Skipping...", name);
                    continue;
                }

                boolean bypass = string.endsWith(":");

                if (bypass && amount > 0) {
                    StatsKeeper.LOGGER.error("Regenerative Item '{}' cannot bypass thresholds when it gains health. Skipping...", name);
                    continue;
                }

                this.items.put(new ResourceLocation(name), new Tuple<>(amount, bypass));
            }
        });

        builder.pop();
    }
}
