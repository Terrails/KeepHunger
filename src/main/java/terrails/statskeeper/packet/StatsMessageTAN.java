package terrails.statskeeper.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import terrails.statskeeper.StatsKeeper;
import toughasnails.api.stat.IPlayerStat;
import toughasnails.api.stat.PlayerStatRegistry;

public class StatsMessageTAN implements IMessage {

    private String capability;
    private NBTTagCompound compound;

    public StatsMessageTAN() {}

    public StatsMessageTAN(Capability<? extends IPlayerStat> capability, NBTTagCompound compound) {
        this.capability = capability.getName();
        this.compound = compound;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.capability = ByteBufUtils.readUTF8String(buf);
        this.compound = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.capability);
        ByteBufUtils.writeTag(buf, this.compound);
    }

    public static class MessageHandler implements IMessageHandler<StatsMessageTAN, IMessage> {
        @Override
        public IMessage onMessage(StatsMessageTAN message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT)
                return null;

            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                EntityPlayer player = StatsKeeper.proxy.getEntityPlayer();
                if (player != null) {

                    Capability<IPlayerStat> capability = (Capability<IPlayerStat>) PlayerStatRegistry.getCapability(message.capability);
                    if (capability == null)
                        return;

                    IPlayerStat stat = player.getCapability(capability, null);
                    capability.getStorage().readNBT(capability, stat, null, message.compound);
                }
            });
            return null;
        }
    }
}