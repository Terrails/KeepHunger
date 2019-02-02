package terrails.statskeeper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.fabricmc.fabric.events.ServerEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import terrails.statskeeper.api.SKPotions;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.effect.NoAppetiteEffect;
import terrails.statskeeper.event.InteractEvent;
import terrails.statskeeper.event.PlayerEvent;
import terrails.statskeeper.event.handler.BasicHandler;
import terrails.statskeeper.event.handler.PlayerHealthHandler;
import terrails.statskeeper.event.handler.PlayerHungerHandler;

public class StatsKeeper implements ModInitializer {

    public static final String MOD_ID = "statskeeper";
    public static final String MOD_NAME = "Stats Keeper";

    @Override
    public void onInitialize() {
        SKConfig.initialize();
        SKPotions.NO_APPETITE = Registry.register(Registry.STATUS_EFFECT, new Identifier(StatsKeeper.MOD_ID, "no_appetite"), new NoAppetiteEffect());
        StatsKeeper.initializeEvents();
    }

    private static void initializeEvents() {
        PlayerEvent.PLAYER_CLONE.register(PlayerHealthHandler.playerCloneEvent);
        PlayerEvent.PLAYER_CLONE.register(PlayerHungerHandler.playerCloneEvent);
        PlayerEvent.PLAYER_CLONE.register(BasicHandler.playerCloneEvent);
        PlayerEvent.PLAYER_JOIN.register(PlayerHealthHandler.playerJoinEvent);
        PlayerEvent.PLAYER_RESPAWN.register(PlayerHungerHandler.playerRespawnEvent);
        InteractEvent.PLAYER_USE_FINISHED.register(PlayerHealthHandler.itemUseFinishedEvent);
        PlayerInteractionEvent.INTERACT_BLOCK.register(PlayerHungerHandler.blockInteractEvent);
        PlayerInteractionEvent.INTERACT_ITEM.register(PlayerHungerHandler.itemInteractEvent);
        PlayerInteractionEvent.INTERACT_ITEM.register(PlayerHealthHandler.itemInteractEvent);
        ServerEvent.START.register((MinecraftServer server) -> SKConfig.initialize());
    }
}
