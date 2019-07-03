package terrails.statskeeper.health;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.config.SKHealthConfig;

public class HealthHelper {

    static boolean hasConfigChanged(int minHealth, int maxHealth, int startingHealth) {
        for (String string : SKHealthConfig.ON_CHANGE_RESET) {
            string = string.toUpperCase();

            if (string.equals("MIN_HEALTH") && SKHealthConfig.MIN_HEALTH != minHealth) {
                return true;
            }

            if (string.equals("MAX_HEALTH") && SKHealthConfig.MAX_HEALTH != maxHealth) {
                return true;
            }

            if (string.equals("STARTING_HEALTH") && SKHealthConfig.STARTING_HEALTH != startingHealth) {
                return true;
            }
        }
        return false;
    }

    public static IAttributeInstance getAttribute(PlayerEntity player) {
        return player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
    }
    public static void addModifier(PlayerEntity player, int amount) {
        IAttributeInstance attribute = HealthHelper.getAttribute(player);
        attribute.removeModifier(StatsKeeper.HEALTH_UUID);
        attribute.applyModifier(new AttributeModifier(StatsKeeper.HEALTH_UUID, StatsKeeper.MOD_ID, amount - attribute.getBaseValue(), AttributeModifier.Operation.ADDITION));
    }
    public static boolean hasModifier(PlayerEntity player) {
        return getAttribute(player).getModifier(StatsKeeper.HEALTH_UUID) != null;
    }
    public static void removeModifier(PlayerEntity player) {
        getAttribute(player).removeModifier(StatsKeeper.HEALTH_UUID);
    }

    public static void playerMessage(PlayerEntity player, String key, double health) {
        if (health == 0) return;
        double messageAmount = health / 2.0;
        TranslationTextComponent component = messageAmount % 1 != 0 ? new TranslationTextComponent(key, messageAmount) : new TranslationTextComponent(key, (int) messageAmount);
        player.sendStatusMessage(component, true);
    }
}
