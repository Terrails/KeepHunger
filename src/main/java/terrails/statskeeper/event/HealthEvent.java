package terrails.statskeeper.event;

import com.google.common.base.CharMatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import terrails.statskeeper.Constants;
import terrails.statskeeper.api.capabilities.health.IHealth;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.data.capabilities.health.CapabilityHealth;
import terrails.statskeeper.data.world.CustomWorldData;
import terrails.statskeeper.potion.ModPotions;
import terrails.terracore.helper.PlayerHelper;
import terrails.terracore.helper.PlayerStats;
import terrails.terracore.helper.StringHelper;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public class HealthEvent {

    private static UUID STATS_KEEPER_HEALTH_UUID = Constants.STATS_KEEPER_HEALTH_UUID;

    /**
     * Enable debugging of these events:
     * {@link HealthEvent#firstJoin} {@link HealthEvent#onTick} {@link HealthEvent#onClone}
     * {@link HealthEvent#addHealthOnRightClick} {@link HealthEvent#addHealthOnUsedItem} {@link HealthEvent#addHealthOnItem}
     */
    private static boolean ENABLE_DEBUGGING = false;

    public static Item getItem(String item) {
        String one = item.contains(";") ? StringHelper.getSubstringBefore(item, ";") : item.contains(",") ? StringHelper.getSubstringBefore(item, ",") : item;
        return Item.getByNameOrId(one);
    }
    public static int getItemMeta(String item) {
        String one = item.contains(";") ? StringHelper.getSubstringAfter(item ,";") : "0";
        String two = one.contains(",") ? StringHelper.getSubstringBefore(item, ",") : one;
        int meta = Integer.parseInt(CharMatcher.digit().retainFrom(two));
        return meta;
    }
    public static int getItemAddedHealth(String item) {
        return item.contains(", ") ? Integer.parseInt(CharMatcher.digit().retainFrom(StringHelper.getSubstringAfter(item, ","))) : 2;
    }

    @SubscribeEvent
    public static void firstJoin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        IHealth health = player.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
        CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());

        if (health != null && worldData != null && ConfigHandler.healthSystem) {
            worldData.markDirty();

            if (!health.getHasAddedHealth()) {
                if (ENABLE_DEBUGGING) Constants.getLogger("PlayerLoggedInEvent").info("Starting Health Before Setting: " + player.getMaxHealth());
                if (!ConfigHandler.startWithMinHealth) {
                    worldData.setOldMaxHealth(worldData.getMaxHealth());

                    health.setAddedHealth(worldData.getMaxHealth() - PlayerStats.getMaxHealthAttribute(player).getBaseValue());
                    PlayerStats.setMaxHealth(player, STATS_KEEPER_HEALTH_UUID, health.getAddedHealth());
                    health.setHasAddedHealth(true);
                } else {
                    worldData.setOldMinHealth(worldData.getMinHealth());

                    health.setAddedHealth(worldData.getMinHealth() - PlayerStats.getMaxHealthAttribute(player).getBaseValue());
                    PlayerStats.setMaxHealth(player, STATS_KEEPER_HEALTH_UUID, health.getAddedHealth());
                    health.setHasAddedHealth(true);
                }

                //Constants.playerMessage(player, "Your health was changed to: " + (int) worldData.getMaxHealth());

                if (ENABLE_DEBUGGING) Constants.getLogger("PlayerLoggedInEvent").info("Starting Health After Setting: " + player.getMaxHealth());
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClone(PlayerEvent.Clone event) {
        if (ConfigHandler.healthSystem) {
            EntityPlayer oldPlayer = event.getOriginal();
            EntityPlayer newPlayer = event.getEntityPlayer();
            IHealth oldHealth = oldPlayer.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
            IHealth newHealth = newPlayer.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
            CustomWorldData worldData = CustomWorldData.get(newPlayer.getEntityWorld());
            if (oldHealth != null && worldData != null && newHealth != null && oldHealth.getHasAddedHealth()) {
                worldData.markDirty();

                if (worldData.getMaxHealth() != 0 && worldData.getMinHealth() == 0 && worldData.getRemoveHealth() == 0) {
                    newHealth.setAddedHealth(worldData.getMaxHealth() - PlayerStats.getMaxHealthAttribute(newPlayer).getBaseValue());
                    PlayerStats.setMaxHealth(newPlayer, STATS_KEEPER_HEALTH_UUID, newHealth.getAddedHealth());
                } 
                
                if (worldData.getMaxHealth() != 0 && worldData.getMinHealth() != 0) {
                    if (ENABLE_DEBUGGING) Constants.getLogger("PlayerEvent.Clone").info("Added Health Before Death: " + oldHealth.getAddedHealth());

                    double removedHealth = oldHealth.getAddedHealth() - worldData.getRemoveHealth();
                    double addedHealth = removedHealth <= worldData.getMinHealth() - PlayerStats.getMaxHealthAttribute(newPlayer).getBaseValue() ? worldData.getMinHealth() - PlayerStats.getMaxHealthAttribute(newPlayer).getBaseValue() : removedHealth;
                    newHealth.setAddedHealth(addedHealth);
                    PlayerStats.setMaxHealth(newPlayer, STATS_KEEPER_HEALTH_UUID, newHealth.getAddedHealth());
                    if (worldData.getRemoveHealth() > 0) {
                        Constants.playerMessage(newPlayer, "Oops, someone was evil and stole " + (int) worldData.getRemoveHealth() + " health from you!");
                    }
                    if (ENABLE_DEBUGGING) Constants.getLogger("PlayerEvent.Clone").info("Added Health After Death: " + newHealth.getAddedHealth());
                }
            }
        }
    }

    @SubscribeEvent
    public static void addHealthOnRightClick(LivingEntityUseItemEvent.Start event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            World world = player.getEntityWorld();
            IHealth health = player.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
            CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());
            if (ConfigHandler.healthSystem && health != null && worldData != null) {
                worldData.markDirty();
                Item theItem = getItem(health.getLastItemName());
                int meta = getItemMeta(health.getLastItemName());
                int healthAmount = getItemAddedHealth(health.getLastItemName());

                if (theItem != null && event.getDuration() == 0) {
                    if (ENABLE_DEBUGGING)
                        Constants.getLogger("LivingEntityUseItemEvent.Start").info("ItemStack: " + theItem + ", Metadata: " + meta);
                    if (ENABLE_DEBUGGING)
                        Constants.getLogger("LivingEntityUseItemEvent.Start").info("Health Regained: " + healthAmount);

                    if (!world.isRemote && world instanceof WorldServer) {
                        if (worldData.getMaxHealth() <= player.getMaxHealth() + healthAmount) {
                            if (ENABLE_DEBUGGING)
                                Constants.getLogger("LivingEntityUseItemEvent.Start").info("Added Health Before Item: " + health.getAddedHealth());
                            health.setAddedHealth(worldData.getMaxHealth() - PlayerStats.getMaxHealthAttribute(player).getBaseValue());
                            PlayerStats.setMaxHealth(player, STATS_KEEPER_HEALTH_UUID, health.getAddedHealth());
                            event.getItem().shrink(1);
                            if (ENABLE_DEBUGGING)
                                Constants.getLogger("LivingEntityUseItemEvent.Start").info("Added Health After Item: " + health.getAddedHealth());
                        } else {
                            if (ENABLE_DEBUGGING)
                                Constants.getLogger("LivingEntityUseItemEvent.Start").info("Added Health Before Item: " + health.getAddedHealth());
                            health.setAddedHealth((player.getMaxHealth() + healthAmount) - PlayerStats.getMaxHealthAttribute(player).getBaseValue());
                            PlayerStats.setMaxHealth(player, STATS_KEEPER_HEALTH_UUID, health.getAddedHealth());
                            event.getItem().shrink(1);
                            if (ENABLE_DEBUGGING)
                                Constants.getLogger("LivingEntityUseItemEvent.Start").info("Added Health After Item: " + health.getAddedHealth());
                        }
                    } else if (!(worldData.getMaxHealth() >= player.getMaxHealth())) {
                        Random random = new Random();
                        for (int particles = 0; particles <= 10; particles++)
                            world.spawnParticle(EnumParticleTypes.HEART,
                                    player.posX + (random.nextDouble() - 0.4D) * (double) player.width,
                                    player.posY + random.nextDouble() * (double) player.height - player.getYOffset(),
                                    player.posZ + (random.nextDouble() - 0.4D) * (double) player.width,
                                    1.0D, 0.2D, 1.0D);
                        world.playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.75F, 1.0F);
                    }
                    health.setLastItemName("none");
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
            if (ConfigHandler.healthSystem && health != null && worldData != null && !health.getLastItemName().equals("none")) {
                worldData.markDirty();
                Item theItem = getItem(health.getLastItemName());
                int meta = getItemMeta(health.getLastItemName());
                int healthAmount = getItemAddedHealth(health.getLastItemName());
                if (theItem != null) {
                    if (ENABLE_DEBUGGING)
                        Constants.getLogger("LivingEntityUseItemEvent.Start").info("ItemStack: " + theItem + ", Metadata: " + meta);
                    if (ENABLE_DEBUGGING)
                        Constants.getLogger("LivingEntityUseItemEvent.Start").info("Health Regained: " + healthAmount);

                    if (!world.isRemote && world instanceof WorldServer) {
                        if (worldData.getMaxHealth() <= player.getMaxHealth() + healthAmount) {
                            if (ENABLE_DEBUGGING)
                                Constants.getLogger("LivingEntityUseItemEvent.Start").info("Added Health Before Item: " + health.getAddedHealth());
                            health.setAddedHealth(worldData.getMaxHealth() - PlayerStats.getMaxHealthAttribute(player).getBaseValue());
                            PlayerStats.setMaxHealth(player, STATS_KEEPER_HEALTH_UUID, health.getAddedHealth());
                            event.getItem().shrink(1);
                            if (ENABLE_DEBUGGING)
                                Constants.getLogger("LivingEntityUseItemEvent.Start").info("Added Health After Item: " + health.getAddedHealth());
                        } else {
                            if (ENABLE_DEBUGGING)
                                Constants.getLogger("LivingEntityUseItemEvent.Start").info("Added Health Before Item: " + health.getAddedHealth());
                            health.setAddedHealth((player.getMaxHealth() + healthAmount) - PlayerStats.getMaxHealthAttribute(player).getBaseValue());
                            PlayerStats.setMaxHealth(player, STATS_KEEPER_HEALTH_UUID, health.getAddedHealth());
                            event.getItem().shrink(1);
                            if (ENABLE_DEBUGGING)
                                Constants.getLogger("LivingEntityUseItemEvent.Start").info("Added Health After Item: " + health.getAddedHealth());
                        }
                    } else if (!(worldData.getMaxHealth() >= player.getMaxHealth())) {
                        Random random = new Random();
                        for (int particles = 0; particles <= 10; particles++)
                            world.spawnParticle(EnumParticleTypes.HEART,
                                    player.posX + (random.nextDouble() - 0.4D) * (double) player.width,
                                    player.posY + random.nextDouble() * (double) player.height - player.getYOffset(),
                                    player.posZ + (random.nextDouble() - 0.4D) * (double) player.width,
                                    1.0D, 0.2D, 1.0D);
                        world.playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.75F, 1.0F);
                    }
                    health.setLastItemName("none");
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
        if (ConfigHandler.healthSystem && health != null && worldData != null && !(player.getMaxHealth() >= worldData.getMaxHealth())) {
            for (String item : ConfigHandler.itemNameArray) {

                Item theItem = getItem(item);
                int meta = getItemMeta(item);

                if (theItem != null && event.getItemStack().getItem() == theItem && event.getItemStack().getItemDamage() == meta && !world.isRemote) {
                    if (event.getItemStack().getItemUseAction() != EnumAction.EAT || !event.getEntityPlayer().isPotionActive(ModPotions.getPotion("appetite"))) {
                        event.setCanceled(true);
                        health.setLastItemName(item);
                        event.getEntityPlayer().setActiveHand(EnumHand.MAIN_HAND);
                    }
                } else health.setLastItemName("none");
            }
        }
        else {
            event.setCanceled(false);
        }
    }


    @SubscribeEvent
    public static void onTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END) {
            for (EntityPlayer player : event.world.playerEntities) {

                IHealth health = player.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
                CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());

                if (health != null && worldData != null) {

                    if (ConfigHandler.healthSystem) {
                        worldData.markDirty();

                        if (!ConfigHandler.startWithMinHealth) {
                            if (worldData.getOldMaxHealth() != worldData.getMaxHealth() || !health.getHasAddedHealth()) {
                                health.setAddedHealth(worldData.getMaxHealth() - PlayerStats.getMaxHealthAttribute(player).getBaseValue());
                                worldData.setOldMaxHealth(worldData.getMaxHealth());
                                PlayerStats.setMaxHealth(player, STATS_KEEPER_HEALTH_UUID, health.getAddedHealth());
                                health.setHasAddedHealth(true);
                                if (ENABLE_DEBUGGING) Constants.getLogger("ServerTickEvent").info("Health has been changed to: " + health.getAddedHealth());
                            }
                        } else {
                            if (worldData.getOldMinHealth() != worldData.getMinHealth() || !health.getHasAddedHealth()) {
                                health.setAddedHealth(worldData.getMinHealth() - PlayerStats.getMaxHealthAttribute(player).getBaseValue());
                                worldData.setOldMinHealth(worldData.getMinHealth());
                                PlayerStats.setMaxHealth(player, STATS_KEEPER_HEALTH_UUID, health.getAddedHealth());
                                health.setHasAddedHealth(true);
                                if (ENABLE_DEBUGGING) Constants.getLogger("ServerTickEvent").info("Health has been changed to: " + health.getAddedHealth());
                            }
                        }
                    } else {
                        PlayerStats.removeMaxHealthModifier(player, STATS_KEEPER_HEALTH_UUID);
                        health.setHasAddedHealth(false);
                        if (ENABLE_DEBUGGING) Constants.getLogger("ServerTickEvent").info("Health System is disabled");
                    }
                }
            }
        }
    }
/*
    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        List<EntityPlayerMP> players = event.side == Side.CLIENT ? PlayerHelper.getPlayerListIntegrated() : PlayerHelper.getPlayerListServer();
        if (event.phase == TickEvent.Phase.END) {
            for (EntityPlayerMP player : players) {

                IHealth health = player.getCapability(CapabilityHealth.HEALTH_CAPABILITY, null);
                CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());

                if (health != null && worldData != null) {

                    if (ConfigHandler.healthSystem) {
                        worldData.markDirty();

                        if (worldData.getOldMaxHealth() != worldData.getMaxHealth() || !health.getHasAddedHealth()) {
                            health.setAddedHealth(worldData.getMaxHealth() - PlayerStats.getMaxHealthAttribute(player).getBaseValue());
                            worldData.setOldMaxHealth(worldData.getMaxHealth());
                            PlayerStats.setMaxHealth(player, STATS_KEEPER_HEALTH_UUID, health.getAddedHealth());
                            health.setHasAddedHealth(true);
                            if (ENABLE_DEBUGGING) Constants.getLogger("ServerTickEvent").info("Health has been changed to: " + health.getAddedHealth());
                        }
                    } else {
                        PlayerStats.removeMaxHealthModifier(player, STATS_KEEPER_HEALTH_UUID);
                        health.setHasAddedHealth(false);
                        if (ENABLE_DEBUGGING) Constants.getLogger("ServerTickEvent").info("Health System is disabled");
                    }
                }
            }
        }
    }
*/
}