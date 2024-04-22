package themixray.repeating.mod;

import net.minecraft.client.gui.DrawContext;

public interface RenderListener {
    default boolean beforeRender() {
        return true;
    }

    void render(DrawContext context, int mouseX, int mouseY, float delta);
}
