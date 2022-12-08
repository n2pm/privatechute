package pm.n2.parachute.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.n2.parachute.config.Configs;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Shadow
    @Final
    private ClientConnection connection;

    @Unique
    private int lastTeleportId;

    @Inject(method = "onResourcePackSend", at = @At("HEAD"), cancellable = true)
    private void onResourcePackSend(ResourcePackSendS2CPacket packet, CallbackInfo ci) {
        boolean tweakEnabled = Configs.TweakConfigs.NO_SERVER_RESOURCE_PACKS.getBooleanValue();
        if (tweakEnabled) {
            this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
            this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
            ci.cancel();
        }
    }

    @Inject(method = "onGameStateChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket;getReason()Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket$Reason;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void onGameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci, PlayerEntity playerEntity) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_DROP_DEMO_PACKET.getBooleanValue()) {
            GameStateChangeS2CPacket.Reason reason = packet.getReason();
            if (reason == GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN || reason == GameStateChangeS2CPacket.GAME_WON) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onWorldBorderCenterChanged", at = @At("HEAD"), cancellable = true)
    private void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet, CallbackInfo ci) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_DROP_WORLDBORDER_PACKET.getBooleanValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "onWorldBorderInitialize", at = @At("HEAD"), cancellable = true)
    private void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet, CallbackInfo ci) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_DROP_WORLDBORDER_PACKET.getBooleanValue()) {
            ci.cancel();
        }
    }

//    @Inject(method = "onPlayerRespawn", at=@At("HEAD"), cancellable = true)
//    private void cancelOnPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
//        if (Configs.TweakConfigs.SUPPRESS_RESPAWN_PACKETS.getBooleanValue()) {
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "onChunkData", at=@At("HEAD"), cancellable = true)
//    private void cancelOnPlayerRespawn(ChunkDataS2CPacket packet, CallbackInfo ci) {
//        if (Configs.TweakConfigs.SUPPRESS_RESPAWN_PACKETS.getBooleanValue()) {
//            ci.cancel();
//        }
//    }

    @Redirect(method = "onPlayerPositionLook", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;)V", ordinal = 0))
    private void cancelOnPlayerRespawn(ClientConnection instance, Packet<?> packet) {
        if (!Configs.TweakConfigs.SUPPRESS_RESPAWN_PACKETS.getBooleanValue()) {
            instance.send(packet);
        }
    }
}
