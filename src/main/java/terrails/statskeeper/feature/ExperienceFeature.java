package terrails.statskeeper.feature;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import terrails.statskeeper.api.event.PlayerCopyCallback;
import terrails.statskeeper.api.event.PlayerDropExperienceCallback;

public class ExperienceFeature extends Feature {

    private final static PropertyMirror<Boolean> keep;
    private final static PropertyMirror<Boolean> drop;

    final PlayerCopyCallback PLAYER_COPY = (ServerPlayerEntity player, ServerPlayerEntity oldPlayer, boolean isEnd) -> {
        if (!isEnd && keep.getValue()) {

            boolean keepInventory = player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
            if (!keepInventory) {
                player.totalExperience = oldPlayer.totalExperience;
                player.experienceLevel = oldPlayer.experienceLevel;
                player.experienceProgress = oldPlayer.experienceProgress;
            }
        }
    };

    final PlayerDropExperienceCallback DROP_EXPERIENCE = (PlayerEntity player) -> drop.getValue();

    @Override
    public void initializeEvents() {
        PlayerCopyCallback.EVENT.register(PLAYER_COPY);
        PlayerDropExperienceCallback.EVENT.register(DROP_EXPERIENCE);
    }

    @Override
    public String name() {
        return "experience";
    }

    @Override
    public void setupConfig(ConfigTreeBuilder tree) {
        configValue(tree, "keep_experience", keep, true,
                "Keep experience on respawn.");
        configValue(tree, "drop_experience", drop, false,
                "Drop experience on death. Make sure to disable this when using keep_experience because of experience dupes.");
    }

    static {
        keep = PropertyMirror.create(ConfigTypes.BOOLEAN);
        drop = PropertyMirror.create(ConfigTypes.BOOLEAN);
    }
}
