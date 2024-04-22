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

    public static void bindBuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
    }

    public static void unbindBuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static void writeBuffer(FloatBuffer buffer) {
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public static void draw(int drawMode, int verts) {
        glDrawArrays(drawMode, 0, verts);
    }

    public static void bind() {
        prevVao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        glBindVertexArray(vao);
    }

    public static void unbind() {
        glBindVertexArray(prevVao);
    }
}
