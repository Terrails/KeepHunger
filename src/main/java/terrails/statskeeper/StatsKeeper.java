package terrails.statskeeper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import terrails.statskeeper.api.event.*;
import terrails.statskeeper.api.potion.SKPotions;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.effect.NoAppetiteEffect;
import terrails.statskeeper.handler.BasicHandler;
import terrails.statskeeper.handler.PlayerHealthHandler;
import terrails.statskeeper.handler.PlayerHungerHandler;

public class StatsKeeper implements ModInitializer {

    public static final String MOD_ID = "statskeeper";
    public static final String MOD_NAME = "Stats Keeper";

    @Override
    public void onInitialize() {
        SKPotions.NO_APPETITE = Registry.register(Registry.STATUS_EFFECT, new Identifier(StatsKeeper.MOD_ID, "no_appetite"), new NoAppetiteEffect());
        StatsKeeper.initializeEvents();
    }

    private static void initializeEvents() {
        PlayerCloneCallback.EVENT.register(PlayerHealthHandler.playerCloneEvent);
        PlayerCloneCallback.EVENT.register(PlayerHungerHandler.playerCloneEvent);
        PlayerCloneCallback.EVENT.register(BasicHandler.playerCloneEvent);
        PlayerJoinCallback.EVENT.register(PlayerHealthHandler.playerJoinEvent);
        PlayerRespawnCallback.EVENT.register(PlayerHungerHandler.playerRespawnEvent);
        PlayerUseFinishedCallback.EVENT.register(PlayerHealthHandler.itemUseFinishedEvent);
        UseBlockCallback.EVENT.register(PlayerHungerHandler.blockInteractEvent);
        UseItemCallback.EVENT.register(PlayerHungerHandler.itemInteractEvent);
        UseItemCallback.EVENT.register(PlayerHealthHandler.itemInteractEvent);
        ServerStartCallback.EVENT.register((MinecraftServer server) -> SKConfig.initialize());
    }
}
