package pm.n2.parachute.mixin;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface IMixinWorldRenderer {
    @Invoker("renderClouds")
    BufferBuilder.BuiltBuffer pubRenderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color);
}
