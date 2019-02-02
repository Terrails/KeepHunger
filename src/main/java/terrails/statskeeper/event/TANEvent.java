package terrails.statskeeper.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.data.tan.ITAN;
import terrails.statskeeper.data.tan.CapabilityTAN;
import terrails.statskeeper.packet.ThirstMessage;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class TANEvent {

    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone player) {
        final IThirst originalPlayer = player.getOriginal().getCapability(TANCapabilities.THIRST, null);
        final IThirst clonedPlayer = player.getEntityPlayer().getCapability(TANCapabilities.THIRST, null);

        final ITemperature originalTemp = player.getOriginal().getCapability(TANCapabilities.TEMPERATURE, null);
        final ITemperature clonedTemp = player.getEntityPlayer().getCapability(TANCapabilities.TEMPERATURE, null);

        if (originalPlayer == null || clonedPlayer == null) {
            return;
        }

        if (originalTemp == null || clonedTemp == null) {
            return;
        }

        if (player.isWasDeath()) {
            if (SKConfig.Compatibility.TAN.keep_thirst) {
                int thirstValue = SKConfig.Compatibility.TAN.lowest_thirst >= originalPlayer.getThirst() ? SKConfig.Compatibility.TAN.lowest_thirst : originalPlayer.getThirst();
                clonedPlayer.setThirst(thirstValue);
            }
            if (SKConfig.Compatibility.TAN.keep_hydration) {
                float hydrationValue = SKConfig.Compatibility.TAN.lowest_hydration >= originalPlayer.getThirst() ? SKConfig.Compatibility.TAN.lowest_hydration : originalPlayer.getHydration();
                clonedPlayer.setHydration(hydrationValue);
            }
            if (SKConfig.Compatibility.TAN.keep_temperature) {
                clonedTemp.setTemperature(originalTemp.getTemperature());
            }
        }
    }

    @SubscribeEvent
    public void playerRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
        if (SKConfig.Compatibility.TAN.keep_thirst) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            IThirst thirstData = ThirstHelper.getThirstData(event.player);
            StatsKeeper.networkWrapper.sendTo(new ThirstMessage(thirstData.getThirst()), player);
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerLoggedInEvent event) {
        if (SKConfig.Compatibility.TAN.keep_thirst) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            ITAN itan = player.getCapability(CapabilityTAN.TAN_CAPABILITY, null);
            if (itan != null && itan.getThirst() != 0)
                StatsKeeper.networkWrapper.sendTo(new ThirstMessage((int) itan.getThirst()), player);
            else if (itan != null)
                StatsKeeper.networkWrapper.sendTo(new ThirstMessage(20), player);
        }
    }

    @SubscribeEvent
    public void onWorldChange(PlayerChangedDimensionEvent event) {
        if (SKConfig.Compatibility.TAN.keep_thirst) {
            if (event.player instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) event.player;
                IThirst thirstData = ThirstHelper.getThirstData(event.player);
                StatsKeeper.networkWrapper.sendTo(new ThirstMessage(thirstData.getThirst()), player);
            }
        }
    }

    @SubscribeEvent
    public void onLogout(PlayerLoggedOutEvent event) {
        if (SKConfig.Compatibility.TAN.keep_thirst) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            ITAN itan = player.getCapability(CapabilityTAN.TAN_CAPABILITY, null);
            IThirst thirstData = ThirstHelper.getThirstData(player);
            if (itan != null) {
                itan.setThirst(thirstData.getThirst());
            }
        }
    }
}