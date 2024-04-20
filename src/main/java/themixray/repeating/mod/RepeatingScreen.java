package themixray.repeating.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import themixray.repeating.mod.events.RecordEvent;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RepeatingScreen extends Screen {
    protected RepeatingScreen() {
        super(Text.literal(""));
        this.mod = RepeatingMod.me;
    }

    public RepeatingMod mod;

    public ButtonWidget record_btn;
    public ButtonWidget replay_btn;
    public ButtonWidget loop_btn;

    public ButtonWidget export_btn;
    public ButtonWidget import_btn;

    public SliderWidget pos_delay_slider;

    public boolean was_build = false;

    public void update_btns() {
        if (was_build) {
            replay_btn.setMessage(Text.translatable("text.repeating-mod." +
                            ((mod.is_replaying) ? "stop_replay" : "start_replay")));
            record_btn.setMessage(Text.translatable("text.repeating-mod." +
                            ((mod.is_recording) ? "stop_record" : "start_record")));
            loop_btn.setMessage(Text.of(((mod.loop_replay) ? "\uefff " : "\ueffe ")));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        record_btn = ButtonWidget.builder(
                Text.translatable("text.repeating-mod.start_record"), button -> {
                    if (!mod.is_replaying) {
                        if (mod.is_recording)
                            mod.stopRecording();
                        else mod.startRecording();
                        update_btns();
                    }
                })
                .dimensions(width / 2 - 60, height / 2 - 54, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("text.repeating-mod.record_tooltip")))
                .build();

        replay_btn = ButtonWidget.builder(
                Text.translatable("text.repeating-mod.start_replay"), button -> {
                    if (!mod.is_recording) {
                        if (mod.is_replaying)
                            mod.stopReplay();
                        else mod.startReplay();
                        update_btns();
                    }
                })
                .dimensions(width / 2 - 60, height / 2 - 32, 98, 20)
                .tooltip(Tooltip.of(Text.translatable("text.repeating-mod.replay_tooltip")))
                .build();

        loop_btn = ButtonWidget.builder(Text.of(""), button -> {
                    mod.loop_replay = !mod.loop_replay;
                    update_btns();
                })
                .dimensions(width / 2 + 40, height / 2 - 32, 20, 20)
                .tooltip(Tooltip.of(Text.translatable("text.repeating-mod.loop_tooltip")))
                .build();

        export_btn = ButtonWidget.builder(
                Text.translatable("text.repeating-mod.export"), button -> {
                    if (mod.finish_record_pos == null) return;
                    StringBuilder t = new StringBuilder();
                    for (int i = 0; i < mod.record.size(); i++) {
                        t.append(mod.record.get(i).serialize());
                        t.append("\n");
                    }
                    t.append(mod.start_record_pos.getX()+"n"+
                            mod.start_record_pos.getY()+"n"+
                            mod.start_record_pos.getZ()+"x"+
                            mod.finish_record_pos.getX()+"n"+
                            mod.finish_record_pos.getY()+"n"+
                            mod.finish_record_pos.getZ());

                    File p = new File(FabricLoader.getInstance().getGameDir().toFile(),"repeating");
                    if (!p.exists()) p.mkdir();
                    File file = new File(p,"export_"+
                            new SimpleDateFormat("MM_dd_yyyy").format(new Date())
                            +"_"+RepeatingMod.rand.nextInt(10)+".txt");
                    try {
                        if (!file.exists()) file.createNewFile();
                        Files.write(file.toPath(), t.toString().getBytes());
                        Runtime.getRuntime().exec("explorer /select,\""+file.getAbsolutePath()+"\"");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .dimensions(width / 2 - 60, height / 2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("text.repeating-mod.export_tooltip")))
                .build();

        import_btn = ButtonWidget.builder(
                Text.translatable("text.repeating-mod.import"), button -> {
                    mod.record.clear();

                    File p = new File(FabricLoader.getInstance().getGameDir().toFile(),"repeating");
                    if (!p.exists()) p.mkdir();

                    JFileChooser fc = new JFileChooser() {
                        @Override
                        protected JDialog createDialog(Component parent) throws HeadlessException {
                            JDialog dialog = super.createDialog(parent);
                            dialog.setLocationByPlatform(true);
                            dialog.setAlwaysOnTop(true);
                            return dialog;
                        }

                    };

                    int retValue = fc.showOpenDialog(null);
                    if (retValue == JFileChooser.APPROVE_OPTION){
                        File file = fc.getSelectedFile();
                        try {
                            String t = Files.readString(file.toPath());
                            List<String> ss = List.of(t.split("\n"));
                            String ls = ss.get(ss.size()-1);
                            ss = ss.subList(0,ss.size()-1);
                            for (String s:ss)
                                mod.record.add(RecordEvent.deserialize(s));
                            String[] lss0 = ls.split("x");
                            String[] lss1 = lss0[0].split("n");
                            String[] lss2 = lss0[1].split("n");
                            mod.start_record_pos = new Vec3d(
                                    Float.parseFloat(lss1[0]),
                                    Float.parseFloat(lss1[1]),
                                    Float.parseFloat(lss1[2]));
                            mod.finish_record_pos = new Vec3d(
                                    Float.parseFloat(lss2[0]),
                                    Float.parseFloat(lss2[1]),
                                    Float.parseFloat(lss2[2]));
                            RepeatingMod.sendMessage(Text.literal(""));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .dimensions(width / 2 - 60, height / 2 + 12, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("text.repeating-mod.import_tooltip")))
                .build();

        pos_delay_slider = new SliderWidget(
                width / 2 - 60, height / 2 + 34, 120, 20,
                (mod.record_pos_delay < 0) ? Text.translatable("text.repeating-mod.nan_pos_delay") :
                Text.translatable("text.repeating-mod.pos_delay", String.valueOf(mod.record_pos_delay)),
                (mod.record_pos_delay+1d)/101d) {

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
                mod.record_pos_delay = (long) v;
                mod.conf.data.put("record_pos_delay",String.valueOf(mod.record_pos_delay));
                mod.conf.save();
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

        was_build = true;

        update_btns();

        addDrawableChild(replay_btn);
        addDrawableChild(loop_btn);
        addDrawableChild(record_btn);
        addDrawableChild(export_btn);
        addDrawableChild(import_btn);
        addDrawableChild(pos_delay_slider);
    }
}
