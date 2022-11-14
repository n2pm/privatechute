package pm.n2.parachute.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.n2.parachute.config.Configs;
import pm.n2.parachute.util.FakeRandom;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Unique
    private double cameraZ;

    @Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FDDD)V", at = @At("HEAD"))
    private void captureCameraZ(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double camX, double camY, double camZ, CallbackInfo ci) {
        this.cameraZ = camZ;
    }

    @ModifyArg(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;"), index = 3)
    private double modifyCameraZ(double z) {
        if (Configs.TweakConfigs.FAKE_CLOUDS_POS.getBooleanValue()) {
            double newZ = FakeRandom.offset(this.cameraZ, FakeRandom.offsetZ) / 12.0 + 0.33F;
            newZ -= (MathHelper.floor(newZ / 2048.0) * 2048);
            return newZ;
        } else return z;
    }
}
