package pm.n2.parachute.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.parachute.config.Configs;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Shadow private GameMode gameMode;

    @Inject(method = "setGameMode", at = @At("HEAD"), cancellable = true)
    private void interceptGameMode(GameMode gameMode, CallbackInfo ci) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_DROP_GAMEMODE_PACKET.getBooleanValue()) {
            this.gameMode = GameMode.SURVIVAL;
            ci.cancel();
        }
    }
}
