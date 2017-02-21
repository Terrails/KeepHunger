package terrails.keephunger.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.keephunger.config.ConfigHandler;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.CapabilityProvider;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.core.ToughAsNails;
import toughasnails.handler.ExtendedStatHandler;
import toughasnails.thirst.ThirstHandler;

import javax.annotation.Nullable;

public class TANEvent{

    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone player) {
        final IThirst oldThirst = getThirst(player.getOriginal());
        final IThirst newThirst = getThirst(player.getEntityPlayer());

        if (oldThirst.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
            newThirst.setThirst(ConfigHandler.thirstAmount);
            newThirst.setHydration(oldThirst.getHydration());
        }
        else if (oldThirst.getThirst() <= ConfigHandler.thirstAmount && ConfigHandler.thirstBoolean && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
            newThirst.setThirst(ConfigHandler.thirstAmount);
            newThirst.setHydration(oldThirst.getHydration());
        }
        else if (ConfigHandler.thirst && Loader.isModLoaded("toughasnails") && player.isWasDeath()) {
                newThirst.setThirst(oldThirst.getThirst());
                newThirst.setHydration(oldThirst.getHydration());
        }
        else if (ConfigHandler.thirst && Loader.isModLoaded("ToughAsNails") && player.isWasDeath()) {
            newThirst.setThirst(oldThirst.getThirst());
            newThirst.setHydration(oldThirst.getHydration());
        }
    }


    @Nullable
    public static IThirst getThirst(final EntityLivingBase entity) {
        return entity.getCapability(TANCapabilities.THIRST, null);
    }
}
