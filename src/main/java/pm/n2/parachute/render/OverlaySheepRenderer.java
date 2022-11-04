package pm.n2.parachute.render;

import com.adryd.cauldron.api.render.helper.OverlayRendererBase;
import com.adryd.cauldron.api.render.helper.RenderObject;
import com.adryd.cauldron.api.render.util.LineDrawing;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import pm.n2.parachute.impulses.Sheep;

public class OverlaySheepRenderer extends OverlayRendererBase {
    public OverlaySheepRenderer() {
        this.renderObjects.add(new RenderObject(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.LINES, GameRenderer::getRenderTypeLinesShader));
    }

    @Override
    public void update(MatrixStack matrices, Camera camera, float tickDelta) {
        if (shouldRender()) {
            var renderLines = this.renderObjects.get(0);
            var linesBuf = renderLines.startBuffer();

            var origPos = Sheep.origPos;
            var sheepPos = Sheep.sheepPos;

            LineDrawing.drawLine(origPos.x, origPos.y, origPos.z, sheepPos.x, sheepPos.y, sheepPos.z, RenderColors.OUTLINE_BLUE, linesBuf);

            renderLines.endBuffer();
        }
    }

    @Override
    public boolean shouldRender() {
        return Sheep.sheepPos != null && Sheep.origPos != null;
    }
}
