package terrails.statskeeper.feature;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import terrails.statskeeper.api.effect.SKEffects;
import terrails.statskeeper.api.event.PlayerCopyCallback;
import terrails.statskeeper.api.event.PlayerRespawnCallback;
import terrails.statskeeper.mixin.HungerManagerAccessor;

public class HungerFeature extends Feature {

    private final static PropertyMirror<Boolean> keepHunger;
    private final static PropertyMirror<Boolean> keepSaturation;
    private final static PropertyMirror<Boolean> keepSaturationOnFullHunger;

    private final static PropertyMirror<Integer> lowestHunger;
    private final static PropertyMirror<Integer> lowestSaturation;
    private final static PropertyMirror<Integer> noAppetiteDuration;

    final PlayerCopyCallback PLAYER_COPY = (ServerPlayerEntity player, ServerPlayerEntity oldPlayer, boolean isEnd) -> {
        if (!isEnd) {

            if (keepHunger.getValue()) {
                int value = Math.max(lowestHunger.getValue(), oldPlayer.getHungerManager().getFoodLevel());
                player.getHungerManager().setFoodLevel(value);
            }

            if (keepSaturation.getValue() && (keepSaturationOnFullHunger.getValue() || !oldPlayer.getHungerManager().isNotFull())) {
                float value = Math.max(lowestSaturation.getValue(), oldPlayer.getHungerManager().getSaturationLevel());
                HungerManagerAccessor manager = (HungerManagerAccessor) player.getHungerManager();
                manager.setFoodSaturationLevel(value);
            }
        }
    };

    final PlayerRespawnCallback PLAYER_RESPAWN = (ServerPlayerEntity player, boolean isEnd) -> {
        if (!isEnd && noAppetiteDuration.getValue() > 0 && !player.isCreative()) {
            player.addStatusEffect(new StatusEffectInstance(SKEffects.NO_APPETITE, noAppetiteDuration.getValue() * 20, 0, false, false, true));
        }
    };

    final UseBlockCallback BLOCK_INTERACT = (PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) -> {
        if (player.isSpectator() || !player.hasStatusEffect(SKEffects.NO_APPETITE)) return ActionResult.PASS;

        if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof CakeBlock) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    };

    final UseItemCallback ITEM_INTERACT = (PlayerEntity player, World world, Hand hand) -> {
        if (player.isSpectator() || !player.hasStatusEffect(SKEffects.NO_APPETITE)) return TypedActionResult.pass(ItemStack.EMPTY);

        ItemStack stack = player.getMainHandStack();
        FoodComponent setting = stack.getItem().getFoodComponent();
        if (setting != null && player.canConsume(setting.isAlwaysEdible())) {
            return TypedActionResult.fail(stack);
        }
        stack = player.getOffHandStack();
        setting = stack.getItem().getFoodComponent();
        if (setting != null && player.canConsume(setting.isAlwaysEdible())) {
            return TypedActionResult.fail(stack);
        }

        return TypedActionResult.pass(ItemStack.EMPTY);
    };

    @Override
    public void initializeEvents() {
        PlayerCopyCallback.EVENT.register(PLAYER_COPY);
        PlayerRespawnCallback.EVENT.register(PLAYER_RESPAWN);
        UseBlockCallback.EVENT.register(BLOCK_INTERACT);
        UseItemCallback.EVENT.register(ITEM_INTERACT);
    }

    @Override
    public String name() {
        return "hunger";
    }

    @Override
    public void setupConfig(ConfigTreeBuilder tree) {
        configValue(tree, "keep_hunger", keepHunger, true,
                "Keep hunger on respawn.");
        configValue(tree, "keep_saturation", keepSaturation, true,
                "Keep saturation on respawn.");

        configValue(tree, "lowest_hunger", lowestHunger, 6,
                "The lowest hunger value on respawn.");
        configValue(tree, "lowest_saturation", lowestSaturation, 6,
                "The lowest saturation value on respawn.");

        configValue(tree, "keep_saturation_on_max_hunger", keepSaturationOnFullHunger, true,
                "Keep saturation only when the hunger is full.");

        ConfigTreeBuilder noAppetite = tree.fork("no_appetite").withComment("An effect which forbids you from eating while its active.");

        configValue(noAppetite, "effect_duration", noAppetiteDuration, 300,
                "The duration of the effect after respawning in seconds.");

        noAppetite.build();
    }

    static {
        keepHunger = PropertyMirror.create(ConfigTypes.BOOLEAN);
        keepSaturation = PropertyMirror.create(ConfigTypes.BOOLEAN);
        keepSaturationOnFullHunger = PropertyMirror.create(ConfigTypes.BOOLEAN);

        lowestHunger = PropertyMirror.create(ConfigTypes.INTEGER.withValidRange(0, 20, 1));
        lowestSaturation = PropertyMirror.create(ConfigTypes.INTEGER.withValidRange(0, 20, 1));
        noAppetiteDuration = PropertyMirror.create(ConfigTypes.INTEGER.withMinimum(0));
    }
}
