package pm.n2.parachute.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.unmapped.C_czisrdmd;
import net.minecraft.unmapped.C_suesdakf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.n2.parachute.config.Configs;

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

    @Inject(method = "setGameMode", at = @At("HEAD"), cancellable = true)
    private void interceptGameMode(GameMode gameMode, CallbackInfo ci) {
        if (Configs.TweakConfigs.LIVEOVERFLOW_DROP_GAMEMODE_PACKET.getBooleanValue()) {
            this.gameMode = GameMode.SURVIVAL;
            ci.cancel();
        }
    }

    @Inject(method = "attackBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onBlockBreaking(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)V"), cancellable = true)
    private void setBlockBreakSpeed(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        assert this.client.world != null;
        assert this.client.player != null;
        if (Configs.TweakConfigs.FASTMINE.getBooleanValue()) {
            BlockState blockState = this.client.world.getBlockState(pos);
            if (blockState.calcBlockBreakingDelta(this.client.player, this.client.player.world, pos) >= 0.7F) {
                this.client.interactionManager.breakBlock(pos);
                try (C_czisrdmd c_czisrdmd = ((IMixinClientWorld) this.client.world).invokeM_lvsrwztn().m_rhbdpkkw()) {
                    this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, c_czisrdmd.m_gqtwgmpw()));
                    this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction, c_czisrdmd.m_gqtwgmpw()));
                }
                cir.setReturnValue(true);
            }
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
}
