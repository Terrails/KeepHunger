package terrails.keephunger.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionDefault extends Potion
{
    private static final ResourceLocation POTIONS_LOCATION = new ResourceLocation("keep_hunger:textures/potions/potioneffect.png");

    protected PotionDefault(boolean isBadEffectIn, int liquidColorIn, int x, int y)
    {
        super(isBadEffectIn, liquidColorIn);
        this.setIconIndex(x, y);
    }

    public boolean shouldRenderInvText(PotionEffect effect)
    {
        return super.shouldRenderInvText(effect);
    }

    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
    {
        super.renderInventoryEffect(x, y, effect, mc);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasStatusIcon()
    {
    //    Minecraft.getMinecraft().getTextureManager().bindTexture(POTIONS_LOCATION);
        Minecraft.getMinecraft().renderEngine.bindTexture(POTIONS_LOCATION);
        this.setIconIndex(0,0);
        return super.hasStatusIcon();
     //   return false;
    }
}
