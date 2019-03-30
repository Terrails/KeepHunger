package terrails.statskeeper.health;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
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

    public static boolean isFoodAlwaysEdible(ItemFood item) {
        return ObfuscationReflectionHelper.getPrivateValue(ItemFood.class, item, "field_77852_bZ");
    }

    public static IAttributeInstance getAttribute(EntityPlayer player) {
        return player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
    }
    public static void addModifier(EntityPlayer player, int amount) {
        IAttributeInstance attribute = HealthHelper.getAttribute(player);
        attribute.removeModifier(StatsKeeper.HEALTH_UUID);
        attribute.applyModifier(new AttributeModifier(StatsKeeper.HEALTH_UUID, StatsKeeper.MOD_ID, amount - attribute.getBaseValue(), 0));
    }
    public static boolean hasModifier(EntityPlayer player) {
        return getAttribute(player).getModifier(StatsKeeper.HEALTH_UUID) != null;
    }
    public static void removeModifier(EntityPlayer player) {
        getAttribute(player).removeModifier(StatsKeeper.HEALTH_UUID);
    }

    public static void playerMessage(EntityPlayer player, String key, double health) {
        if (health == 0) return;
        double messageAmount = health / 2.0;
        TextComponentTranslation component = messageAmount % 1 != 0 ? new TextComponentTranslation(key, messageAmount) : new TextComponentTranslation(key, (int) messageAmount);
        player.sendStatusMessage(component, true);
    }
}
