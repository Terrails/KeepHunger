package terrails.keephunger.event;


import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.keephunger.config.ConfigHandler;
import terrails.keephunger.potion.ModPotions;

public class EventHandler
    {
        @SubscribeEvent
        public void keepHunger(PlayerEvent.Clone player)
        {
            if ((player.isWasDeath()) &&
                    (ConfigHandler.keepHunger)) {
                player.getEntityPlayer().getFoodStats().setFoodLevel(player.getOriginal().getFoodStats().getFoodLevel());
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