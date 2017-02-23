package terrails.keephunger.event;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.keephunger.config.ConfigHandler;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;

import javax.annotation.Nullable;

public class TANEvent{

    @Nullable
    public static IThirst getThirstStats(Entity entity) {
        if(entity.hasCapability(TANCapabilities.THIRST, null))
            return entity.getCapability(TANCapabilities.THIRST, null);
        return null;
    }
/*
    @Nullable
    public static IThirst getThirst(final EntityLivingBase entity) {
        return entity.getCapability(TANCapabilities.THIRST, null);
    }
*/
    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone player) {
        final IThirst original = getThirstStats(player.getOriginal());
        final IThirst entityPlayer = getThirstStats(player.getEntityPlayer());

        //Using multiple isModLoaded id's for 1.9-1.11 compatibility

        if (original.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
            entityPlayer.setThirst(ConfigHandler.thirstAmount);

        } else if (original.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
            entityPlayer.setThirst(ConfigHandler.thirstAmount);

        } else if (ConfigHandler.thirst && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
            entityPlayer.setThirst(original.getThirst());

        } else if (ConfigHandler.thirst && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
            entityPlayer.setThirst(original.getThirst());
        }
    }

    @SubscribeEvent
    public void onRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event)
    {
        EntityPlayer entityPlayer = event.player;
        final IThirst player = getThirstStats(entityPlayer);

        IThirst thirstData = ThirstHelper.getThirstData(entityPlayer);
        thirstData.setThirst(player.getThirst());
    }
}