package terrails.statskeeper.api.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

public class PlayerHelper {

    public static List<EntityPlayerMP> getPlayerListIntegrated() {
        Minecraft minecraft = Minecraft.getMinecraft();
        IntegratedServer integratedServer = minecraft.getIntegratedServer();
        boolean gamePaused = minecraft.getConnection() != null && minecraft.isGamePaused();

        if (!gamePaused && integratedServer != null) {
            return integratedServer.getPlayerList().getPlayers();
        } else return new ArrayList<>();
    }
    public static List<EntityPlayerMP> getPlayerListServer() {
        MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (minecraftServer != null) {
            return minecraftServer.getPlayerList().getPlayers();
        } else return new ArrayList<>();
    }
}
