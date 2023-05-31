package themixray.repeating.mod;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Insets;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;

public class RepeatingScreen extends BaseOwoScreen<FlowLayout> {
    public RepeatingMod mod;
    public ButtonComponent replay_btn;
    public ButtonComponent record_btn;
    public ButtonComponent loop_btn;
    public boolean was_build = false;

    public RepeatingScreen() {
        this.mod = RepeatingMod.me;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::horizontalFlow);
    }

    public void update_btns() {
        if (was_build) {
            replay_btn.setMessage(Text.translatable("text.repeating-mod." +
                            ((mod.is_replaying) ? "stop" : "start")).append(" ")
                    .append(Text.translatable("text.repeating-mod.replay")));
            record_btn.setMessage(Text.translatable("text.repeating-mod." +
                            ((mod.is_recording) ? "stop" : "start")).append(" ")
                    .append(Text.translatable("text.repeating-mod.record")));
            loop_btn.setMessage(Text.of(((mod.loop_replay) ? "\uefff " : "\ueffe ")));
        }
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        replay_btn = (ButtonComponent) Components.button(Text.of("replay"),
                (ButtonComponent btn) -> {
                    if (!mod.is_recording) {
                        if (mod.is_replaying)
                            mod.stopReplay();
                        else mod.startReplay();
                        update_btns();
                    }
                }).margins(Insets.of(1)).sizing(
                Sizing.fixed(98),Sizing.fixed(20));

        loop_btn = (ButtonComponent) Components.button(Text.of(""),
                (ButtonComponent btn) -> {
                    mod.loop_replay = !mod.loop_replay;
                    update_btns();
                }).margins(Insets.of(1))
                .sizing(Sizing.fixed(20),Sizing.fixed(20));

        record_btn = (ButtonComponent) Components.button(Text.of("record"),
                (ButtonComponent btn) -> {
                    if (!mod.is_replaying) {
                        if (mod.is_recording)
                            mod.stopRecording();
                        else mod.startRecording();
                        update_btns();
                    }
                }).margins(Insets.of(1)).sizing(
                Sizing.fixed(120),Sizing.fixed(20));
        was_build = true;

        rootComponent.child(
            Containers.horizontalFlow(Sizing.content(), Sizing.content()).child(
                Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(Components.label(Text.translatable("text.repeating-mod.basic")).margins(Insets.of(1)))
                        .padding(Insets.of(5))
                        .surface(Surface.DARK_PANEL)
                        .verticalAlignment(VerticalAlignment.CENTER)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .margins(Insets.of(1)))
                    .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                .child(replay_btn).child(loop_btn))
                        .child(record_btn)
                        .child(Components.button(Text.translatable(
                            "text.repeating-mod.export"),
                            (ButtonComponent btn) -> {
                                String t = "";
                                for (int i = 0; i < mod.record.size(); i++) {
                                    t += mod.record.get(i).toText();
                                    if (i != mod.record.size()-1)
                                        t += "\n";
                                }

                                File p = new File(FabricLoader.getInstance().getGameDir().toFile(),"repeating");
                                if (!p.exists()) p.mkdir();
                                File file = new File(p,"export.txt");

                                try {
                                    if (!file.exists()) file.createNewFile();
                                    Files.write(file.toPath(), t.getBytes());
                                    Runtime.getRuntime().exec("explorer /select,\""+file.getAbsolutePath()+"\"");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).margins(Insets.of(10,1,1,1)).sizing(
                            Sizing.fixed(120),Sizing.fixed(20)))
                        .child(Components.button(Text.translatable(
                            "text.repeating-mod.import"),
                            (ButtonComponent btn) -> {
                                mod.record.clear();

                                File p = new File(FabricLoader.getInstance().getGameDir().toFile(),"repeating");
                                if (!p.exists()) p.mkdir();
                                File file = new File(p,"import.txt");

                                try {
                                    if (!file.exists()) {
                                        file.createNewFile();
                                        Runtime.getRuntime().exec("explorer /select,\""+file.getAbsolutePath()+"\"");
                                        return;
                                    }
                                    String t = Files.readString(file.toPath());
                                    for (String s:t.split("\n"))
                                        mod.record.add(RepeatingMod.RecordEvent.fromText(s));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).margins(Insets.of(1)).sizing(
                            Sizing.fixed(120),Sizing.fixed(20)))
                        .padding(Insets.of(10))
                        .surface(Surface.DARK_PANEL)
                        .verticalAlignment(VerticalAlignment.CENTER)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .margins(Insets.of(1)))
        /*).child(
            Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(Components.label(Text.translatable("text.repeating-mod.parkour")).margins(Insets.of(1)))
                    .padding(Insets.of(5))
                    .surface(Surface.DARK_PANEL)
                    .verticalAlignment(VerticalAlignment.CENTER)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .margins(Insets.of(1)))
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(Components.label(Text.translatable("text.repeating-mod.dev")).margins(Insets.of(1)))
                    .padding(Insets.of(10))
                    .surface(Surface.DARK_PANEL)
                    .verticalAlignment(VerticalAlignment.CENTER)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .margins(Insets.of(1)))*/
        ).child(
            Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(Components.label(Text.translatable("text.repeating-mod.settings")).margins(Insets.of(1)))
                    .padding(Insets.of(5))
                    .surface(Surface.DARK_PANEL)
                    .verticalAlignment(VerticalAlignment.CENTER)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .margins(Insets.of(1)))
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(Components.discreteSlider(Sizing.fixed(120),-20,100)
                        .setFromDiscreteValue(mod.record_pos_delay)
                        .message((String s)->{
                            mod.record_pos_delay = Long.parseLong(s);
                            mod.conf.data.put("record_pos_delay",String.valueOf(mod.record_pos_delay));
                            mod.conf.save();
                            if (mod.record_pos_delay > -1)
                                return Text.translatable("text.repeating-mod.pos_delay", s);
                            return Text.translatable("text.repeating-mod.nan_pos_delay");
                        }).scrollStep(25)
                        .margins(Insets.of(1))
                        .tooltip(Text.translatable("text.repeating-mod.pos_delay_text")))
                    .padding(Insets.of(10))
                    .surface(Surface.DARK_PANEL)
                    .verticalAlignment(VerticalAlignment.CENTER)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .margins(Insets.of(1)))
        ));
        update_btns();
    }
}
