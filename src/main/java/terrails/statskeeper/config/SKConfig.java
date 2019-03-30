package terrails.statskeeper.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import terrails.statskeeper.StatsKeeper;

import java.nio.file.Path;

public class SKConfig {

    static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec CONFIG;

    public static boolean KEEP_EXPERIENCE;
    public static boolean DROP_EXPERIENCE;

    static {
        BUILDER.push("experience");

        ConfigHandler.KEEP_EXPERIENCE = BUILDER
                .comment("Make the player keep experience when respawning")
                .define("keepExperience", true);

        ConfigHandler.DROP_EXPERIENCE = BUILDER
                .comment("Make the player drop experience on death, \n" +
                        "make sure to disable this when using the keep option because of XP dupes")
                .define("dropExperience", false);

        BUILDER.pop();

        SKHungerConfig.init();
        SKHealthConfig.init();

        CONFIG = BUILDER.build();
    }

    @SubscribeEvent
    public static void configLoading(final ModConfig.Loading event) {
        if (!event.getConfig().getModId().equals(StatsKeeper.MOD_ID))
            return;

        ConfigHandler.configLoading();
        StatsKeeper.LOGGER.debug("Loaded {} config file {}", StatsKeeper.MOD_ID, event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void configReloading(final ModConfig.ConfigReloading event) {
        if (!event.getConfig().getModId().equals(StatsKeeper.MOD_ID))
            return;

        ConfigHandler.configLoading();
        StatsKeeper.LOGGER.debug("Loaded {} config file {}", StatsKeeper.MOD_ID, event.getConfig().getFileName());
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        StatsKeeper.LOGGER.debug("Loading config file {}", path);

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        StatsKeeper.LOGGER.debug("Built TOML config for {}", path.toString());
        configData.load();
        StatsKeeper.LOGGER.debug("Loaded TOML config file {}", path.toString());
        spec.setConfig(configData);
    }

}
