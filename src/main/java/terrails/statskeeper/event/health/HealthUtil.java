package terrails.statskeeper.event.health;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.Loader;
import terrails.statskeeper.Constants;
import terrails.statskeeper.config.ConfigHandler;

import java.util.UUID;

public class HealthUtil {

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
}
