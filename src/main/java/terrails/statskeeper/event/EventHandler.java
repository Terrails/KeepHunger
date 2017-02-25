package terrails.statskeeper.event;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.potion.ModPotions;

import java.lang.reflect.Field;

public class EventHandler
{
        @SubscribeEvent
        public void keepHunger(PlayerEvent.Clone player)
        {
            if((player.isWasDeath() && ConfigHandler.respawnMinHungerBoolean) &&
            player.getOriginal().getFoodStats().getFoodLevel() <= ConfigHandler.minHungerValue) {
                player.getEntityPlayer().getFoodStats().setFoodLevel(ConfigHandler.minHungerValue);

            } else if ((player.isWasDeath()) &&
                    (ConfigHandler.keepHunger)) {
                player.getEntityPlayer().getFoodStats().setFoodLevel(player.getOriginal().getFoodStats().getFoodLevel());
            }

            if(player.isWasDeath() && ConfigHandler.saturation) {
                setFoodSaturationLevel(player.getEntityPlayer().getFoodStats(), player.getOriginal().getFoodStats().getSaturationLevel());
            }
        }

        public void setFoodSaturationLevel(FoodStats food, float sat)
        {
            try
            {
                Field setSaturationLevel = ReflectionHelper.findField(FoodStats.class, "foodSaturationLevel", "field_75125_b", "b");
                setSaturationLevel.set(food, sat);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }

        @SubscribeEvent
        public void keepExp(PlayerEvent.Clone player)
        {
            if ((ConfigHandler.keepXP) &&
                    (player.isWasDeath())) {
                player.getEntityPlayer().addExperience(player.getOriginal().experienceTotal);
            }
        }

        @SubscribeEvent
        public void applyEffect(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent player)
        {
            if (ConfigHandler.noEating) {
                player.player.addPotionEffect(new PotionEffect(ModPotions.appetite, ConfigHandler.noEatingTime * 20));
            }
        }
}

