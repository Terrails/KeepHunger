package terrails.statskeeper.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import terrails.statskeeper.StatsKeeper;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.IThirst;

public class ThirstMessage implements IMessage {

    int thirst;

    public ThirstMessage() {}

    public ThirstMessage(int thirst) {
        this.thirst = thirst;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        thirst = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(thirst);
    }

    public static class MessageHandler implements IMessageHandler<ThirstMessage, IMessage> {
        @Override
        public IMessage onMessage(ThirstMessage message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT)
                return null;

            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                EntityPlayer player = StatsKeeper.proxy.getEntityPlayer();
                if (player != null) {
                    final IThirst thirst = player.getCapability(TANCapabilities.THIRST, null);
                    if (thirst != null) {
                        thirst.setThirst(message.thirst);
                    }
                }
            });
            return null;
        }
    }
}