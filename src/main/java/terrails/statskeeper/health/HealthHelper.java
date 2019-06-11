package terrails.statskeeper.health;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.config.SKHealthConfig;

public class HealthHelper {

    static boolean hasConfigChanged(int minHealth, int maxHealth, int startingHealth) {
        for (String string : SKHealthConfig.on_change_reset) {
            string = string.toUpperCase();

            if (string.equals("MIN_HEALTH") && SKHealthConfig.min_health != minHealth) {
                return true;
            }

            if (string.equals("MAX_HEALTH") && SKHealthConfig.max_health != maxHealth) {
                return true;
            }

            if (string.equals("STARTING_HEALTH") && SKHealthConfig.starting_health != startingHealth) {
                return true;
            }
        }
        return false;
    }

    public static EntityAttributeInstance getAttribute(PlayerEntity player) {
        return player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
    }
    public static void addModifier(PlayerEntity player, int amount) {
        EntityAttributeInstance attribute = HealthHelper.getAttribute(player);
        attribute.removeModifier(StatsKeeper.HEALTH_UUID);
        attribute.addModifier(new EntityAttributeModifier(StatsKeeper.HEALTH_UUID, StatsKeeper.MOD_ID, amount - attribute.getBaseValue(), Operation.ADDITION));
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
        TranslatableText component = messageAmount % 1 != 0 ? new TranslatableText(key, messageAmount) : new TranslatableText(key, (int) messageAmount);
        player.addChatMessage(component, true);
    }
}
