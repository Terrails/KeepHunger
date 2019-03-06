package terrails.statskeeper.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.config.configs.SKTANConfig;
import terrails.statskeeper.packet.StatsMessageTAN;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.IPlayerStat;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;

public class TANEvent {

    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath())
            return;

        IThirst oldThirst = event.getOriginal().getCapability(TANCapabilities.THIRST, null);
        IThirst thirst = event.getEntityPlayer().getCapability(TANCapabilities.THIRST, null);

        ITemperature oldTemp = event.getOriginal().getCapability(TANCapabilities.TEMPERATURE, null);
        ITemperature temp = event.getEntityPlayer().getCapability(TANCapabilities.TEMPERATURE, null);

        if (oldThirst == null || thirst == null) {
            return;
        }

        if (oldTemp == null || temp == null) {
            return;
        }

        if (SKTANConfig.keep_thirst) {
            int value = Math.max(SKTANConfig.lowest_thirst, oldThirst.getThirst());
            thirst.setThirst(value);
        }
        if (SKTANConfig.keep_hydration) {
            float value = Math.max(SKTANConfig.lowest_hydration, oldThirst.getHydration());
            thirst.setHydration(value);
        }
        if (SKTANConfig.keep_temperature) {
            temp.setTemperature(oldTemp.getTemperature());
        }
    }


    /**
     * This has to be done since ToughAsNails
     * somehow doesn't update stats all the time.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onJoin(PlayerLoggedInEvent event) {
        if (SKTANConfig.keep_thirst || SKTANConfig.keep_hydration) {
            readAndSyncData(TANCapabilities.THIRST, event.player);
        }

        if (SKTANConfig.keep_temperature) {
            readAndSyncData(TANCapabilities.TEMPERATURE, event.player);
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerRespawnEvent event) {
        if (SKTANConfig.keep_thirst || SKTANConfig.keep_hydration) {
            readAndSyncData(TANCapabilities.THIRST, event.player);
        }

        if (SKTANConfig.keep_temperature) {
            readAndSyncData(TANCapabilities.TEMPERATURE, event.player);
        }
    }


    @SubscribeEvent
    public void onWorldChange(PlayerChangedDimensionEvent event) {
        if (SKTANConfig.keep_thirst || SKTANConfig.keep_hydration) {
            readAndSyncData(TANCapabilities.THIRST, event.player);
        }

        if (SKTANConfig.keep_temperature) {
            readAndSyncData(TANCapabilities.TEMPERATURE, event.player);
        }
    }

    private <T extends IPlayerStat> void readAndSyncData(Capability<T> capability, EntityPlayer player) {
        T data = player.getCapability(capability, null);
        if (data != null) {
            capability.getStorage().readNBT(capability, data, null, player.getEntityData().getCompoundTag(capability.getName()));
            NBTTagCompound nbt = (NBTTagCompound) capability.getStorage().writeNBT(capability, data, null);
            StatsKeeper.networkWrapper.sendTo(new StatsMessageTAN(capability, nbt), (EntityPlayerMP) player);
        }
    }
}