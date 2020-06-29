package terrails.statskeeper.feature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ExperienceFeature extends Feature {

    private static ForgeConfigSpec.BooleanValue keep;
    private static ForgeConfigSpec.BooleanValue drop;

    @SubscribeEvent
    public void clone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && keep.get()) {
            PlayerEntity player = event.getPlayer();
            PlayerEntity oldPlayer = event.getOriginal();

            boolean keepInventory = player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
            if (!keepInventory) {
                player.experienceLevel = oldPlayer.experienceLevel;
                player.experienceTotal = oldPlayer.experienceTotal;
                player.experience = oldPlayer.experience;
                player.setScore(oldPlayer.getScore());
            }
        }
    }

    @SubscribeEvent
    public void drop(LivingExperienceDropEvent event) {
        if (!drop.get() && event.getEntity() instanceof PlayerEntity) {
            event.setCanceled(true);
        }
    }

    @Override
    public String name() {
        return "experience";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {
        keep = builder
                .comment("Make the player keep experience when respawning")
                .define("keepExperience", true);

        drop = builder
                .comment("Make the player drop experience on death, \n" +
                        "make sure to disable this when using the keep option because of XP dupes")
                .define("dropExperience", false);
    }
}
