package themixray.repeating.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import themixray.repeating.mod.widget.RecordListWidget;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RepeatingScreen extends Screen {
    private static List<RenderListener> render_listeners = new ArrayList<>();

    public ButtonWidget record_btn;
    public ButtonWidget loop_btn;
    public ButtonWidget import_btn;

    public SliderWidget pos_delay_slider;

    public boolean was_build = false;

    protected RepeatingScreen() {
        super(Text.empty());
    }

    public static void addRenderListener(RenderListener render) {
        render_listeners.add(render);
    }

    public static void removeRenderListener(RenderListener render) {
        render_listeners.remove(render);
    }

    public void updateButtons() {
        if (was_build) {
            record_btn.setMessage(Text.translatable("text.repeating-mod." + ((Main.me.is_recording) ? "stop_record" : "start_record")));
            loop_btn.setMessage(Text.translatable("text.repeating-mod." + ((Main.me.loop_replay) ? "off_loop" : "on_loop")));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context,mouseX,mouseY,delta);

        for (RenderListener l : render_listeners) {
            if (l.beforeRender()) {
                l.render(context, mouseX, mouseY, delta);
            }
        }

        super.render(context, mouseX, mouseY, delta);

        for (RenderListener l : render_listeners) {
            if (!l.beforeRender()) {
                l.render(context, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    protected void init() {
        RecordListWidget list_widget = Main.me.record_list.getWidget();

        list_widget.method_46421(width / 2 + 2);
        list_widget.method_46419(height / 2 - list_widget.getHeight() / 2);
        list_widget.init(this);


        record_btn = ButtonWidget.builder(
                        Text.translatable("text.repeating-mod.start_record"), button -> {
                            if (!Main.me.is_replaying) {
                                if (Main.me.is_recording)
                                    Main.me.stopRecording();
                                else Main.me.startRecording();
                                updateButtons();
                            }
                        })
                .dimensions(width / 2 - 120, height / 2 - 32, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("text.repeating-mod.record_tooltip")))
                .build();

        loop_btn = ButtonWidget.builder(Text.empty(), button -> {
                    Main.me.loop_replay = !Main.me.loop_replay;
                    updateButtons();
                })
                .dimensions(width / 2 - 120, height / 2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("text.repeating-mod.loop_tooltip")))
                .build();

        pos_delay_slider = new SliderWidget(
                width / 2 - 120, height / 2 + 12, 120, 20,
                (Main.me.record_pos_delay < 0) ? Text.translatable("text.repeating-mod.nan_pos_delay") :
                        Text.translatable("text.repeating-mod.pos_delay", String.valueOf(Main.me.record_pos_delay)),
                (Main.me.record_pos_delay+1d)/101d) {

            @Override
            protected void updateMessage() {
                double v = value*101d-1d;
                if (v <= 1) setMessage(Text.translatable("text.repeating-mod.nan_pos_delay"));
                else setMessage(Text.translatable("text.repeating-mod.pos_delay", String.valueOf((long) v)));
            }

            @Override
            protected void applyValue() {
                double v = value*101d-1d;
                if (v <= 1) setMessage(Text.translatable("text.repeating-mod.nan_pos_delay"));
                else setMessage(Text.translatable("text.repeating-mod.pos_delay", String.valueOf((long) v)));
                Main.me.record_pos_delay = (long) v;
                Main.me.conf.data.put("record_pos_delay",String.valueOf(Main.me.record_pos_delay));
                Main.me.conf.save();
            }

            @Override
            public void onRelease(double mouseX, double mouseY) {
                super.onRelease(mouseX, mouseY);
                applyValue();
            }

            @Override
            protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
                super.onDrag(mouseX, mouseY, deltaX, deltaY);
                applyValue();
            }

            @Override
            public void render(DrawContext context, int mouseX, int mouseY, float delta) {
                super.render(context, mouseX, mouseY, delta);
                updateMessage();
            }
        };
        pos_delay_slider.setTooltip(Tooltip.of(Text.translatable("text.repeating-mod.pos_delay_tooltip")));

        import_btn = ButtonWidget.builder(Text.translatable("text.repeating-mod.import"), button -> {
                    new Thread(() -> {
                        FileDialog fd = new FileDialog((java.awt.Frame) null);
                        fd.setMultipleMode(true);
                        fd.setName("Choose record files");
                        fd.setTitle("Choose record files");
                        fd.setFilenameFilter((dir, name) -> name.endsWith(".rrm"));
                        fd.setVisible(true);

                        File[] files = fd.getFiles();
                        if (files != null) {
                            for (File file : files) {
                                try {
                                    Main.me.record_list.addRecord(file);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }}).start();
                })
                .dimensions(width / 2 + 2, height / 2 - list_widget.getHeight() / 2 - 22, 180, 20)
                .tooltip(Tooltip.of(Text.translatable("text.repeating-mod.import_tooltip")))
                .build();

        was_build = true;

        updateButtons();

        addDrawableChild(loop_btn);
        addDrawableChild(record_btn);
        addDrawableChild(import_btn);
        addDrawableChild(pos_delay_slider);
    }

    public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        return super.addDrawableChild(drawableElement);
    }

    public <T extends Drawable> T addDrawable(T drawable) {
        return super.addDrawable(drawable);
    }

    public <T extends Element & Selectable> T addSelectableChild(T child) {
        return super.addSelectableChild(child);
    }

    public void remove(Element child) {
        super.remove(child);
    }
}
