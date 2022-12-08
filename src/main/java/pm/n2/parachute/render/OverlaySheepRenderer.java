package pm.n2.parachute.render;

import com.adryd.cauldron.api.render.helper.BufferBuilderProxy;
import com.adryd.cauldron.api.render.helper.OverlayRendererBase;
import com.adryd.cauldron.api.render.helper.RenderObject;
import com.adryd.cauldron.api.render.util.LineDrawing;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import pm.n2.parachute.config.Configs;

import java.util.ArrayList;
import java.util.List;

public class OverlaySheepRenderer extends OverlayRendererBase {

    public static List<Vec3d> returnPositions = new ArrayList<>();
    public static List<Vec3d> approachPositions = new ArrayList<>();
    public static List<Vec3d> importantPositions = new ArrayList<>();

    private RenderObject linesObject;

    private static double boxSize = 0.05d;
    private static double importantBoxSize = 0.1d;

    public OverlaySheepRenderer() {
        this.linesObject = new RenderObject(VertexFormat.DrawMode.LINES, VertexFormats.LINES, GameRenderer::getRenderTypeLinesShader);
        this.renderObjects.add(this.linesObject);
        this.linesObject.setBeforeDraw(() -> {
            RenderSystem.disableDepthTest();
            RenderSystem.lineWidth(3.0f);
        });
    }

    @Override
    public void update(MatrixStack matrices, Camera camera, float tickDelta) {
        RenderObject renderLines = this.renderObjects.get(0);
        BufferBuilderProxy linesBuf = renderLines.startBuffer();

        for (Vec3d pos : approachPositions) {
            LineDrawing.drawBox(new Box(pos.x - boxSize, pos.y - boxSize, pos.z - boxSize, pos.x + boxSize, pos.y + boxSize, pos.z + boxSize), RenderColors.OUTLINE_BLUE, camera, linesBuf);
        }

        for (Vec3d pos : returnPositions) {
            LineDrawing.drawBox(new Box(pos.x - boxSize, pos.y - boxSize, pos.z - boxSize, pos.x + boxSize, pos.y + boxSize, pos.z + boxSize), RenderColors.OUTLINE_GREEN, camera, linesBuf);
        }

        for (Vec3d pos : importantPositions) {
            LineDrawing.drawBox(new Box(pos.x - importantBoxSize , pos.y - importantBoxSize, pos.z - importantBoxSize, pos.x + importantBoxSize, pos.y + importantBoxSize, pos.z + importantBoxSize), RenderColors.OUTLINE_RED, camera, linesBuf);
        }

        renderLines.endBuffer(camera);
    }

    @Override
    public boolean shouldRender() {
        return Configs.FeatureConfigs.REACH_RENDERER.getBooleanValue() && !returnPositions.isEmpty() && !approachPositions.isEmpty();
    }

    @Override
    public boolean shouldUpdate(Camera camera) {
        return Configs.FeatureConfigs.REACH_RENDERER.getBooleanValue() && !returnPositions.isEmpty() && !approachPositions.isEmpty();
    }
}
