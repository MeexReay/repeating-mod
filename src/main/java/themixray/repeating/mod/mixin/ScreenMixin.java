package themixray.repeating.mod.mixin;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import themixray.repeating.mod.Main;
import themixray.repeating.mod.TickTask;
import themixray.repeating.mod.event.events.*;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractParentElement implements Drawable {
    @Inject(at = @At(value = "HEAD"), method = "close")
    private void close(CallbackInfo ci) {
        if (Main.me.is_recording) {
            Main.me.now_record.addEvent(new GuiCloseEvent());
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "keyPressed")
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (Main.me.is_recording) {
            Main.me.now_record.addEvent(new GuiKeyPressEvent(keyCode, scanCode, modifiers));
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (Main.me.is_recording) {
            Main.me.now_record.addEvent(new GuiCharTypeEvent(chr, modifiers));
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (Main.me.is_recording) {
            Main.me.now_record.addEvent(new GuiKeyReleaseEvent(keyCode, scanCode, modifiers));
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Main.me.is_recording) {
            Main.me.now_record.addEvent(new GuiMouseClickEvent(mouseX, mouseY, button));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (Main.me.is_recording) {
            Main.me.now_record.addEvent(new GuiMouseMoveEvent(mouseX, mouseY));
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (Main.me.is_recording) {
            Main.me.now_record.addEvent(new GuiMouseDragEvent(mouseX, mouseY, button, deltaX, deltaY));
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (Main.me.is_recording) {
            Main.me.now_record.addEvent(new GuiMouseReleaseEvent(mouseX, mouseY, button));
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (Main.me.is_recording) {
            Main.me.now_record.addEvent(new GuiMouseScrollEvent(mouseX, mouseY, amount));
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
