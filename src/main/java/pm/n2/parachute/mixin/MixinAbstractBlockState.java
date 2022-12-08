package pm.n2.parachute.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pm.n2.parachute.config.Configs;
import pm.n2.parachute.util.OffsetUtil;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class MixinAbstractBlockState {
    @Redirect(method = "getModelOffset", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;hashCode(III)J"))
    private long getSeed(int x, int y, int z) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_BROKEN_ROTATIONS.getBooleanValue() || Configs.TweakConfigs.LIVEOVERFLOW_NO_ROTATIONS.getBooleanValue()) {
            return OffsetUtil.getHashCode(x, y, z);
        } else {
            return MathHelper.hashCode(x, y, z);
        }
    }
}
