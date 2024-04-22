package themixray.repeating.mod.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import themixray.repeating.mod.Main;
import themixray.repeating.mod.RecordState;
import themixray.repeating.mod.RepeatingScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RecordListWidget extends ScrollableWidget {
    private List<RecordWidget> widgets = new ArrayList<>();
    private boolean focused = false;

    public RecordListWidget(int x, int y, int width, int height) {
        super(x,y,width,height,Text.empty());
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        focused = true;
        boolean res = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        focused = false;
        return res;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 10;
    }

    @Override
    protected void renderContents(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int y = 0;
        for (RecordWidget wid: widgets) {
            wid.setY(y);
            wid.render(ctx, mouseX, (int) (mouseY + this.getScrollY()), delta);

            y += wid.getHeight();
            y += 2;
        }
    }

    public void addWidget(RecordState record) {
        RecordWidget widget = new RecordWidget(0, 0, width, 55, record, this);
        widget.init(null);
        widgets.add(0, widget);
    }

    public void removeWidget(RecordState record) {
        widgets.removeIf(i -> i.getRecord().equals(record));
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    protected int getContentsHeight() {
        return !widgets.isEmpty() ? widgets.size() * 55 + (widgets.size() - 1) * 2 : 0;
    }

    public void init(RepeatingScreen screen) {
        for (RecordWidget widget : widgets) {
            widget.init(screen);
        }

        screen.addDrawableChild(this);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    public RecordWidget getWidget(RecordState record) {
        for (RecordWidget widget : widgets) {
            if (widget.getRecord().equals(record)) {
                return widget;
            }
        }
        return null;
    }

    public interface transport {
        boolean check(ClickableWidget ch);
    }

    public boolean checkTransport(transport tr) {
        for (RecordWidget wid : widgets) {
            for (ClickableWidget child : wid.getChildren()) {
                if (child.isFocused() && tr.check(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkTransportNF(double mouseX, double mouseY, int button) {
        for (RecordWidget wid : widgets) {
            if (wid.contains((int) mouseX, (int) mouseY)) {
                Main.me.setNowRecord(wid.getRecord());
            }

            for (ClickableWidget child : wid.getChildren()) {
                if (child.mouseClicked(mouseX, mouseY, button)) {
                    child.setFocused(true);
                    return true;
                } else {
                    child.setFocused(false);
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return checkTransportNF(mouseX, mouseY + getScrollY(), button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return checkTransport((c) -> c.charTyped(chr, modifiers)) || super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return checkTransport((c) -> c.keyPressed(keyCode, scanCode, modifiers)) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return checkTransport((c) -> c.keyReleased(keyCode, scanCode, modifiers)) || super.keyReleased(keyCode, scanCode, modifiers);
    }
}
