package themixray.repeating.mod.render.buffer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import themixray.repeating.mod.render.shader.Shader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class WorldBuffer {
    private final List<Vertex> vertices = new ArrayList<>();
    private final int drawMode;
    private final Shader shader;
    private FloatBuffer projectionMatrix;
    private final Vec3d cameraPos;

    public WorldBuffer(int drawMode, Shader shader, WorldRenderContext worldRenderContext) {
        this.drawMode = drawMode;
        this.shader = shader;
        this.cameraPos = worldRenderContext.camera().getPos();
        makeProjectionMatrix(worldRenderContext.projectionMatrix(), worldRenderContext.matrixStack().peek().getPositionMatrix());
    }

    public void vert(float x, float y, float z, float r, float g, float b, float a) {
        vertices.add(new Vertex(x - (float) cameraPos.x, y - (float) cameraPos.y, z - (float) cameraPos.z, r, g, b, a));
    }

    public void draw() {
        BufferManager.bind();
        BufferManager.bindBuffer();

        BufferManager.writeBuffer(getBuffer());
        applyProjectionMatrix();

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, vertices.size() * 3 * 4L);
        glEnableVertexAttribArray(1);

        BufferManager.unbindBuffer();

        shader.bind();
        BufferManager.draw(drawMode, this.vertices.size());
        shader.unbind();

        BufferManager.unbind();
    }

    private FloatBuffer getBuffer() {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(vertices.size() * 7);
        ArrayList<Float> floats = new ArrayList<>();
        for (Vertex vertex : vertices) {
            floats.add(vertex.getX());
            floats.add(vertex.getY());
            floats.add(vertex.getZ());
        }
        for (Vertex vertex : vertices) {
            floats.add(vertex.getR());
            floats.add(vertex.getG());
            floats.add(vertex.getB());
            floats.add(vertex.getA());
        }
        Float[] floatArray = new Float[floats.size()];
        floats.toArray(floatArray);
        floatBuffer.put(ArrayUtils.toPrimitive(floatArray));
        return floatBuffer.flip();
    }

    private void makeProjectionMatrix(Matrix4f projectionMatrix, Matrix4f viewModelMatrix) {
        this.projectionMatrix = projectionMatrix.mul(viewModelMatrix).get(BufferUtils.createFloatBuffer(16));
    }

    private void applyProjectionMatrix() {
        shader.uniformMatrix4f("u_projection", projectionMatrix);
    }
}
