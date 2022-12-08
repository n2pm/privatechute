package pm.n2.parachute.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.parachute.config.Configs;

import javax.annotation.Nullable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
    @Shadow
    private GameMode gameMode;
    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;
    @Shadow
    @Final
    private MinecraftClient client;
    @Unique
    @Nullable
    BlockPos lastBlockBreak;
    @Shadow
    public abstract boolean breakBlock(BlockPos pos);
    @Shadow
    private int blockBreakingCooldown;

    @Inject(method = "setGameMode", at = @At("HEAD"), cancellable = true)
    private void interceptGameMode(GameMode gameMode, CallbackInfo ci) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_DROP_GAMEMODE_PACKET.getBooleanValue()) {
            this.gameMode = GameMode.SURVIVAL;
            ci.cancel();
        }
    }

    @ModifyConstant(
            method = "updateBlockBreakingProgress",
            constant = @Constant(
                    ordinal = 2,
                    floatValue = 1.0F
            )
    )
    private float setBreakThreshold(float orig) {
        if (Configs.TweakConfigs.FASTMINE.getBooleanValue()) {
            return 0.7F;
        } else {
            return 1.0F;
        }
    }

    @Inject(method = "attackBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onBlockBreaking(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)V", ordinal = 1), cancellable = true)
    private void setBlockBreakSpeed(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        assert this.client.world != null;
        assert this.client.player != null;
        if (civBreak(pos, direction, cir)) {
            return;
        }
        if (Configs.TweakConfigs.FASTMINE.getBooleanValue()) {
            BlockState blockState = this.client.world.getBlockState(pos);
            if (blockState.calcBlockBreakingDelta(this.client.player, this.client.player.world, pos) >= 0.7F) {
                this.breakBlock(pos);
                try (PendingUpdateManager pendingUpdateManager = ((IMixinClientWorld) this.client.world).invokeGetPendingUpdateManager().incrementSequence()) {
                    this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, pendingUpdateManager.getSequence()));
                    pendingUpdateManager.incrementSequence();
                    this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction, pendingUpdateManager.getSequence()));
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onBlockBreaking(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)V", ordinal = 1), cancellable = true)
    private void civBreakUpdate(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        civBreak(pos, direction, cir);
    }

    private boolean civBreak(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.TweakConfigs.CIVBREAK.getBooleanValue() && this.lastBlockBreak != null && this.lastBlockBreak.equals(pos)) {
            try (PendingUpdateManager pendingUpdateManager = ((IMixinClientWorld) this.client.world).invokeGetPendingUpdateManager().incrementSequence()) {
                this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction, pendingUpdateManager.getSequence()));
            }
            this.breakBlock(pos);
            cir.setReturnValue(true);
            return true;
        } else {
            this.lastBlockBreak = null;
        }
        return false;
    }

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void updateLastBrokenBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        this.lastBlockBreak = pos;
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
    private void noBlockBreakCooldown(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.TweakConfigs.NO_BREAK_COOLDOWN.getBooleanValue()) {
            this.blockBreakingCooldown = 0;
        }
    }

    @Inject(method = "cancelBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void dontCancelBlockBreaking(CallbackInfo ci) {
        if (Configs.TweakConfigs.NO_BREAK_CANCEL.getBooleanValue()) {
            ci.cancel();
        }
    }

}
