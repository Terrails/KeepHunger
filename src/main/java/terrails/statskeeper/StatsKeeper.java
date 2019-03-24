package terrails.statskeeper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import terrails.statskeeper.api.event.*;
import terrails.statskeeper.api.effect.SKPotions;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.effect.NoAppetiteEffect;
import terrails.statskeeper.event.BaseStatHandler;
import terrails.statskeeper.event.PlayerHealthHandler;

import java.util.UUID;

public class StatsKeeper implements ModInitializer {

    public static final UUID HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");
    public static final String MOD_ID = "statskeeper";

    @Override
    public void onInitialize() {
        SKPotions.NO_APPETITE = Registry.register(Registry.STATUS_EFFECT, new Identifier(StatsKeeper.MOD_ID, "no_appetite"), new NoAppetiteEffect());
        StatsKeeper.initializeEvents();
    }

    private static void initializeEvents() {
        PlayerJoinCallback.EVENT.register(PlayerHealthHandler.PLAYER_JOIN);
        PlayerCopyCallback.EVENT.register(PlayerHealthHandler.PLAYER_COPY);
        PlayerUseFinishedCallback.EVENT.register(PlayerHealthHandler.ITEM_USE_FINISHED);
        UseItemCallback.EVENT.register(PlayerHealthHandler.ITEM_INTERACT);
        PlayerCopyCallback.EVENT.register(BaseStatHandler.PLAYER_COPY);
        PlayerRespawnCallback.EVENT.register(BaseStatHandler.PLAYER_RESPAWN);
        UseBlockCallback.EVENT.register(BaseStatHandler.BLOCK_INTERACT);
        UseItemCallback.EVENT.register(BaseStatHandler.ITEM_INTERACT);
        ServerStartCallback.EVENT.register((MinecraftServer server) -> SKConfig.initialize());
    }
}
