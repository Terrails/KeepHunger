package terrails.statskeeper.api.helper;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class PlayerStats {

    public static IAttributeInstance getAttribute(EntityPlayer player, IAttribute attribute) {
        if (player != null)
            return player.getAttributeMap().getAttributeInstance(attribute);
        return null;
    }
    public static IAttributeInstance getMaxHealthAttribute(EntityPlayer player) {
        return getAttribute(player, SharedMonsterAttributes.MAX_HEALTH);
    }

    public static void removeModifier(EntityPlayer entity, IAttribute attribute, UUID modifierUUID) {
        if (entity != null) {
            AttributeModifier modifier = getAttribute(entity, attribute).getModifier(modifierUUID);
            if (modifier != null) {
                getAttribute(entity, attribute).removeModifier(modifier);
            }
        }
    }
    public static void removeMaxHealthModifier(EntityPlayer entity, UUID modifierUUID) {
        if (entity != null) {
            AttributeModifier modifier = getMaxHealthAttribute(entity).getModifier(modifierUUID);
            if (modifier != null) {
                getMaxHealthAttribute(entity).removeModifier(modifier);
            }
        }
    }

    public static void setMaxHealth(EntityPlayer player, UUID modifierUUID, double health) {
        if (player != null) {
            removeMaxHealthModifier(player, modifierUUID);
            getMaxHealthAttribute(player).applyModifier(new AttributeModifier(modifierUUID, "StatsKeeper HP", health/* - getAttribute(player).getBaseValue()*/, 0));
            player.setHealth(player.getMaxHealth());
        }
    }
}
