package terrails.statskeeper.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import terrails.statskeeper.StatsKeeper;

import java.awt.*;

public class NoAppetiteEffect extends StatusEffect {

    private final Identifier texture = new Identifier(StatsKeeper.MOD_ID, "textures/potions.png");

    public NoAppetiteEffect() {
        super(true, new Color(72, 120, 68).getRGB());
        this.setIcon(0, 0);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasIcon() {
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        return super.hasIcon();
    }
}
