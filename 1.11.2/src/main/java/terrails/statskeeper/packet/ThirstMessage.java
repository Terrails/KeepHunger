package terrails.statskeeper.packet;


import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.IThirst;

public class ThirstMessage implements IMessage {

    int thirst;

    public ThirstMessage() {
    }

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
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    final IThirst entityPlayer = player.getCapability(TANCapabilities.THIRST, null);
                    if (entityPlayer != null) {
                        entityPlayer.setThirst(message.thirst);
                    }
                }
            });
            return null;
        }
    }
}