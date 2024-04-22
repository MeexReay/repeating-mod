package themixray.repeating.mod;

import com.google.common.collect.Lists;
import net.minecraft.util.math.Vec3d;
import themixray.repeating.mod.event.RecordEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordState {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    public static SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");

    private final File file;
    private String name;
    private Date date;
    private String author;

    private List<RecordEvent> events;
    private Vec3d start_record_pos;
    private Vec3d finish_record_pos;

    public RecordState(File file,
                       String name,
                       Date date,
                       String author,
                       List<RecordEvent> events,
                       Vec3d start_record_pos,
                       Vec3d finish_record_pos) {
        this.file = file;
        this.name = name;
        this.date = date;
        this.author = author;

        this.events = events;
        this.start_record_pos = start_record_pos;
        this.finish_record_pos = finish_record_pos;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public List<RecordEvent> getEvents() {
        return events;
    }

    public Vec3d getFinishRecordPos() {
        return finish_record_pos;
    }

    public Vec3d getStartRecordPos() {
        return start_record_pos;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEvents(List<RecordEvent> events) {
        this.events = events;
    }

    public void setFinishRecordPos(Vec3d finish_record_pos) {
        this.finish_record_pos = finish_record_pos;
    }

    public void setStartRecordPos(Vec3d start_record_pos) {
        this.start_record_pos = start_record_pos;
    }

    public void addEvent(RecordEvent event) {
        events.add(event);
    }

    public RecordEvent getLastEvent(String type) {
        for (RecordEvent r: Lists.reverse(new ArrayList<>(events))) {
            if (r.getType().equals(type)) {
                return r;
            }
        }
        return null;
    }

    public void save() throws IOException {
        if (start_record_pos == null || finish_record_pos == null) return;

        StringBuilder text = new StringBuilder();

        text.append(name).append("\n")
                .append(DATE_FORMAT.format(date)).append("\n")
                .append(author).append("\n");

        text.append(start_record_pos.getX()).append("n")
                .append(start_record_pos.getY()).append("n")
                .append(start_record_pos.getZ()).append("x")
                .append(finish_record_pos.getX()).append("n")
                .append(finish_record_pos.getY()).append("n")
                .append(finish_record_pos.getZ());

        for (int i = 0; i < events.size(); i++) {
            text.append("\n");
            text.append(events.get(i).serialize());
        }

        Files.write(file.toPath(), text.toString().getBytes());
    }

    public static RecordState load(File file) throws Exception {
        String text = Files.readString(file.toPath());
        List<String> lines = List.of(text.split("\n"));

        List<String> signature = lines.subList(0,4);

        String name = signature.get(0);
        Date date = DATE_FORMAT.parse(signature.get(1));
        String author = signature.get(2);

        String record_pos = signature.get(3);

        String[] lss0 = record_pos.split("x");
        String[] lss1 = lss0[0].split("n");
        String[] lss2 = lss0[1].split("n");

        Vec3d start_record_pos = new Vec3d(
                Float.parseFloat(lss1[0]),
                Float.parseFloat(lss1[1]),
                Float.parseFloat(lss1[2]));
        Vec3d finish_record_pos = new Vec3d(
                Float.parseFloat(lss2[0]),
                Float.parseFloat(lss2[1]),
                Float.parseFloat(lss2[2]));

        List<String> event_lines = lines.subList(4,lines.size());
        List<RecordEvent> events = event_lines.stream().map(RecordEvent::deserialize).toList();

        return new RecordState(file, name, date, author, events, start_record_pos, finish_record_pos);
    }

    public void remove() {
        Main.me.record_list.removeRecord(this);
        Main.me.record_list.getWidget().removeWidget(this);
        if (Main.me.is_recording && this.equals(Main.me.now_record)) {
            Main.me.stopRecording();
            Main.me.now_record = null;
            return;
        }
        file.delete();
    }
}
