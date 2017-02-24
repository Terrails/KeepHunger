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
import net.minecraftforge.fml.common.gameevent.TickEvent;
import terrails.keephunger.MainClass;
import terrails.keephunger.config.ConfigHandler;
import terrails.keephunger.packet.ThirstMessage;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.thirst.ThirstHandler;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class TANEvent{


    @Nullable
    public static IThirst getThirstStats(Entity entity) {
        if(entity.hasCapability(TANCapabilities.THIRST, null))
            return entity.getCapability(TANCapabilities.THIRST, null);
        return null;
    }

    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone player) {
        final IThirst originalPlayer = player.getOriginal().getCapability(TANCapabilities.THIRST, null);
        final IThirst entityPlayer = player.getEntityPlayer().getCapability(TANCapabilities.THIRST, null);
        entityPlayer.setThirst(originalPlayer.getThirst());


        //Using multiple isModLoaded name's for 1.9-1.11 compatibility
        //Currently not using because od debugging
/*
        if (original.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
        //    entityPlayer.setThirst(ConfigHandler.thirstAmount);

        } else if (original.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
       //     entityPlayer.setThirst(ConfigHandler.thirstAmount);

        } else if (ConfigHandler.thirst && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
      //      entityPlayer.setThirst(original.getThirst());

        } else if (ConfigHandler.thirst && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
       //     entityPlayer.setThirst(original.getThirst());

        } */
    }

    @SubscribeEvent
    public void playerRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event){
        //   final IThirst entityPlayer = event.player.getCapability(TANCapabilities.THIRST, null);
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        IThirst thirstData = ThirstHelper.getThirstData(event.player);
        MainClass.instance.sendTo(new ThirstMessage(1), player);
    }
}