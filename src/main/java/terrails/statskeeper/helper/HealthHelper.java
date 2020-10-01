package terrails.statskeeper.helper;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import terrails.statskeeper.StatsKeeper;

public class HealthHelper {

    public static EntityAttributeInstance getAttribute(PlayerEntity player) {
        return player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
    }

    public static void addModifier(PlayerEntity player, int amount) {
        EntityAttributeInstance attribute = HealthHelper.getAttribute(player);
        attribute.removeModifier(StatsKeeper.HEALTH_UUID);
        attribute.addPersistentModifier(new EntityAttributeModifier(StatsKeeper.HEALTH_UUID, StatsKeeper.MOD_ID, amount - attribute.getBaseValue(), Operation.ADDITION));
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
        player.sendMessage(component, true);
    }
}
