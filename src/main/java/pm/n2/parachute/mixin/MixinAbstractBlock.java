package pm.n2.parachute.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pm.n2.parachute.config.Configs;
import pm.n2.parachute.util.FakeRandom;

@Mixin(AbstractBlock.class)
public class MixinAbstractBlock {
    @Redirect(method="getRenderingSeed", at=@At(value="INVOKE", target="Lnet/minecraft/util/math/MathHelper;hashCode(Lnet/minecraft/util/math/Vec3i;)J"))
    private long getSeed(Vec3i vec) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_BROKEN_ROTATIONS.getBooleanValue() || Configs.TweakConfigs.LIVEOVERFLOW_NO_ROTATIONS.getBooleanValue()) {
            return FakeRandom.getHashCode(vec.getX(), vec.getY(), vec.getZ());
        } else {
            return MathHelper.hashCode(vec);
        }
    }
}
