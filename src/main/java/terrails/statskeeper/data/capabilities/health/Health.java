package terrails.statskeeper.data.capabilities.health;

import com.google.common.base.CharMatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServerMulti;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import terrails.statskeeper.Constants;
import terrails.statskeeper.api.capabilities.health.IHealth;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.data.world.CustomWorldData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

//@Mod.EventBusSubscriber
public class Health implements IHealth {
/*
    private static UUID STATS_KEEPER_HEALTH_UUID = Constants.STATS_KEEPER_HEALTH_UUID;

    /**
     * Enable debugging of {@link Health#firstJoin} {@link Health#onTick} {@link Health#onClone}
     *
    private static boolean ENABLE_DEBUGGING = false;

    private static IAttributeInstance getAttribute(EntityPlayer player) {
        if (player != null)
            return player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        return null;
    }
    private static void removeModifier(EntityPlayer entity, UUID modifierUUID) {
        if (entity != null) {
            AttributeModifier modifier = getAttribute(entity).getModifier(modifierUUID);
            if (modifier != null) {
                getAttribute(entity).removeModifier(modifier);
            }
        }
    }

    private static void setMaxHealth(EntityPlayer player, double health) {
        if (player != null) {
            removeModifier(player, STATS_KEEPER_HEALTH_UUID);
            getAttribute(player).applyModifier(new AttributeModifier(STATS_KEEPER_HEALTH_UUID, "StatsKeeper HP", health - player.getMaxHealth(), 0));
            player.setHealth(player.getMaxHealth());
        }
    }
    private static void addMaxHealth(EntityPlayer player, double health) {
        if (player != null) {
        //    removeModifier(player, STATS_KEEPER_HEALTH_UUID);
            getAttribute(player).applyModifier(new AttributeModifier(STATS_KEEPER_HEALTH_UUID, "StatsKeeper HP", player.getMaxHealth() + health, 0));
            player.setHealth(player.getMaxHealth());
        }
    }

    private static List<EntityPlayerMP> getPlayerListIntegrated() {
        Minecraft minecraft = Minecraft.getMinecraft();
        IntegratedServer integratedServer = minecraft.getIntegratedServer();
        boolean gamePaused = minecraft.getConnection() != null && minecraft.isGamePaused();

        if (!gamePaused && integratedServer != null) {
            return integratedServer.getPlayerList().getPlayers();
        } else return new ArrayList<>();
    }
    private static List<EntityPlayerMP> getPlayerListServer() {
        MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (minecraftServer != null) {
            return minecraftServer.getPlayerList().getPlayers();
        } else return new ArrayList<>();
    }

    public static Item getItem(String item) {
        String one = item.contains(";") ? item.substring(0, item.indexOf(";")) : item.contains(",") ? item.substring(0, item.indexOf(",")) : item;
        return Item.getByNameOrId(one);
    }
    public static int getItemMeta(String item) {
        String one = item.contains(";") ? item.substring(item.indexOf(";")) : "0";
        String two = one.contains(",") ? one.substring(0, one.indexOf(",")) : one;
        int meta = Integer.parseInt(CharMatcher.digit().retainFrom(two));
        return meta;
    }
    public static int getItemAddedHealth(String item) {
        return item.contains(", ") ? Integer.parseInt(CharMatcher.digit().retainFrom(item.substring(item.indexOf(",")))) : 2;
    }

    @SubscribeEvent
    public static void addHealthOnRightClick(LivingEntityUseItemEvent.Start event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            World world = player.getEntityWorld();
            IHealth health = player.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
            CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());
            if (ConfigHandler.healthSystem && worldData.getMaxHealth() != 0 && ConfigHandler.maxHealth > player.getMaxHealth() && health != null) {
                for (String item : ConfigHandler.itemNameArray) {
                    worldData.markDirty();
                    Item theItem = getItem(item);
                    int meta = getItemMeta(item);
                    int healthAmount = getItemAddedHealth(item);

                    if (theItem != null && theItem == event.getItem().getItem() && meta == event.getItem().getMetadata() && event.getDuration() == 0) {
                        if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("The item name is: " + theItem + ", and the meta is: " + meta);
                        if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("The health amount is: " + healthAmount);

                        if (worldData.getMaxHealth() <= player.getMaxHealth() + healthAmount && !world.isRemote) {
                            if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("Added Health Before Using: " + health.getAddedHealth());
                            health.setAddedHealth(worldData.getMaxHealth() - getAttribute(player).getBaseValue());
                            setMaxHealth(player, worldData.getMaxHealth());
                            if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("Added Health After Using: " + health.getAddedHealth());
                        } else
                        if (!world.isRemote) {
                            if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("Added Health Before Using: " + health.getAddedHealth());
                            setMaxHealth(player, player.getMaxHealth() + healthAmount);
                            health.setAddedHealth(player.getMaxHealth() - getAttribute(player).getBaseValue());
                            if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("Added Health After Using: " + health.getAddedHealth());
                        }

                        if (world.isRemote) {
                            Random rand = new Random();
                            for (int particleCount = 0; particleCount <= 10; ++particleCount)
                                world.spawnParticle(EnumParticleTypes.HEART, player.posX + (rand.nextDouble() - 0.4D) * (double) player.width, player.posY + rand.nextDouble() * (double) player.height - player.getYOffset(), player.posZ + (rand.nextDouble() - 0.4D) * (double) player.width, 1.0D, 0.2D, 1.0D);
                            world.playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.75F, 1.0F);
                        }
                        if (!world.isRemote)
                            event.getItem().shrink(1);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void addHealthOnUsedItem(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            World world = player.getEntityWorld();
            IHealth health = player.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
            CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());
        if (ConfigHandler.healthSystem && worldData.getMaxHealth() != 0 && ConfigHandler.maxHealth > player.getMaxHealth() && health != null) {
                for (String item : ConfigHandler.itemNameArray) {
                    worldData.markDirty();
                    Item theItem = getItem(item);
                    int meta = getItemMeta(item);
                    int healthAmount = getItemAddedHealth(item);

                    if (theItem != null && theItem == event.getItem().getItem() && meta == event.getItem().getMetadata()) {
                        if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("The item name is: " + theItem + ", and the meta is: " + meta);
                        if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("The health amount is: " + healthAmount);

                        if (worldData.getMaxHealth() <= player.getMaxHealth() + healthAmount && !world.isRemote) {
                            if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("Added Health Before Using: " + health.getAddedHealth());
                            health.setAddedHealth(worldData.getMaxHealth() - getAttribute(player).getBaseValue());
                            setMaxHealth(player, worldData.getMaxHealth());
                            if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("Added Health After Using: " + health.getAddedHealth());
                        } else
                        if (!world.isRemote) {
                            if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("Added Health Before Using: " + health.getAddedHealth());
                            health.setAddedHealth(player.getMaxHealth() + healthAmount - getAttribute(player).getBaseValue());
                            setMaxHealth(player, player.getMaxHealth() + healthAmount);
                            if (ENABLE_DEBUGGING) Constants.getLogger("RightClickItem").info("Added Health After Using: " + health.getAddedHealth());
                        }

                        if (world.isRemote) {
                            Random rand = new Random();
                            for (int particleCount = 0; particleCount <= 10; ++particleCount)
                                world.spawnParticle(EnumParticleTypes.HEART, player.posX + (rand.nextDouble() - 0.4D) * (double) player.width, player.posY + rand.nextDouble() * (double) player.height - player.getYOffset(), player.posZ + (rand.nextDouble() - 0.4D) * (double) player.width, 1.0D, 0.2D, 1.0D);
                            world.playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.75F, 1.0F);
                        }
                        if (!world.isRemote)
                            event.getItem().shrink(1);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void addHealthOnItem(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();
        IHealth health = player.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
        CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());
        if (ConfigHandler.healthSystem && worldData.getMaxHealth() != 0 && ConfigHandler.maxHealth > player.getMaxHealth() && health != null) {
            for (String item : ConfigHandler.itemNameArray) {

                Item theItem = getItem(item);
                int meta = getItemMeta(item);
                int healthAmount = getItemAddedHealth(item);

                if (theItem != null && event.getItemStack().getItem() == theItem && event.getItemStack().getItemDamage() == meta) {
                    if (worldData.getMaxHealth() <= player.getMaxHealth() + healthAmount && !world.isRemote) {
                        event.getEntityPlayer().setActiveHand(EnumHand.MAIN_HAND);
                    } else if (!world.isRemote) {
                        event.getEntityPlayer().setActiveHand(EnumHand.MAIN_HAND);
                    }
                }
            }
        } else {
            event.setCanceled(false);
        }
    }


    @SubscribeEvent
    public static void firstJoin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        IHealth health = player.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
        CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());

        if (health != null && worldData != null && ConfigHandler.healthSystem) {
            worldData.markDirty();

            if (!health.getHasAddedHealth()) {
                if (ENABLE_DEBUGGING) Constants.getLogger("PlayerLoggedInEvent").info("Old Starting Health: " + health.getAddedHealth());
                Constants.playerMessage(player, "Your health was changed to: " + (int) worldData.getMaxHealth());
                health.setAddedHealth(worldData.getMaxHealth() - player.getMaxHealth());
                worldData.setOldMaxHealth(worldData.getMaxHealth());
                setMaxHealth(player, worldData.getMaxHealth());
                if (ENABLE_DEBUGGING) Constants.getLogger("PlayerLoggedInEvent").info("New Starting Health: " + health.getAddedHealth());
                health.setHasAddedHealth(true);
            }
        }
    }
    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        List<EntityPlayerMP> players = event.side == Side.CLIENT ? getPlayerListIntegrated() : getPlayerListServer();
        if (event.phase == TickEvent.Phase.END) {
            for (EntityPlayerMP player : players) {

                IHealth health = player.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
                CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());

                if (health != null && worldData != null) {
                    if (!ConfigHandler.healthSystem) {
                        removeModifier(player, STATS_KEEPER_HEALTH_UUID);
                        health.setHasAddedHealth(false);
                        if (ENABLE_DEBUGGING) Constants.getLogger("ServerTickEvent").info("Health System is disabled");
                    } else {
                        worldData.markDirty();
                        if (worldData.getOldMaxHealth() != worldData.getMaxHealth()) {
                            removeModifier(player, STATS_KEEPER_HEALTH_UUID);
                            health.setAddedHealth(worldData.getMaxHealth() - player.getMaxHealth());
                            setMaxHealth(player, worldData.getMaxHealth());
                            worldData.setOldMaxHealth(worldData.getMaxHealth());
                            health.setHasAddedHealth(true);
                            if (ENABLE_DEBUGGING) Constants.getLogger("ServerTickEvent").info("Added Health: " + health.getAddedHealth());
                        }
                        else if (!health.getHasAddedHealth()) {
                            removeModifier(player, STATS_KEEPER_HEALTH_UUID);
                            health.setAddedHealth(worldData.getMaxHealth() - player.getMaxHealth());
                            setMaxHealth(player, worldData.getMaxHealth());
                            worldData.setOldMaxHealth(worldData.getMaxHealth());
                            health.setHasAddedHealth(true);
                            if (ENABLE_DEBUGGING) Constants.getLogger("ServerTickEvent").info("Added Health: " + health.getAddedHealth());
                        }
                        else if (player.getMaxHealth() < worldData.getMinHealth()) {
                            setMaxHealth(player, worldData.getMinHealth());
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (ConfigHandler.healthSystem) {
            EntityPlayer oldPlayer = event.getOriginal();
            EntityPlayer newPlayer = event.getEntityPlayer();
            IHealth health = oldPlayer.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
            IHealth newHealth = newPlayer.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
            CustomWorldData worldData = CustomWorldData.get(newPlayer.getEntityWorld());
            if (health != null && worldData != null && newHealth != null) {
                worldData.markDirty();
                if (worldData.getMaxHealth() != 0 && worldData.getMinHealth() == 0 && worldData.getRemoveHealth() == 0) {
                    setMaxHealth(newPlayer, health.getAddedHealth() + getAttribute(newPlayer).getBaseValue());
                }
                else if (worldData.getMaxHealth() != 0 && worldData.getMinHealth() != 0 && worldData.getRemoveHealth() != 0) {
                    if (ENABLE_DEBUGGING) Constants.getLogger("Player Respawning").info("Added Health Before Death: " + health.getAddedHealth());

                    double health1 = newHealth.getAddedHealth() - worldData.getRemoveHealth();
                    double health2 = health1 <= worldData.getMinHealth()-newPlayer.getMaxHealth() ? worldData.getMinHealth()-newPlayer.getMaxHealth() : health1;
                    newHealth.setAddedHealth(health2);

                    if (ENABLE_DEBUGGING) Constants.getLogger("Player Respawning").info("Added Health After Death: " + newHealth.getAddedHealth());

                    setMaxHealth(newPlayer, health2 + getAttribute(newPlayer).getBaseValue());
                    Constants.playerMessage(newPlayer, "Oops, someone was evil and stole " + (int) worldData.getRemoveHealth() + " health from you!");
                }
            }
        }
    }
*/
    private double addedHealth;
    private boolean hasAddedHealth;
    private String lastItemName;

    @Override
    public void setHasAddedHealth(boolean maxHealth) {
        hasAddedHealth = maxHealth;
    }
    @Override
    public boolean getHasAddedHealth() {
        return hasAddedHealth;
    }

    @Override
    public void setAddedHealth(double health) {
        addedHealth = health;
    }
    @Override
    public double getAddedHealth() {
        return addedHealth;
    }

    @Override
    public void setLastItemName(String name) {
        lastItemName = name;
    }
    @Override
    public String getLastItemName() {
        if (lastItemName != null)
            return lastItemName;
        return "none";
    }
}