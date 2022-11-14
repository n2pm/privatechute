package pm.n2.parachute.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface IMixinWorldRenderer {
    @Invoker("renderClouds")
    BufferBuilder.RenderedBuffer pubRenderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color);
}
