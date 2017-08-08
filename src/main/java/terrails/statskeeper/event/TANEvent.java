package terrails.statskeeper.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.Constants;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.packet.ThirstMessage;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;

public class TANEvent{

    public static void init() {
        if (Loader.isModLoaded("ToughAsNails") || Loader.isModLoaded("toughasnails")) {
            Constants.LOGGER.info("TAN addon activated!");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone player) {
        if (Loader.isModLoaded("toughasnails") || Loader.isModLoaded("ToughAsNails")) {
            final IThirst originalPlayer = player.getOriginal().getCapability(TANCapabilities.THIRST, null);
            final IThirst clonedPlayer = player.getEntityPlayer().getCapability(TANCapabilities.THIRST, null);

            final ITemperature originalTemp = player.getOriginal().getCapability(TANCapabilities.TEMPERATURE, null);
            final ITemperature clonedTemp = player.getEntityPlayer().getCapability(TANCapabilities.TEMPERATURE, null);

            if (ConfigHandler.keepThirst && player.isWasDeath()) {
                int thirstValue = ConfigHandler.minThirstAmount >= originalPlayer.getThirst() ? ConfigHandler.minThirstAmount : originalPlayer.getThirst();
                clonedPlayer.setThirst(thirstValue);

            }
            if (ConfigHandler.keepHydration && player.isWasDeath()) {
                float hydrationValue = ConfigHandler.minHydrationAmount >= originalPlayer.getThirst() ? ConfigHandler.minHydrationAmount : originalPlayer.getHydration();
                clonedPlayer.setHydration(hydrationValue);
            }
            if (ConfigHandler.keepTemperature && player.isWasDeath()) {
                clonedTemp.setTemperature(originalTemp.getTemperature());
            }
        }
    }

    @SubscribeEvent
    public void playerRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
        if (Loader.isModLoaded("ToughAsNails") || Loader.isModLoaded("toughasnails")) {
            if (ConfigHandler.keepThirst) {
                EntityPlayerMP player = (EntityPlayerMP) event.player;
                IThirst thirstData = ThirstHelper.getThirstData(event.player);
                StatsKeeper.wrapperInstance.sendTo(new ThirstMessage(thirstData.getThirst()), player);
            }
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerLoggedInEvent event) {
        if (Loader.isModLoaded("ToughAsNails") || Loader.isModLoaded("toughasnails")) {
            if (ConfigHandler.keepThirst) {
                EntityPlayerMP player = (EntityPlayerMP) event.player;
                IThirst thirstData = ThirstHelper.getThirstData(event.player);
                StatsKeeper.wrapperInstance.sendTo(new ThirstMessage(thirstData.getThirst()), player);
            }
        }
    }

    @SubscribeEvent
    public void onWorldChange(PlayerChangedDimensionEvent event) {
        if (Loader.isModLoaded("ToughAsNails") || Loader.isModLoaded("toughasnails")) {
            if (ConfigHandler.keepThirst) {
                if (event.player instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) event.player;
                    IThirst thirstData = ThirstHelper.getThirstData(player);
                    StatsKeeper.wrapperInstance.sendTo(new ThirstMessage(thirstData.getThirst()), player);
                }
            }
        }
    }

}