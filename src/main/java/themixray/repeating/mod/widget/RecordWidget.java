package themixray.repeating.mod.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import themixray.repeating.mod.Main;
import themixray.repeating.mod.RecordState;
import themixray.repeating.mod.RenderListener;
import themixray.repeating.mod.RepeatingScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.function.Consumer;

public class RecordWidget implements Drawable, Widget {
    private RecordState record;

    private List<ClickableWidget> children;

    private RecordListWidget parent;

    private int x;
    private int y;
    private int width;
    private int height;

    public RecordWidget(int x, int y, int width, int height, RecordState record, RecordListWidget parent) {
        this.parent = parent;
        this.record = record;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.children = new ArrayList<>();
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public List<ClickableWidget> getChildren() {
        return children;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        children.forEach(consumer);
    }

    public void init(RepeatingScreen screen) {
        this.children = new ArrayList<>();

        TextFieldWidget name_widget = new TextFieldWidget(
                Main.client.textRenderer,
                parent.getX() + getX() + 5,
                parent.getY() + getY() + 5,
                70,
                10,
                Text.empty());

        name_widget.setText(record.getName());

        name_widget.setChangedListener((s) -> {
            record.setName(s);
            try {
                record.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        children.add(name_widget);

        ButtonWidget delete_button = ButtonWidget.builder(Text.translatable("text.repeating-mod.delete"), (i) -> {
            record.remove();
        }).dimensions(parent.getX() + getX() + 77,parent.getY() + getY() + 4, 38, 13).build();

        children.add(delete_button);

        ButtonWidget export_button = ButtonWidget.builder(Text.translatable("text.repeating-mod.export"), (i) -> {
            if (Desktop.isDesktopSupported()) {
                Desktop desk = Desktop.getDesktop();
                try {
                    desk.browseFileDirectory(record.getFile());
                } catch (Exception e) {
                    try {
                        desk.browse(record.getFile().toURI());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }).dimensions(parent.getX() + getX() + 77,parent.getY() + getY() + 4 + 14, 38, 13).build();

        children.add(export_button);

        ButtonWidget replay_button = ButtonWidget.builder(Text.translatable("text.repeating-mod.start"), (i) -> {
            if (Main.me.is_replaying) {
                Main.me.stopReplay();
            }

            i.setMessage(Text.translatable("text.repeating-mod.stop"));
            Main.me.now_record = record;
            Main.me.startReplay();
        }).dimensions(parent.getX() + getX() + 77,parent.getY() + getY() + 4 + 28, 38, 13)
                .tooltip(Tooltip.of(Text.translatable("text.repeating-mod.replay_tooltip"))).build();

        children.add(replay_button);
    }

    public RecordState getRecord() {
        return record;
    }

    public void drawText(int x, int y, DrawContext ctx, List<Text> lines, float size, int line_height, boolean shadow) {
        ctx.getMatrices().push();
        ctx.getMatrices().scale(size, size, size);

        int now_y = y;

        for (Text line : lines) {
            ctx.drawText(Main.client.textRenderer, line, (int) (x / size), (int) (now_y / size), line.getStyle().getColor().getRgb(), shadow);
            now_y += line_height;
        }

        ctx.getMatrices().pop();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int color = record.equals(Main.me.now_record) ? 0xFF555555 : 0xFF333333;

        ctx.fill(parent.getX() + getX(),
                parent.getY() + getY(),
                parent.getX() + getX() + getWidth(),
                parent.getY() + getY() + getHeight(),
                color);

        drawText(
            parent.getX() + getX() + 5,
            parent.getY() + getY() + 5 + 12,
            ctx, List.of(
                Text.translatable("text.repeating-mod.recorded_at")
                        .append(": ")
                        .styled((s) -> s.withColor(0xbbbbbb)),
                Text.literal(RecordState.DATE_FORMAT.format(record.getDate())).styled((s) -> s.withColor(0xffffff)),
                Text.translatable("text.repeating-mod.author")
                        .append(": ")
                        .styled((s) -> s.withColor(0xbbbbbb)),
                Text.literal(record.getAuthor()).styled((s) -> s.withColor(0xffffff))
            ), 0.7f,
            7,
            false);

        if (!children.isEmpty()) {
            ClickableWidget name_widget = children.get(0);
            name_widget.setPosition(parent.getX() + getX() + 5, parent.getY() + getY() + 5);

            ClickableWidget delete_button = children.get(1);
            delete_button.setPosition(parent.getX() + getX() + 77,parent.getY() + getY() + 4);

            ClickableWidget export_button = children.get(2);
            export_button.setPosition(parent.getX() + getX() + 77,parent.getY() + getY() + 4 + 14);

            ClickableWidget replay_button = children.get(3);
            replay_button.setPosition(parent.getX() + getX() + 77,parent.getY() + getY() + 4 + 28);
        }

        for (ClickableWidget child : children) {
            child.render(ctx, mouseX, mouseY, delta);
        }
    }
}
