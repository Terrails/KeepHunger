package terrails.statskeeper.event.health;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.Loader;
import terrails.statskeeper.Constants;
import terrails.statskeeper.config.ConfigHandler;
import toughasnails.api.HealthHelper;
//import toughasnails.api.HealthHelper;

import java.util.UUID;

public class HealthUtil {

    private boolean isTANLoaded = Loader.isModLoaded("ToughAsNails") || Loader.isModLoaded("toughasnails");
    public static HealthUtil INSTANCE = new HealthUtil();
    public static UUID STATS_KEEPER_HEALTH_UUID = Constants.STATS_KEEPER_HEALTH_UUID;

    public void setMaxHealth(EntityPlayer player, double health) {
        if (player != null) {
            removeModifier(player, STATS_KEEPER_HEALTH_UUID);

            IAttributeInstance attribute = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
            attribute.applyModifier(new AttributeModifier(STATS_KEEPER_HEALTH_UUID, "StatsKeeper HP", health - player.getMaxHealth(), 0));
            player.setHealth(player.getMaxHealth());
        }
    }


    public void removeMaxHealth(EntityPlayer player, EntityPlayer oldPlayer, double removeHealth, double minHealth) {
        if (player != null && oldPlayer != null) {
            removeModifier(player, STATS_KEEPER_HEALTH_UUID);
            double health = oldPlayer.getMaxHealth() - removeHealth > minHealth ? oldPlayer.getMaxHealth() - removeHealth: minHealth;

            IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            attribute.applyModifier(new AttributeModifier(STATS_KEEPER_HEALTH_UUID, "StatsKeeper MaxHP", health - player.getMaxHealth(), 0));
            player.setHealth(player.getMaxHealth());
        }
    }

    public void removeModifier(EntityPlayer entity, UUID modifierUUID) {
        if (entity != null) {
            IAttributeInstance attribute = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
            AttributeModifier modifier = attribute.getModifier(modifierUUID);
            if (modifier != null) {
                attribute.removeModifier(modifier);
            }
        }
    }

/*
    // Tough As Nails Methods
    public void addHealthTAN(EntityPlayer player, double health) {
        if (isTANLoaded) {
            double hearts = health / 2;
      //      HealthHelper.addActiveHearts(player, (int) hearts);
        }
    }
    public double getActiveHealthTAN(EntityPlayer player) {
        if (isTANLoaded) {
       //     int hearts = HealthHelper.getActiveHearts(player);
      //      return hearts * 2;
        }
        return 0;
    }
    public double getInactiveHealthTAN(EntityPlayer player) {
        if (isTANLoaded) {
        //    int hearts = HealthHelper.getInactiveHearts(player);
        //    return hearts * 2;
        }
        return 0;
    }
    public double getLifeBloodHealthTAN(EntityPlayer player) {
        if (isTANLoaded) {
        //    int hearts = HealthHelper.getLifebloodHearts(player);
       //     return hearts * 2;
        }
        return 0;
    }
    public double getHealthByDifficultyTAN(EntityPlayer entity) {
        if (isTANLoaded) {
            double TANDifficultyHealth;
            switch (entity.getEntityWorld().getDifficulty()) {
                case PEACEFUL:
                    TANDifficultyHealth = -20.0D; // TAN DEFAULT: 0.0D;
                    break;
                case EASY:
                    TANDifficultyHealth = -14.0D; // TAN DEFAULT: -6.0D;
                    break;
                case NORMAL:
                    TANDifficultyHealth = -10.0D; // TAN DEFAULT: -10.0D;
                    break;
                case HARD:
                    TANDifficultyHealth = -6.0D; // TAN DEFAULT: -14.0D;
                    break;
                default:
                    TANDifficultyHealth = 0.0D;
                    break;
            }
            double lifebloodHealth = HealthHelper.getLifebloodHearts(entity) * 2;
            TANDifficultyHealth -= lifebloodHealth;
            return TANDifficultyHealth;
        }
        return 0;
    }


    public void increaseHealth(EntityPlayer player, double health) {
        removeModifier(player, STATS_KEEPER_HEALTH_UUID);
        IAttributeInstance attribute = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        attribute.applyModifier(new AttributeModifier(STATS_KEEPER_HEALTH_UUID, "StatsKeeper HP", health, 0));
    }

    public void decreaseHealth(EntityPlayer player, EntityPlayer oldPlayer, double health) {
        IAttributeInstance attribute = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        removeModifier(player, STATS_KEEPER_HEALTH_UUID);
        attribute.applyModifier(new AttributeModifier(STATS_KEEPER_HEALTH_UUID, "StatsKeeper HP", oldPlayer.getMaxHealth() - health - oldPlayer.getMaxHealth(), 0));
        player.setHealth(player.getMaxHealth());
    }
*/
}
