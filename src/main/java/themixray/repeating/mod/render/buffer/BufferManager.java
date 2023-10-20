package themixray.repeating.mod.render.buffer;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

@UtilityClass
public class BufferManager {
    private int vao;
    private int vbo;

    private int prevVao;

    public void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            vao = glGenVertexArrays();
            vbo = glGenBuffers();
        });
    }

    public void bindBuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
    }

    public void unbindBuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void writeBuffer(FloatBuffer buffer) {
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public void draw(int drawMode, int verts) {
        glDrawArrays(drawMode, 0, verts);
    }

    public void bind() {
        prevVao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        glBindVertexArray(vao);
    }

    public void unbind() {
        glBindVertexArray(prevVao);
    }
}
