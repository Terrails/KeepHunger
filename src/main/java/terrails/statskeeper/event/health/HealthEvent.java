package terrails.statskeeper.event.health;

import com.google.common.base.CharMatcher;
import jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.StringUtils;
import scala.xml.Null;
import terrails.statskeeper.Constants;
import terrails.statskeeper.config.ConfigHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class HealthEvent {

    private boolean isTANLoaded = Loader.isModLoaded("ToughAsNails") || Loader.isModLoaded("toughasnails");
    private HealthUtil util = HealthUtil.INSTANCE;
    private int oldMaxConfigHealthClone;
    private int oldMaxConfigHealthJoin;
    private int oldMinConfigHealthJoin;

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void addHealthOnItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (ConfigHandler.healthSystem) {
            if (ConfigHandler.maxHealth != 0) {
                for (String item : ConfigHandler.itemNameArray) {
                    if (ConfigHandler.maxHealth > event.getEntityPlayer().getMaxHealth()) {

                        if (item.contains(", ")) {
                            String name = StringUtils.substringBefore(item, ", ");
                            String theNumber = StringUtils.substringAfter(item, ", ");
                            String onlyNumber = CharMatcher.DIGIT.retainFrom(theNumber);
                            int integer = Integer.parseInt(onlyNumber);

                            Item theItem = Item.getByNameOrId(name);
                            if (theItem != null && new ItemStack(theItem) == event.getItemStack() && event.getHand() == EnumHand.MAIN_HAND) {

                                if (ConfigHandler.maxHealth <= event.getEntityPlayer().getMaxHealth() + integer) {
                                    util.setMaxHealth(event.getEntityPlayer(), ConfigHandler.maxHealth);
                                } else {
                                    util.setMaxHealth(event.getEntityPlayer(), event.getEntityPlayer().getMaxHealth() + integer);
                                }
                                event.getItemStack().shrink(1);
                            }
                        } else {
                            Item theItem = Item.getByNameOrId(item);
                            if (theItem != null && event.getHand() == EnumHand.MAIN_HAND && theItem == event.getItemStack().getItem()) {
                                if (ConfigHandler.maxHealth <= event.getEntityPlayer().getMaxHealth() + 2) {
                                    util.setMaxHealth(event.getEntityPlayer(), ConfigHandler.maxHealth);
                                } else {
                                    util.setMaxHealth(event.getEntityPlayer(), event.getEntityPlayer().getMaxHealth() + 2);
                                }
                                event.getItemStack().shrink(1);
                            }
                        }
                    } else {
                        String name = StringUtils.substringBefore(item, ", ");
                        Item theItem = Item.getByNameOrId(name);
                        if (theItem != null && theItem == event.getItemStack().getItem()) {
                            event.setCanceled(true);
                        }
                    }
                }
            } else {
                for (String item : ConfigHandler.itemNameArray) {
                    if (20 > event.getEntityPlayer().getMaxHealth()) {

                        if (item.contains(", ")) {
                            String name = StringUtils.substringBefore(item, ", ");
                            String theNumber = StringUtils.substringAfter(item, ", ");
                            String onlyNumber = CharMatcher.DIGIT.retainFrom(theNumber);
                            int integer = Integer.parseInt(onlyNumber);

                            Item theItem = Item.getByNameOrId(name);
                            if (theItem != null && new ItemStack(theItem) == event.getItemStack() && event.getHand() == EnumHand.MAIN_HAND) {

                                if (20 <= event.getEntityPlayer().getMaxHealth() + integer) {
                                    util.setMaxHealth(event.getEntityPlayer(), 20);
                                } else {
                                    util.setMaxHealth(event.getEntityPlayer(), event.getEntityPlayer().getMaxHealth() + integer);
                                }
                                event.getItemStack().shrink(1);
                            }
                        } else {
                            Item theItem = Item.getByNameOrId(item);
                            if (theItem != null && event.getHand() == EnumHand.MAIN_HAND && theItem == event.getItemStack().getItem()) {
                                if (20 <= event.getEntityPlayer().getMaxHealth() + 2) {
                                    util.setMaxHealth(event.getEntityPlayer(), 20);
                                } else {
                                    util.setMaxHealth(event.getEntityPlayer(), event.getEntityPlayer().getMaxHealth() + 2);
                                }
                                event.getItemStack().shrink(1);
                            }
                        }
                    } else {
                        String name = StringUtils.substringBefore(item, ", ");
                        Item theItem = Item.getByNameOrId(name);
                        if (theItem != null && theItem == event.getItemStack().getItem()) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void setHealthOnTick(TickEvent.ServerTickEvent event) {
        if (ConfigHandler.healthSystem) {
            Minecraft minecraft = Minecraft.getMinecraft();
            IntegratedServer integratedServer = minecraft.getIntegratedServer();

            if (event.phase == TickEvent.Phase.END && integratedServer != null) {
                boolean gamePaused = Minecraft.getMinecraft().getConnection() != null && minecraft.isGamePaused();

                if (!gamePaused && minecraft.world != null) {

                    //This is checked before the difficulty is actually changed to make the two match in IntegratedServer's tick()
                    List<EntityPlayerMP> players = integratedServer.getPlayerList().getPlayers();

                    //Update the modifiers of all the connected players
                    for (EntityPlayerMP player : players) {

                        IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);

                        int minHealth = ConfigHandler.minHealth;
                        int maxHealth = ConfigHandler.maxHealth;
                        int removeHealth = ConfigHandler.removedHealthOnDeath;

                        if (maxHealth != 0 && oldMaxConfigHealthJoin != maxHealth) {
                            util.setMaxHealth(player, maxHealth);
                            oldMaxConfigHealthJoin = maxHealth;
                            oldMinConfigHealthJoin = minHealth;
                        } else if (minHealth != 0 && oldMinConfigHealthJoin != minHealth && player.getMaxHealth() == oldMinConfigHealthJoin && minHealth > oldMinConfigHealthJoin) {
                            util.setMaxHealth(player, minHealth);
                            oldMaxConfigHealthJoin = maxHealth;
                            oldMinConfigHealthJoin = minHealth;
                        } else if (minHealth > maxHealth) {
                            util.setMaxHealth(player, minHealth);
                            oldMaxConfigHealthJoin = maxHealth;
                            oldMinConfigHealthJoin = minHealth;
                        } else if (minHealth == 0 && removeHealth == 0 && maxHealth == 0) {
                            util.setMaxHealth(player, 20);
                            oldMaxConfigHealthJoin = maxHealth;
                            oldMinConfigHealthJoin = minHealth;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void removeMaxHealth (PlayerEvent.Clone event) {
        if (ConfigHandler.healthSystem) {
            EntityPlayer oldPlayer = event.getOriginal();
            EntityPlayer newPlayer = event.getEntityPlayer();

            int minHealth = ConfigHandler.minHealth;
            int maxHealth = ConfigHandler.maxHealth;
            int removeHealth = ConfigHandler.removedHealthOnDeath;


            // Really messy still need to work on it but it works so I don't care!!!
            int specifiedValue = 0;

            if (minHealth != 0 && removeHealth != 0) {
                for (int i = (int) oldPlayer.getMaxHealth(); minHealth <= i - removeHealth; i -= removeHealth) {
                    specifiedValue = i;
                }
                if (maxHealth == oldPlayer.getMaxHealth() && maxHealth != 0) {
                    util.removeMaxHealth(newPlayer, oldPlayer, removeHealth, minHealth);
                } else if (maxHealth < minHealth && oldMaxConfigHealthClone != maxHealth && maxHealth != 0) {
                    util.setMaxHealth(newPlayer, minHealth);
                } else if (maxHealth == 0 && oldMaxConfigHealthClone != maxHealth) {
                    util.setMaxHealth(newPlayer, 20);
                } else if (maxHealth != 0 && oldMaxConfigHealthClone != maxHealth) {
                    util.setMaxHealth(newPlayer, maxHealth);
                } else if (oldPlayer.getMaxHealth() == maxHealth) {
                    util.removeMaxHealth(newPlayer, oldPlayer, removeHealth, minHealth);
                } else if (specifiedValue > 0) {
                    util.removeMaxHealth(newPlayer, oldPlayer, removeHealth, minHealth);
                } else if (specifiedValue == 0) {
                    util.setMaxHealth(newPlayer, minHealth);
                }
            } else if (maxHealth != 0) {
                util.setMaxHealth(newPlayer, maxHealth);
            } else {
                util.removeModifier(newPlayer, Constants.STATS_KEEPER_HEALTH_UUID);
            }

            this.oldMaxConfigHealthClone = maxHealth;
        }
    }
}
