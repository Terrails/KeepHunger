package terrails.statskeeper.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.Constants;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.packet.ThirstMessage;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;

public class TANEvent{

             // Print to console if TAN addon is enabled \\
    public static void init(){
        if(Loader.isModLoaded("ToughAsNails")){
            Constants.LOGGER.info("TAN addon activated!");
        }
        else if(Loader.isModLoaded("toughasnails")){
            Constants.LOGGER.info("TAN addon activated!");
        }
    }

      //                                                          \\
     // Using duplicated event's for support of 1.9, 1.10 and 1.11 \\
    //                                                              \\
    @Optional.Method(modid = "ToughAsNails")
    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone player) {
        final IThirst originalPlayer = player.getOriginal().getCapability(TANCapabilities.THIRST, null);
        final IThirst clonedPlayer = player.getEntityPlayer().getCapability(TANCapabilities.THIRST, null);

        if (originalPlayer.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && player.isWasDeath()) {
            clonedPlayer.setThirst(ConfigHandler.thirstAmount);

        } else if (ConfigHandler.thirst && player.isWasDeath()) {
            clonedPlayer.setThirst(originalPlayer.getThirst());

        }
    }

    @Optional.Method(modid = "ToughAsNails")
    @SubscribeEvent
    public void playerRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
        if (ConfigHandler.thirstBoolean || ConfigHandler.thirst) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            IThirst thirstData = ThirstHelper.getThirstData(event.player);
            StatsKeeper.instance.sendTo(new ThirstMessage(thirstData.getThirst()), player);
        }
    }

    @Optional.Method(modid = "ToughAsNails")
    @SubscribeEvent
    public void onJoin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (ConfigHandler.thirstBoolean || ConfigHandler.thirst) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            IThirst thirstData = ThirstHelper.getThirstData(event.player);
            StatsKeeper.instance.sendTo(new ThirstMessage(thirstData.getThirst()), player);
        }
    }

    @Optional.Method(modid = "ToughAsNails")
    @SubscribeEvent
    public void onWorldChange(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        if (ConfigHandler.thirstBoolean || ConfigHandler.thirst) {
            if (event.player instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) event.player;
                IThirst thirstData = ThirstHelper.getThirstData(player);
                StatsKeeper.instance.sendTo(new ThirstMessage(thirstData.getThirst()), player);
            }
        }
    }

      //                                 \\
     // lowercase MOD_ID for 1.9 and 1.10 \\
    //                                     \\

    @Optional.Method(modid = "toughasnails")
    @SubscribeEvent
    public void onClonePlayerOldTAN(PlayerEvent.Clone player) {
        final IThirst originalPlayer = player.getOriginal().getCapability(TANCapabilities.THIRST, null);
        final IThirst clonedPlayer = player.getEntityPlayer().getCapability(TANCapabilities.THIRST, null);

        if (originalPlayer.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && player.isWasDeath()) {
            clonedPlayer.setThirst(ConfigHandler.thirstAmount);

        } else if (ConfigHandler.thirst && player.isWasDeath()) {
            clonedPlayer.setThirst(originalPlayer.getThirst());
        }
    }



    @Optional.Method(modid = "toughasnails")
    @SubscribeEvent
    public void playerRespawnOldTAN(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
        if (ConfigHandler.thirstBoolean || ConfigHandler.thirst) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            IThirst thirstData = ThirstHelper.getThirstData(event.player);
            StatsKeeper.instance.sendTo(new ThirstMessage(thirstData.getThirst()), player);
        }
    }

    @Optional.Method(modid = "toughasnails")
    @SubscribeEvent
    public void onJoinOldTAN(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (ConfigHandler.thirstBoolean || ConfigHandler.thirst) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            IThirst thirstData = ThirstHelper.getThirstData(event.player);
            StatsKeeper.instance.sendTo(new ThirstMessage(thirstData.getThirst()), player);
        }
    }
    @Optional.Method(modid = "toughasnails")
    @SubscribeEvent
    public void onWorldChangeOldTAN(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        if (ConfigHandler.thirstBoolean || ConfigHandler.thirst) {
            if (event.player instanceof EntityPlayerMP) {
                EntityPlayerMP entityPlayer = (EntityPlayerMP) event.player;
                IThirst thirstData = ThirstHelper.getThirstData(entityPlayer);
                StatsKeeper.instance.sendTo(new ThirstMessage(thirstData.getThirst()), entityPlayer);
            }
        }
    }

/*
    @SubscribeEvent
    public void onClonePlayerOldTAN(PlayerEvent.Clone player) {
        final IThirst originalPlayer = player.getOriginal().getCapability(TANCapabilities.THIRST, null);
        final IThirst clonedPlayer = player.getEntityPlayer().getCapability(TANCapabilities.THIRST, null);

        //Using multiple isModLoaded name's for 1.9-1.11 compatibility

        if (originalPlayer.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
            clonedPlayer.setThirst(ConfigHandler.thirstAmount);

        } else if (originalPlayer.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
            clonedPlayer.setThirst(ConfigHandler.thirstAmount);

        } else if (ConfigHandler.thirst && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
            clonedPlayer.setThirst(originalPlayer.getThirst());

        } else if (ConfigHandler.thirst && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
            clonedPlayer.setThirst(originalPlayer.getThirst());

        }
    }
*/
}