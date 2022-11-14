package pm.n2.parachute.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.parachute.Parachute;
import pm.n2.parachute.config.Configs;
import pm.n2.parachute.impulses.ReachAttack;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient_reach {
    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), cancellable = true)
    public void doAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.TweakConfigs.REACH.getBooleanValue()) return;

        var client = (MinecraftClient) (Object) this;
        var type = client.crosshairTarget.getType();
        Parachute.LOGGER.info("type: {}", type);

        // if we miss the first ray...
        if (type != HitResult.Type.ENTITY) {
            // do a second one
            var targetedEntity = DebugRenderer.getTargetedEntity(client.player, 200);
            Parachute.LOGGER.info("targetedEntity: {}", targetedEntity);

            // and if *that* one hits...
            if (targetedEntity.isPresent()) {
                // do it!
                ReachAttack.attack(targetedEntity.get());

                client.player.swingHand(Hand.MAIN_HAND);
                cir.setReturnValue(false);
            }
        }
    }
}
