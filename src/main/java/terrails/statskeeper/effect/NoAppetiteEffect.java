package terrails.statskeeper.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

import java.awt.*;

public class NoAppetiteEffect extends StatusEffect {

    public NoAppetiteEffect() {
        super(StatusEffectType.HARMFUL, new Color(72, 120, 68).getRGB());
    }
}
