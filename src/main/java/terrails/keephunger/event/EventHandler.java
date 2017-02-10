package terrails.keephunger.event;


import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import terrails.keephunger.config.ConfigHandler;
import terrails.keephunger.potion.ModPotions;

import java.lang.reflect.Field;

public class EventHandler
{

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
        public void keepHunger(PlayerEvent.Clone player)
        {
            if((player.isWasDeath() && ConfigHandler.respawnMin) &&
            player.getOriginal().getFoodStats().getFoodLevel() <= ConfigHandler.minRespawn) {
                player.getEntityPlayer().getFoodStats().setFoodLevel(ConfigHandler.minRespawn);

            } else if ((player.isWasDeath()) &&
                    (ConfigHandler.keepHunger)) {
                player.getEntityPlayer().getFoodStats().setFoodLevel(player.getOriginal().getFoodStats().getFoodLevel());
            }

            if(player.isWasDeath() && ConfigHandler.saturation) {
                // player.getEntityPlayer().getFoodStats().setFoodSaturationLevel(player.getOriginal().getFoodStats().getSaturationLevel());
                setFoodSaturationLevel(player.getEntityPlayer().getFoodStats(), player.getOriginal().getFoodStats().getSaturationLevel());
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