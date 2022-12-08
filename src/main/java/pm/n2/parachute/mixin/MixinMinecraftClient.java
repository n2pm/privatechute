package pm.n2.parachute.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.parachute.config.Configs;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "hasReducedDebugInfo", at = @At("HEAD"), cancellable = true)
    private void forceShowDebugInfo(CallbackInfoReturnable<Boolean> cir) {
        if (Configs.TweakConfigs.FORCE_DETAILED_DEBUG_INFO.getBooleanValue()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void forceShowOutline(CallbackInfoReturnable<Boolean> cir) {
        if (Configs.RenderConfigs.FORCE_GLOWING.getBooleanValue()) {
            cir.setReturnValue(true);
        }
    }
}
