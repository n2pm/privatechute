package pm.n2.parachute.render;

import com.adryd.cauldron.api.render.helper.OverlayRendererBase;
import com.adryd.cauldron.api.render.helper.RenderObject;
import com.adryd.cauldron.api.render.util.LineDrawing;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class OverlaySheepRenderer extends OverlayRendererBase {

    public static List<Vec3d> returnPositions = new ArrayList<>();
    public static List<Vec3d> approachPositions = new ArrayList<>();

    private static double boxSize = 0.1d;

    public OverlaySheepRenderer() {
        this.renderObjects.add(new RenderObject(VertexFormat.DrawMode.LINES, VertexFormats.LINES, GameRenderer::getRenderTypeLinesShader));
    }

    @Override
    public void update(MatrixStack matrices, Camera camera, float tickDelta) {
        var renderLines = this.renderObjects.get(0);
        var linesBuf = renderLines.startBuffer();

        for (Vec3d pos : approachPositions) {
            LineDrawing.drawBox(new Box(pos.x - boxSize, pos.y - boxSize, pos.z - boxSize, pos.x + boxSize, pos.y + boxSize, pos.z + boxSize), RenderColors.OUTLINE_BLUE, linesBuf);
        }

        for (Vec3d pos : returnPositions) {
            LineDrawing.drawBox(new Box(pos.x - boxSize, pos.y - boxSize, pos.z - boxSize, pos.x + boxSize, pos.y + boxSize, pos.z + boxSize), RenderColors.OUTLINE_GREEN, linesBuf);
        }

        renderLines.endBuffer();
    }

    @Override
    public boolean shouldRender() {
        return !returnPositions.isEmpty() && !approachPositions.isEmpty();
    }

    @Override
    public boolean shouldUpdate() {
        return !returnPositions.isEmpty() && !approachPositions.isEmpty();
    }
}
