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
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import terrails.statskeeper.api.SKEffects;

public class HungerFeature extends Feature {

    public static final HungerFeature INSTANCE = new HungerFeature();

    private ForgeConfigSpec.BooleanValue keep_hunger;
    private ForgeConfigSpec.IntValue lowest_hunger;

    private ForgeConfigSpec.BooleanValue keep_saturation;
    private ForgeConfigSpec.BooleanValue keep_saturation_when_hunger_is_maxed;
    private ForgeConfigSpec.IntValue lowest_saturation;

    private ForgeConfigSpec.IntValue no_appetite_time;

    @SubscribeEvent
    public void clone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PlayerEntity player = event.getEntityPlayer();
            PlayerEntity oldPlayer = event.getOriginal();

            if (this.keep_hunger.get()) {
                int value = Math.max(this.lowest_hunger.get(), oldPlayer.getFoodStats().getFoodLevel());
                player.getFoodStats().setFoodLevel(value);
            }

            if (this.keep_saturation.get()) {
                if (this.keep_saturation_when_hunger_is_maxed.get() && oldPlayer.getFoodStats().needFood()) {
                    return;
                }

                float value = Math.max(this.lowest_saturation.get(), oldPlayer.getFoodStats().getSaturationLevel());
                ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, player.getFoodStats(), value, "field_75125_b");
            }
        }
    }

    @SubscribeEvent
    public void respawn(PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (this.no_appetite_time.get() > 0 && !player.isCreative()) {
            player.addPotionEffect(new EffectInstance(SKEffects.NO_APPETITE, this.no_appetite_time.get() * 20, 0, false, false, true));
        }
    }

    @SubscribeEvent
    public void itemInteract(PlayerInteractEvent.RightClickItem event) {
        if (!event.getEntityPlayer().isPotionActive(SKEffects.NO_APPETITE)) {
            return;
        }

        Food food = event.getEntityPlayer().getHeldItemMainhand().getItem().getFood();
        if (food != null && event.getEntityPlayer().canEat(food.canEatWhenFull())) {
            event.setCanceled(true);
            return;
        }

        food = event.getEntityPlayer().getHeldItemOffhand().getItem().getFood();
        if (food != null && event.getEntityPlayer().canEat(food.canEatWhenFull())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void blockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntityPlayer().isPotionActive(SKEffects.NO_APPETITE)) {
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
        this.keep_hunger = builder
                .comment("Make the player keep hunger when respawning")
                .define("keepHunger", true);

        this.lowest_hunger = builder
                .comment("The lowest hunger value the player can have when respawning, must be used with keepHunger")
                .defineInRange("lowestHunger", 6, 0, 20);

        builder.push("saturation");

        this.keep_saturation = builder
                .comment("Make the player keep saturation when respawning")
                .define("keepSaturation", true);

        this.lowest_saturation = builder
                .comment("The lowest saturation value the player can have when respawning, must be used with keepSaturation")
                .defineInRange("lowestSaturation", 6, 0, 20);

        this.keep_saturation_when_hunger_is_maxed = builder
                .comment("Make the player keep saturation when respawning only when hunger is full. Only usable with the other two options")
                .define("keepSaturationWithFullHunger", true);

        builder.pop();

        builder.push("no_appetite");

        this.no_appetite_time = builder
                .comment("The duration that the player will have the 'No Appetite' effect after respawning (seconds)")
                .defineInRange("effectDuration", 300, 0, Integer.MAX_VALUE);

        builder.pop();
    }
}
