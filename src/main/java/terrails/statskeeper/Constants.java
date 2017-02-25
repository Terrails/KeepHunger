package terrails.statskeeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {

    public static final String MODID = "stats_keeper";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "Stats Keeper";
    public static final String MCVERSION = "[1.9],[1.9.4],[1.10],[1.10.2],[1.11],[1.11.2]";
    public static final String GUIFACTORY = "terrails.statskeeper.config.ConfigFactoryGUI";
    public static final String CLIENT_PROXY = "terrails.statskeeper.proxies.ClientProxy";
    public static final String SERVER_PROXY = "terrails.statskeeper.proxies.ServerProxy";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
}
