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
import terrails.keephunger.config.ConfigHandler;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.thirst.ThirstHandler;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class TANEvent{

    private int thirst;

    @Nullable
    public static IThirst getThirstStats(Entity entity) {
        if(entity.hasCapability(TANCapabilities.THIRST, null))
            return entity.getCapability(TANCapabilities.THIRST, null);
        return null;
    }
    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone player) {
        final IThirst original = getThirstStats(player.getOriginal());
        //Using multiple isModLoaded name's for 1.9-1.11 compatibility

        if (original.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
        //    entityPlayer.setThirst(ConfigHandler.thirstAmount);
            this.thirst = ConfigHandler.thirstAmount;

        } else if (original.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
       //     entityPlayer.setThirst(ConfigHandler.thirstAmount);
            this.thirst = ConfigHandler.thirstAmount;

        } else if (ConfigHandler.thirst && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
      //      entityPlayer.setThirst(original.getThirst());
            this.thirst = original.getThirst();

        } else if (ConfigHandler.thirst && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
       //     entityPlayer.setThirst(original.getThirst());
            this.thirst = original.getThirst();

        }
    }

    @SubscribeEvent
    public void playerRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event){
    //    final IThirst entityPlayer = getThirstStats(event.player);
        IThirst thirstData = ThirstHelper.getThirstData(event.player);
        thirstData.setThirst(this.thirst);
    }
}