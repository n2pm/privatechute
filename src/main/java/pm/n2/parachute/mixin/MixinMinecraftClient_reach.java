package pm.n2.parachute.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.parachute.Parachute;
import pm.n2.parachute.config.Configs;
import pm.n2.parachute.impulses.ReachAttack;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient_reach {

    @Shadow
    public HitResult crosshairTarget;

    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), cancellable = true)
    public void doAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.FeatureConfigs.REACH.getBooleanValue()) return;

        // if we miss the first ray...
        if (this.crosshairTarget.getType() != HitResult.Type.ENTITY) {
            // do a second one
            Optional<Entity> targetedEntity = DebugRenderer.getTargetedEntity(this.player, 200);

            // and if that one hits
            if (targetedEntity.isPresent()) {
                // do it!
                ReachAttack.attack(targetedEntity.get());

                this.player.swingHand(Hand.MAIN_HAND);
                cir.setReturnValue(false);
            }
        }
    }
}
