package themixray.repeating.mod;

import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import themixray.repeating.mod.events.RecordEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class RecordFile {
    private final File file;
    private String name;
    private String date;
    private String author;

    private List<RecordEvent> events;
    private Vec3d start_record_pos;
    private Vec3d finish_record_pos;

    public RecordFile(File file,
                      String name,
                      String date,
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

    public String getDate() {
        return date;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void writeToMod() {

    }

    public static void readFromMod() {

    }

    public void writeToFile(File file) {

    }

    public static RecordFile readFromFile(File file) throws IOException {
        String text = Files.readString(file.toPath());
        List<String> lines = List.of(text.split("\n"));

        List<String> signature = lines.subList(0,4);

        String name = signature.get(0);
        String data = signature.get(1);
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

        return new RecordFile(file, )
    }
}
