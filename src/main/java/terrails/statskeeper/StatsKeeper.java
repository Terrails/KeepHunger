package terrails.statskeeper;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrails.statskeeper.api.effect.SKEffects;
import terrails.statskeeper.effect.NoAppetiteEffect;
import terrails.statskeeper.feature.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

public class StatsKeeper implements ModInitializer {

    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "statskeeper.json5");
    private static final JanksonValueSerializer CONFIG_SERIALIZER = new JanksonValueSerializer(false);
    private static ConfigBranch CONFIG_NODE;

    public static final UUID HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");

    public static final Logger LOGGER = LogManager.getLogger("Stats Keeper");
    public static final String MOD_ID = "statskeeper";

    private static final Feature[] FEATURES = {
            new ExperienceFeature(),
            new HungerFeature(),
            new HealthFeature()
    };

    @Override
    public void onInitialize() {
        SKEffects.NO_APPETITE = Registry.register(Registry.STATUS_EFFECT, new Identifier(StatsKeeper.MOD_ID, "no_appetite"), new NoAppetiteEffect());
        StatsKeeper.setupConfig();
        Arrays.stream(FEATURES).filter(Feature::canLoad).forEach(Feature::initializeEvents);
    }

    private static void setupConfig() {
        ConfigTreeBuilder tree = ConfigTree.builder();
        Arrays.stream(FEATURES).filter(Feature::canLoadConfig).forEach(feature -> {
            ConfigTreeBuilder branch = tree.fork(feature.name());
            feature.setupConfig(branch);
            branch.build();
        });
        CONFIG_NODE = tree.build();
        StatsKeeper.setupConfigFile();
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> StatsKeeper.setupConfigFile());
    }

    static void setupConfigFile() {
        boolean recreate = false;
        while (true) {
            try {
                if (!CONFIG_FILE.exists() || recreate) {
                    FiberSerialization.serialize(CONFIG_NODE, Files.newOutputStream(CONFIG_FILE.toPath()), CONFIG_SERIALIZER);
                    LOGGER.info("Successfully created the config file in '{}'", CONFIG_FILE.toString());
                    break;
                } else {
                    try {
                        FiberSerialization.deserialize(CONFIG_NODE, Files.newInputStream(CONFIG_FILE.toPath()), CONFIG_SERIALIZER);
                        FiberSerialization.serialize(CONFIG_NODE, Files.newOutputStream(CONFIG_FILE.toPath()), CONFIG_SERIALIZER);
                        break;
                    } catch (ValueDeserializationException e) {
                        String fileName = ("statskeeper-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".json5");
                        LOGGER.error("Found a syntax error in the config.");
                        if (CONFIG_FILE.renameTo(new File(CONFIG_FILE.getParent(), fileName))) {
                            LOGGER.info("Config file successfully renamed to '{}'.", fileName);
                        }
                        recreate = true;
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
