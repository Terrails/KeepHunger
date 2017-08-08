package terrails.statskeeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Constants {

    public static final String MOD_ID = "stats_keeper";
    public static final String MOD_NAME = "Stats Keeper";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String TERRACORE_VERSION = "2.0.0";
    public static final String MC_VERSION = "[1.12]";
    public static final String GUI_FACTORY = "terrails.statskeeper.config.ConfigFactoryGUI";
    public static final String CLIENT_PROXY = "terrails.statskeeper.proxies.ClientProxy";
    public static final String SERVER_PROXY = "terrails.statskeeper.proxies.ServerProxy";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final UUID STATS_KEEPER_HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");
}
