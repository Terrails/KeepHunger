package terrails.statskeeper.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.config.configs.SKHealthConfig;
import terrails.statskeeper.config.configs.SKHungerConfig;

import java.nio.file.Path;

public class SKConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec CONFIG;

    public static BooleanValue KEEP_EXPERIENCE;
    public static BooleanValue DROP_EXPERIENCE;

    static {
        BUILDER.push("experience");

        KEEP_EXPERIENCE = BUILDER
                .comment("Make the player keep experience when respawning")
                .define("keepExperience", true);

        DROP_EXPERIENCE = BUILDER
                .comment("Make the player drop experience on death, \n" +
                        "make sure to disable this when using the keep option because of XP dupes")
                .define("dropExperience", false);

        BUILDER.pop();

        SKHungerConfig.init(BUILDER);
        SKHealthConfig.init(BUILDER);

        CONFIG = BUILDER.build();
    }

    @SubscribeEvent
    public static void configLoading(final ModConfig.Loading event) {
        if (!event.getConfig().getModId().equals(StatsKeeper.MOD_ID))
            return;

        SKHealthConfig.configReloading(event);
        StatsKeeper.LOGGER.debug("Loaded {} config file {}", StatsKeeper.MOD_ID, event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void configReloading(final ModConfig.ConfigReloading event) {
        if (!event.getConfig().getModId().equals(StatsKeeper.MOD_ID))
            return;

        SKHealthConfig.configReloading(event);
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
