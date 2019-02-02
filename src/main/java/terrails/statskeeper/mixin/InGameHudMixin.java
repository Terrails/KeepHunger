package terrails.statskeeper.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.config.SKConfig;

import java.util.Random;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    private final Identifier HEART_TEXTURES = new Identifier(StatsKeeper.MOD_ID, "textures/gui/hearts.png");

    @Shadow private @Final MinecraftClient client;
    @Shadow private int ticks;
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Shadow private @Final Random random;
    @Shadow private long field_2032; // healthTicks

    @Inject(method = "method_1765", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private void fixStatusEffectRenderer(CallbackInfo info) {
        this.client.getTextureManager().bindTexture(ContainerScreen.BACKGROUND_TEXTURE);
    }

    @Redirect(method = "method_1760",
            slice = @Slice(from = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=health")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(F)I", ordinal = 0))
    private int runDefaultRenderer(float def) {
        return -1;
    }

    @Inject(method = "method_1760", locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=health"))
    private void render(CallbackInfo info, PlayerEntity player) {
        InGameHud hud = (InGameHud) (Object) this;
        int currentHealth = MathHelper.ceil(player.getHealth());
        boolean highlight = this.field_2032 > (long) this.ticks && (this.field_2032 - (long) this.ticks) / 3L % 2L == 1L;

        int left = this.scaledWidth / 2 - 91;
        int top = this.scaledHeight - 39;
        float maxHealth = player.getHealthMaximum();
        int currentAbsorption = MathHelper.ceil(player.getAbsorptionAmount());

        int regenerationTicks = -1;
        if (player.hasPotionEffect(StatusEffects.REGENERATION)) {
            regenerationTicks = this.ticks % MathHelper.ceil(maxHealth + 5.0F);
        }

        // Health Renderer
        int maxHealthIterator = MathHelper.ceil(maxHealth / 2.0f);
        for (int i = 1; i <= maxHealthIterator; i++) {
            int x = left + (i - 1) % 10 * 8;
            int y = top;

            if (currentHealth <= 4 && maxHealth > 4) {
                y += random.nextInt(2);
            }

            if (i == regenerationTicks) {
                y -= 2;
            }

            int effect_offset = 16 + (player.hasPotionEffect(StatusEffects.POISON) ? 36 : player.hasPotionEffect(StatusEffects.WITHER) ? 72 : 0);
            int hardcore_offset = player.world.getLevelProperties().isHardcore() ? 5 : 0;
            int highlight_offset = highlight ? 9 : 0;
            int iterHealth = i * 2 - 1;
            boolean renderMissing = SKConfig.instance.HEALTH_STATS.render_missing;

            if (iterHealth <= 20) {
                if (renderMissing && maxHealth < 19 && i == maxHealthIterator) {
                    this.client.getTextureManager().bindTexture(HEART_TEXTURES);
                    for (int z = 1; z <= (20 - (int) maxHealth) / 2; z++) {
                        int xz = left + (MathHelper.ceil(maxHealth / 2.0f) + z - 1) % 10 * 8;

                        hud.drawTexturedRect(xz, y, 0, 0, 9, 9);
                    }
                    this.client.getTextureManager().bindTexture(Drawable.ICONS);
                }

                if (i * 2 - 1 > maxHealth - 1) {
                    this.client.getTextureManager().bindTexture(HEART_TEXTURES);
                    hud.drawTexturedRect(x, y, 9 + highlight_offset, 0, 9, 9);
                    this.client.getTextureManager().bindTexture(Drawable.ICONS);
                } else {
                    hud.drawTexturedRect(x, y, 16 + highlight_offset, 0, 9, 9);
                }
            }

            if (highlight) {
                if (i * 2 - 1 < currentHealth) {
                    hud.drawTexturedRect(x, y, effect_offset + 54, 9 * hardcore_offset, 9, 9);
                }

                if (i * 2 - 1 == currentHealth) {
                    hud.drawTexturedRect(x, y, effect_offset + 63, 9 * hardcore_offset, 9, 9);
                }
            }


            if (iterHealth <= 20) {
                if (iterHealth < currentHealth) {
                    hud.drawTexturedRect(x, y, effect_offset + 36, 9 * hardcore_offset, 9, 9);
                }

                if (iterHealth == currentHealth) {
                    hud.drawTexturedRect(x, y, effect_offset + 45, 9 * hardcore_offset, 9, 9);
                }
            } else {
                this.client.getTextureManager().bindTexture(HEART_TEXTURES);
                effect_offset = player.hasPotionEffect(StatusEffects.POISON) ? 9 : 0;
                hardcore_offset = player.world.getLevelProperties().isHardcore() ? 27 : 0;

                int type_offset = ((i * 2 - 21 - currentAbsorption) / 20) % 12;

                if (iterHealth < currentHealth) {
                    hud.drawTexturedRect(x, y, effect_offset + 15 * type_offset, hardcore_offset + effect_offset + 10, 9, 9);
                }

                if (iterHealth == currentHealth) {
                    hud.drawTexturedRect(x, y, effect_offset + 9 + 15 * type_offset, hardcore_offset + effect_offset + 10, 5, 9);
                }

                this.client.getTextureManager().bindTexture(Drawable.ICONS);
            }
        }

        // Absorption Renderer
        int maxAbsorptionIterator = MathHelper.ceil(currentAbsorption / 2.0f);
        for (int i = 1; i <= maxAbsorptionIterator; i++) {
            int x = left + (i - 1) % 10 * 8;
            int y = top - 10;

            /*
            if (currentHealth <= 4 && maxHealth > 4) {
                y += random.nextInt(2);
            }
            */

            if (i == regenerationTicks) {
                y -= 2;
            }

            int effect_offset = 16 + (player.hasPotionEffect(StatusEffects.POISON) ? 36 : player.hasPotionEffect(StatusEffects.WITHER) ? 72 : 0);
            int hardcore_offset = player.world.getLevelProperties().isHardcore() ? 5 : 0;
            int highlight_offset = highlight ? 1 : 0;
            int iterAbsorption = i * 2 - 1;

            if (iterAbsorption > currentAbsorption - 1) {
                this.client.getTextureManager().bindTexture(HEART_TEXTURES);
                hud.drawTexturedRect(x, y, 9 + highlight_offset * 9, 0, 9, 9);
                this.client.getTextureManager().bindTexture(Drawable.ICONS);
            } else {
                hud.drawTexturedRect(x, y, 16 + highlight_offset * 9, 0, 9, 9);
            }

            if (iterAbsorption <= 20 || player.hasPotionEffect(StatusEffects.WITHER) ) {

                if (iterAbsorption == currentAbsorption && currentAbsorption % 2 == 1) {
                    hud.drawTexturedRect(x, y, effect_offset + 153, 9 * hardcore_offset, 9, 9);
                } else {
                    hud.drawTexturedRect(x, y, effect_offset + 144, 9 * hardcore_offset, 9, 9);
                }
            } else {
                this.client.getTextureManager().bindTexture(HEART_TEXTURES);
                effect_offset = player.hasPotionEffect(StatusEffects.POISON) ? 9 : 0;
                hardcore_offset = player.world.getLevelProperties().isHardcore() ? 27 : 0;

                int type_offset = ((iterAbsorption - 20) / 20) % 12;

                if (iterAbsorption == currentAbsorption && currentAbsorption % 2 == 1) {
                    hud.drawTexturedRect(x, y, effect_offset + 9 + 15 * type_offset, hardcore_offset + effect_offset + 75, 5, 9);
                } else {
                    hud.drawTexturedRect(x, y, effect_offset + 15 * type_offset, hardcore_offset + effect_offset + 75, 9, 9);
                }
                this.client.getTextureManager().bindTexture(Drawable.ICONS);
            }
        }
    }

    /*
    @Inject(method = "method_1760", locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;method_1734()Lnet/minecraft/entity/LivingEntity;"))
    private void insertCustomRenderer(CallbackInfo info, PlayerEntity player) {
        // code
    }


    @Redirect(method = "method_1760", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(F)I", ordinal = 0))
    private int getHealth(float val) {
        return Math.min(MathHelper.ceil(val), 20);
    }

    @Redirect(method = "method_1760", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeInstance;getValue()D", ordinal = 0))
    private double getSKMaxHealth(EntityAttributeInstance instance) {
        if (instance.getAttribute() != EntityAttributes.MAX_HEALTH) {
            return instance.getValue();
        }
        return Math.min(instance.getValue(), 20.0);
    }
    */
}
