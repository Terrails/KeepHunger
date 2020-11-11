package terrails.statskeeper.feature;

import com.google.common.collect.*;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.data.HealthManager;
import terrails.statskeeper.api.event.*;
import terrails.statskeeper.helper.HealthHelper;

import java.util.*;

public class HealthFeature extends Feature {

    private final static PropertyMirror<Boolean> enabled;
    private final static PropertyMirror<Boolean> message;
    private final static PropertyMirror<Boolean> hardcore;
    private final static PropertyMirror<OnChangeReset[]> onChangeReset;

    private final static PropertyMirror<Integer> maxHealth;
    private final static PropertyMirror<Integer> minHealth;
    private final static PropertyMirror<Integer> startingHealth;
    private final static PropertyMirror<Integer> healthDecrease;

    private final static PropertyMirror<RegenerativeItem[]> regenerativeItems;
    private final static PropertyMirror<NavigableSet<Integer>> thresholds;

    public static class Handler implements HealthManager {

        /** PlayerEntity that is bound to this manager */
        private final ServerPlayerEntity playerEntity;

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

        public Handler(ServerPlayerEntity player) {
            this.playerEntity = player;
        }

        @Override
        public void update() {
            if (!playerEntity.isAlive() || !enabled.getValue()) {
                return;
            }

            if (hasConfigChanged()) {
                this.reset();
                return;
            }

            int prevThreshold = this.threshold;
            int prevHealthAmount = this.amount;

            if (!HealthHelper.hasModifier(playerEntity)) {
                this.amount = this.start;
            }

            Integer integer = thresholds.getValue().floor(this.amount);
            this.threshold = integer != null ? Math.abs(integer) : 0;

            if (this.start == this.max && this.min <= 0 && !hardcore.getValue()) {
                this.amount = this.max;
            } else {
                int min = Math.max(this.min, this.threshold);
                this.amount = MathHelper.clamp(this.amount, min, this.max);
            }

            if (prevHealthAmount != this.amount) {
                this.setHealth(this.amount);
            }

            if (prevThreshold != this.threshold && prevThreshold != 0 && this.threshold > 0) {
                HealthHelper.playerMessage(playerEntity, "health.statskeeper.threshold", this.threshold);
            }
        }

        @Override
        public boolean setHealth(int amount) {
            if (!playerEntity.isAlive() || !enabled.getValue()) {
                return false;
            }

            amount = MathHelper.clamp(amount, this.min, this.max);
            HealthHelper.addModifier(playerEntity, amount);

            if (this.amount != amount) {
                playerEntity.setHealth(playerEntity.getHealth() + Math.max(amount - this.amount, 0));
                this.amount = amount;
                this.update();
                return true;
            }

            return false;
        }

        @Override
        public boolean addHealth(int amount, boolean threshold) {
            if (!playerEntity.isAlive() || !enabled.getValue()) {
                return false;
            }

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
                HealthHelper.playerMessage(playerEntity, key, Math.abs((this.amount - prevHealth)));
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
            this.start = startingHealth.getValue();
            this.max = maxHealth.getValue();
            this.min = minHealth.getValue();
            this.setHealth(this.start);
            this.amount = this.start;
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
            return (this.min > 0 && this.amount > min) || hardcore.getValue();
        }

        @Override
        public void serialize(CompoundTag compound) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("sk:additional_health", this.amount - 20);
            tag.putInt("sk:max_health", this.max);
            tag.putInt("sk:min_health", this.min);
            tag.putInt("sk:starting_health", this.start);
            tag.putInt("sk:health_threshold", this.threshold);
            compound.put(StatsKeeper.MOD_ID, tag);
        }

        @Override
        public void deserialize(CompoundTag compound) {
            CompoundTag tag = compound.contains(StatsKeeper.MOD_ID) ? compound.getCompound(StatsKeeper.MOD_ID) : compound;

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
            for (OnChangeReset ocr : onChangeReset.getValue()) {

                if (ocr == OnChangeReset.MIN_HEALTH && minHealth.getValue() != this.min) {
                    return true;
                }

                if (ocr == OnChangeReset.MAX_HEALTH && maxHealth.getValue() != this.max) {
                    return true;
                }

                if (ocr == OnChangeReset.STARTING_HEALTH && startingHealth.getValue() != this.start) {
                    return true;
                }
            }
            return false;
        }
    }

    final PlayerJoinCallback PLAYER_JOIN = (ServerPlayerEntity player) -> {
        if (!enabled.getValue()) {
            HealthHelper.removeModifier(player);
            return;
        }

        HealthManager.apply(player, HealthManager::update);
    };

    final PlayerCopyCallback PLAYER_COPY = (ServerPlayerEntity player, ServerPlayerEntity oldPlayer, boolean isEnd) -> {
        if (!enabled.getValue()) {
            return;
        }

        HealthManager.apply(player, manager -> {

            HealthManager.apply(oldPlayer, oldManager -> {
                CompoundTag tag = new CompoundTag();
                oldManager.serialize(tag);
                manager.deserialize(tag);
                manager.setHealth(manager.getHealth());
            });

            if (startingHealth.getValue().equals(maxHealth.getValue()) && minHealth.getValue() <= 0 && !hardcore.getValue()) {
                manager.update();
                return;
            }

            int decrease = healthDecrease.getValue();
            if (!isEnd && !oldPlayer.isSpectator() && !oldPlayer.isCreative() && decrease > 0 && manager.isHealthRemovable()) {
                int prevHealth = manager.getHealth();
                manager.addHealth(-decrease);
                double removedAmount = manager.getHealth() - prevHealth;
                if (message.getValue() && removedAmount > 0 && manager.getHealth() > 0) {
                    HealthHelper.playerMessage(player, "health.statskeeper.death_remove", removedAmount);
                }
            }
        });
    };

    final PlayerRespawnCallback PLAYER_RESPAWN = (ServerPlayerEntity player, boolean isEnd) -> {
        if (isEnd || !enabled.getValue() || player.isSpectator() || player.isCreative()) {
            return;
        }

        HealthManager.apply(player, manager -> {
            player.setHealth(player.getMaxHealth());

            if (hardcore.getValue() && healthDecrease.getValue() > 0 && manager.getHealth() <= 0) {
                player.setGameMode(GameMode.SPECTATOR);
                manager.reset();
                player.setHealth(player.getMaxHealth());
                TranslatableText text = new TranslatableText("health.statskeeper.hardcore_death");
                player.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, text));
            }

        });
    };

    final PlayerUseItemCallback ITEM_INTERACT = (ServerPlayerEntity player, World world, Hand hand) -> {
        if (!enabled.getValue() || world.isClient || player.isCreative()) {
            return;
        }

        HealthManager.apply(player, manager -> {

            ItemStack stack = player.getStackInHand(hand);
            FoodComponent food = stack.getItem().getFoodComponent();

            if (food != null && player.canConsume(food.isAlwaysEdible())) {
                return;
            }

            if (stack.getUseAction() == UseAction.DRINK) {
                return;
            }

            for (RegenerativeItem item : regenerativeItems.getValue()) {

                if (!Registry.ITEM.getId(stack.getItem()).equals(item.identifier)) {
                    continue;
                }

                if (manager.addHealth(item.amount, !item.bypassesThreshold)) {
                    stack.decrement(1);
                    return;
                }

                break;
            }
        });
    };

    final PlayerUseFinishedCallback ITEM_USE_FINISHED = (PlayerEntity player, ItemStack stack) -> {
        if (!enabled.getValue() || player.world.isClient || player.isCreative()) {
            return;
        }

        HealthManager.apply((ServerPlayerEntity) player, manager -> {

            for (RegenerativeItem item : regenerativeItems.getValue()) {

                if (!Registry.ITEM.getId(stack.getItem()).equals(item.identifier)) {
                    continue;
                }

                manager.addHealth(item.amount, !item.bypassesThreshold);
                break;
            }
        });
    };

    @Override
    public void initializeEvents() {
        PlayerJoinCallback.EVENT.register(PLAYER_JOIN);
        PlayerCopyCallback.EVENT.register(PLAYER_COPY);
        PlayerRespawnCallback.EVENT.register(PLAYER_RESPAWN);
        PlayerUseItemCallback.EVENT.register(ITEM_INTERACT);
        PlayerUseFinishedCallback.EVENT.register(ITEM_USE_FINISHED);
    }

    @Override
    public String name() {
        return "health";
    }

    @Override
    public void setupConfig(ConfigTreeBuilder tree) {
        configValue(tree, "enabled", enabled, true);

        ConfigTreeBuilder values = tree.fork("values");

        configValue(values, "max_health", maxHealth, 20,
                "The highest amount of health a player can have.");
        configValue(values, "min_health", minHealth, 6,
                "The lowest amount of health a player can have. Can be set to 0 in case only 'max_health' is wanted.");
        configValue(values, "death_decreased_health", healthDecrease, 1,
                "The amount of health lost on each death. It will only work if 'min_health' is higher than 0.");
        configValue(values, "starting_health", startingHealth, 6,
                "The starting health for the player. Possible values are \"MIN\", \"MAX\" or just a number.");

        values.build();

        ConfigTreeBuilder additional = tree.fork("additional");

        configValue(additional, "on_change_reset", onChangeReset, OnChangeReset.values(),
                "Config options which when changed should be considered for the reset of health in an already created world.\n" +
                        "All available options are used by default.");

        configValue(additional, "message", message, true,
                "Show a message when a threshold is reached and when health is gained or lost.");

        configValue(additional, "hardcore", hardcore, false,
                "Enables 'hardcore' mode which makes the player a spectator when 0 max health is reached.\n" +
                        "Setting 'min_health' to 0 and removing all 'thresholds' is required or unexpected behaviour might happen.");

        configValue(additional, "thresholds", thresholds, ImmutableSortedSet.of(-8, 16),
                "Values which, when achieved, move the lowest health of the player to the achieved value.\n" +
                        "The first threshold can also be non-removable, meaning that the health won't be removed until the player is over that threshold.\n" +
                        "This can only be used on the first threshold. To use it make the number negative.\n" +
                        "Make sure that the values are in ascending order!");

        RegenerativeItem[] items = {new RegenerativeItem(new Identifier("minecraft:nether_star"), 1, false)};
        configValue(additional, "regenerative_items", regenerativeItems, items,
                "Items that increase/decrease health when used. Use an equal sign to define how much health is gained or lost.\n" +
                        "e.g. 'minecraft:apple = 1', the health gets increased by 0.5 hearts.\n" +
                        "Appending a ':' after the number will make an item bypass 'thresholds' when decreasing health.");

        additional.build();
    }

    private enum OnChangeReset {
        MIN_HEALTH, MAX_HEALTH, STARTING_HEALTH
    }

    private static class RegenerativeItem {

        public final Identifier identifier;
        public final int amount;
        public final boolean bypassesThreshold;

        public RegenerativeItem(Identifier identifier, int amount, boolean bypassesThreshold) {
            this.identifier = identifier;
            this.amount = amount;
            this.bypassesThreshold = bypassesThreshold;
        }

        public static RegenerativeItem[] fromString(String... strings) {
            RegenerativeItem[] regenerativeItems = new RegenerativeItem[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i].replaceAll("[\\s+]", "");
                String name = str.substring(0, str.indexOf("="));
                int amount = Integer.parseInt(str.substring(str.indexOf("=") + 1, (str.endsWith(":") ? str.lastIndexOf(":") : str.length())).trim());
                boolean bypassesThreshold = str.endsWith(":");

                regenerativeItems[i] = new RegenerativeItem(new Identifier(name), amount, amount < 0 && bypassesThreshold);
            }
            return regenerativeItems;
        }

        public static String[] toString(RegenerativeItem... regenerativeItems) {
            String[] strings = new String[regenerativeItems.length];
            for (int i = 0; i < regenerativeItems.length; i++) {
                RegenerativeItem item = regenerativeItems[i];
                strings[i] = item.identifier.toString() + " = " + item.amount + (item.bypassesThreshold ? ":" : "");
            }
            return strings;
        }
    }

    static {
        enabled = PropertyMirror.create(ConfigTypes.BOOLEAN);
        message = PropertyMirror.create(ConfigTypes.BOOLEAN);
        hardcore = PropertyMirror.create(ConfigTypes.BOOLEAN);

        maxHealth = PropertyMirror.create(ConfigTypes.INTEGER.withValidRange(1, 1024, 1));
        minHealth = PropertyMirror.create(ConfigTypes.INTEGER.withValidRange(0, 1024, 1));
        startingHealth = PropertyMirror.create(ConfigTypes.STRING.derive(Integer.class, s -> {
            s = s.toUpperCase();
            if (s.equals("MIN")) {
                if (minHealth.getValue() == 0) {
                    StatsKeeper.LOGGER.error("'starting_health' cannot be set to '{}' while 'min_health' is set to 0.", s);
                    StatsKeeper.LOGGER.error("Using 'max_health' as an alternative! Things will not behave as expected.");
                    return maxHealth.getValue();
                } else return minHealth.getValue();
            } else if (s.equals("MAX")) {
                return maxHealth.getValue();
            } else {
                try {
                    int value = Integer.parseInt(s);
                    if (value > maxHealth.getValue() || value < minHealth.getValue()) {
                        StatsKeeper.LOGGER.error("'starting_health' '{}' is out of bounds!", value);
                        StatsKeeper.LOGGER.error("Using 'max_health' as an alternative! Things will not behave as expected.");
                        return maxHealth.getValue();
                    } else return value;
                } catch (NumberFormatException e) {
                    StatsKeeper.LOGGER.error("'starting_health' is invalid. Accepted values are \"MIN\", \"MAX\" or a number.");
                    StatsKeeper.LOGGER.error("Using 'max_health' as an alternative! Things will not behave as expected.");
                    e.printStackTrace();
                    return maxHealth.getValue();
                }
            }
        }, i -> {
            if (i.equals(maxHealth.getValue())) return "MAX";
            else if (i.equals(minHealth.getValue())) return "MIN";
            else return i.toString();
        }));
        healthDecrease = PropertyMirror.create(ConfigTypes.INTEGER.withValidRange(0, 1024, 1));

        onChangeReset = PropertyMirror.create(ConfigTypes.makeArray(ConfigTypes.makeEnum(OnChangeReset.class)));

        regenerativeItems = PropertyMirror.create(ConfigTypes.makeArray(ConfigTypes.STRING)
                .derive(RegenerativeItem[].class, RegenerativeItem::fromString, RegenerativeItem::toString));

        thresholds = PropertyMirror.create(ConfigTypes.makeSet(ConfigTypes.INTEGER).derive(NavigableSet.class, ImmutableSortedSet::copyOf, (s) -> s));
    }
}
