package terrails.statskeeper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Constants {

    public static final Logger LOGGER = LogManager.getLogger(StatsKeeper.MOD_NAME);
    public static final UUID STATS_KEEPER_HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");

    public static Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }
    public static void playerMessage(EntityPlayer player, String message) {
        player.sendMessage(new TextComponentString("[" + TextFormatting.GREEN + "Stats Keeper" + TextFormatting.RESET + "] " + message));
    }
}
