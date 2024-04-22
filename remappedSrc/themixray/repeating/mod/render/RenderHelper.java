package themixray.repeating.mod.render;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.util.math.Vec3d;
import themixray.repeating.mod.render.buffer.WorldBuffer;
import themixray.repeating.mod.render.shader.ShaderManager;

import java.awt.*;

import static org.lwjgl.opengl.GL33.*;

@UtilityClass
public class RenderHelper {
    public WorldBuffer startLines(WorldRenderContext context) {
        glEnable(GL_LINE_SMOOTH);
        return new WorldBuffer(GL_LINES, ShaderManager.getPositionColorShader(), context);
    }

    public void endLines(WorldBuffer buffer) {
        glEnable(GL_BLEND);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);
        buffer.draw();
        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    public void drawLine(WorldBuffer buffer, float x1, float y1, float z1, float x2, float y2, float z2, Color color) {
        buffer.vert(x1, y1, z1, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        buffer.vert(x2, y2, z2, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public static WorldBuffer startTri(WorldRenderContext context) {
        return new WorldBuffer(GL_TRIANGLES, ShaderManager.getPositionColorShader(), context);
    }

    public static void endTri(WorldBuffer buffer) {
        //glDepthRange(0, 0.7);
        glEnable(GL_BLEND);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        glDepthMask(false);
        buffer.draw();
        glDepthMask(true);
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
        glDepthRange(0, 1);
    }

    public void drawTri(WorldBuffer buffer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Color color) {
        buffer.vert(x1, y1, z1, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        buffer.vert(x2, y2, z2, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        buffer.vert(x3, y3, z3, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public static void drawRectFromTri(WorldBuffer buffer,
                                       float x1, float y1, float z1,
                                       float x2, float y2, float z2,
                                       float x3, float y3, float z3,
                                       float x4, float y4, float z4,
                                       Color color) {
        drawTri(buffer,
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3,
                color);
        drawTri(buffer,
                x3, y3, z3,
                x4, y4, z4,
                x1, y1, z1,
                color);
    }

    public void drawRectFromLines(WorldBuffer buffer,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                Color color) {
        drawLine(buffer,
                x1, y1, z1,
                x2, y2, z2,
                color);
        drawLine(buffer,
                x2, y2, z2,
                x3, y3, z3,
                color);
        drawLine(buffer,
                x3, y3, z3,
                x4, y4, z4,
                color);
        drawLine(buffer,
                x4, y4, z4,
                x1, y1, z1,
                color);
    }

    public void drawBoxFromTri(WorldBuffer buffer,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                Color color) {
        float[][] v = new float[][]{
            new float[]{Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2)},
            new float[]{Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)}};

        drawRectFromTri(buffer,
            v[0][0], v[0][1], v[0][2],
            v[1][0], v[0][1], v[0][2],
            v[1][0], v[1][1], v[0][2],
            v[0][0], v[1][1], v[0][2],
            color);

        drawRectFromTri(buffer,
            v[0][0], v[0][1], v[0][2],
            v[1][0], v[0][1], v[0][2],
            v[1][0], v[0][1], v[1][2],
            v[0][0], v[0][1], v[1][2],
            color);

        drawRectFromTri(buffer,
            v[0][0], v[0][1], v[0][2],
            v[0][0], v[0][1], v[1][2],
            v[0][0], v[1][1], v[1][2],
            v[0][0], v[1][1], v[0][2],
            color);

        drawRectFromTri(buffer,
            v[0][0], v[0][1], v[1][2],
            v[1][0], v[0][1], v[1][2],
            v[1][0], v[1][1], v[1][2],
            v[0][0], v[1][1], v[1][2],
            color);

        drawRectFromTri(buffer,
            v[0][0], v[1][1], v[0][2],
            v[1][0], v[1][1], v[0][2],
            v[1][0], v[1][1], v[1][2],
            v[0][0], v[1][1], v[1][2],
            color);

        drawRectFromTri(buffer,
            v[1][0], v[0][1], v[0][2],
            v[1][0], v[0][1], v[1][2],
            v[1][0], v[1][1], v[1][2],
            v[1][0], v[1][1], v[0][2],
            color);
    }

    public void drawBoxFromLines(WorldBuffer buffer,
                               float x1, float y1, float z1,
                               float x2, float y2, float z2,
                               Color color) {
        float[][] v = new float[][]{
                new float[]{Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2)},
                new float[]{Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)}};

        drawRectFromLines(buffer,
                v[0][0], v[0][1], v[0][2],
                v[1][0], v[0][1], v[0][2],
                v[1][0], v[1][1], v[0][2],
                v[0][0], v[1][1], v[0][2],
                color);

        drawRectFromLines(buffer,
                v[0][0], v[0][1], v[0][2],
                v[1][0], v[0][1], v[0][2],
                v[1][0], v[0][1], v[1][2],
                v[0][0], v[0][1], v[1][2],
                color);

        drawRectFromLines(buffer,
                v[0][0], v[0][1], v[0][2],
                v[0][0], v[0][1], v[1][2],
                v[0][0], v[1][1], v[1][2],
                v[0][0], v[1][1], v[0][2],
                color);

        drawRectFromLines(buffer,
                v[0][0], v[0][1], v[1][2],
                v[1][0], v[0][1], v[1][2],
                v[1][0], v[1][1], v[1][2],
                v[0][0], v[1][1], v[1][2],
                color);

        drawRectFromLines(buffer,
                v[0][0], v[1][1], v[0][2],
                v[1][0], v[1][1], v[0][2],
                v[1][0], v[1][1], v[1][2],
                v[0][0], v[1][1], v[1][2],
                color);

        drawRectFromLines(buffer,
                v[1][0], v[0][1], v[0][2],
                v[1][0], v[0][1], v[1][2],
                v[1][0], v[1][1], v[1][2],
                v[1][0], v[1][1], v[0][2],
                color);
    }
}
