package themixray.repeating.mod.render;

import lombok.experimental.UtilityClass;
import themixray.repeating.mod.render.buffer.BufferManager;
import themixray.repeating.mod.render.shader.ShaderManager;

@UtilityClass
public class RenderSystem {
    public static void init() {
        BufferManager.init();
        ShaderManager.init();
    }
}
