package terrails.statskeeper.feature;

import net.minecraft.block.Block;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import terrails.statskeeper.api.SKEffects;

public class HungerFeature extends Feature {

    private static ForgeConfigSpec.BooleanValue keep_hunger;
    private static ForgeConfigSpec.IntValue lowest_hunger;

    private static ForgeConfigSpec.BooleanValue keep_saturation;
    private static ForgeConfigSpec.BooleanValue keep_saturation_when_hunger_is_maxed;
    private static ForgeConfigSpec.IntValue lowest_saturation;

    private static ForgeConfigSpec.IntValue no_appetite_time;

    @SubscribeEvent
    public void clone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PlayerEntity player = event.getPlayer();
            PlayerEntity oldPlayer = event.getOriginal();

            if (keep_hunger.get()) {
                int value = Math.max(lowest_hunger.get(), oldPlayer.getFoodStats().getFoodLevel());
                player.getFoodStats().setFoodLevel(value);
            }

            if (keep_saturation.get()) {
                if (keep_saturation_when_hunger_is_maxed.get() && oldPlayer.getFoodStats().needFood()) {
                    return;
                }

                float value = Math.max(lowest_saturation.get(), oldPlayer.getFoodStats().getSaturationLevel());
                ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, player.getFoodStats(), value, "field_75125_b");
            }
        }
    }

    @SubscribeEvent
    public void respawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (no_appetite_time.get() > 0 && !player.isCreative()) {
            player.addPotionEffect(new EffectInstance(SKEffects.NO_APPETITE, no_appetite_time.get() * 20, 0, false, false, true));
        }
    }

    @SubscribeEvent
    public void itemInteract(PlayerInteractEvent.RightClickItem event) {
        if (!event.getPlayer().isPotionActive(SKEffects.NO_APPETITE)) {
            return;
        }

        Food food = event.getPlayer().getHeldItemMainhand().getItem().getFood();
        if (food != null && event.getPlayer().canEat(food.canEatWhenFull())) {
            event.setCanceled(true);
            return;
        }

        food = event.getPlayer().getHeldItemOffhand().getItem().getFood();
        if (food != null && event.getPlayer().canEat(food.canEatWhenFull())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void blockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getPlayer().isPotionActive(SKEffects.NO_APPETITE)) {
            return;
        }

        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (block instanceof CakeBlock) {
            event.setCanceled(true);
        }
    }

    @Override
    public String name() {
        return "hunger";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {
        keep_hunger = builder
                .comment("Make the player keep hunger when respawning")
                .define("keepHunger", true);

        lowest_hunger = builder
                .comment("The lowest hunger value the player can have when respawning, must be used with keepHunger")
                .defineInRange("lowestHunger", 6, 0, 20);

        builder.push("saturation");

        keep_saturation = builder
                .comment("Make the player keep saturation when respawning")
                .define("keepSaturation", true);

        lowest_saturation = builder
                .comment("The lowest saturation value the player can have when respawning, must be used with keepSaturation")
                .defineInRange("lowestSaturation", 6, 0, 20);

        keep_saturation_when_hunger_is_maxed = builder
                .comment("Make the player keep saturation when respawning only when hunger is full. Only usable with the other two options")
                .define("keepSaturationWithFullHunger", true);

        builder.pop();

        builder.push("no_appetite");

        no_appetite_time = builder
                .comment("The duration that the player will have the 'No Appetite' effect after respawning (seconds)")
                .defineInRange("effectDuration", 300, 0, Integer.MAX_VALUE);

        builder.pop();
    }
}
