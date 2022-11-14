package pm.n2.parachute.render;

import com.adryd.cauldron.api.render.helper.OverlayRendererBase;
import com.adryd.cauldron.api.render.helper.RenderObject;
import com.adryd.cauldron.api.render.util.LineDrawing;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class OverlaySheepRenderer extends OverlayRendererBase {

    public static List<Vec3d> teleportPositions = List.of();

    public OverlaySheepRenderer() {
        this.renderObjects.add(new RenderObject(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.LINES, GameRenderer::getRenderTypeLinesShader));
    }

    @Override
    public void update(MatrixStack matrices, Camera camera, float tickDelta) {
        if (shouldRender()) {
            var renderLines = this.renderObjects.get(0);
            var linesBuf = renderLines.startBuffer();

            for (Vec3d position : teleportPositions) {
                LineDrawing.drawBox(new Box(new BlockPos(position)), RenderColors.OUTLINE_BLUE, linesBuf);
            }

            renderLines.endBuffer();
        }
    }

    @Override
    public boolean shouldRender() {
        return !teleportPositions.isEmpty();
    }
}
