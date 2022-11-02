package pm.n2.parachute.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.parachute.config.Configs;

@Mixin(VehicleMoveC2SPacket.class)
public class MixinVehicleMoveC2SPacket {
    @Final
    @Mutable
    @Shadow
    protected double x;

    @Final
    @Mutable
    @Shadow
    protected double z;

    private double round(double d) {
        double tmp = Math.floor(d * 100) / 100;
        return Math.nextAfter(
                tmp,
                tmp + Math.signum(d)
        );
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void roundPosition(Entity entity, CallbackInfo ci) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_BOT_MOVEMENT.getBooleanValue()) {
            this.x = round(entity.getX());
            this.z = round(entity.getZ());
        }
    }
}
