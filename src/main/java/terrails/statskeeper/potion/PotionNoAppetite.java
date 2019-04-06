package terrails.statskeeper.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import terrails.statskeeper.StatsKeeper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PotionNoAppetite extends Potion {

    private static final ResourceLocation TEXTURE = new ResourceLocation(StatsKeeper.MOD_ID, "textures/potions.png");

    public PotionNoAppetite() {
        super(true, new Color(72, 120, 68).getRGB());
        this.setRegistryName(new ResourceLocation(StatsKeeper.MOD_ID, "no_appetite"));
        this.setIconIndex(0, 0);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean hasStatusIcon() {
        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
        return super.hasStatusIcon();
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}