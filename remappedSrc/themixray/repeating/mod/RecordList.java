package themixray.repeating.mod;

import net.minecraft.text.Text;
import themixray.repeating.mod.widget.RecordListWidget;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordList {
    private final File folder;
    private List<RecordState> records;
    private RecordListWidget widget;

    public RecordList(File folder) {
        this.folder = folder;
        this.records = new ArrayList<>();
        this.widget = new RecordListWidget(0, 0, 180, 200);
    }

    public List<RecordState> getRecords() {
        return records;
    }

    public File getFolder() {
        return folder;
    }

    public RecordListWidget getWidget() {
        return widget;
    }

    public void loadRecords() {
        for (File file : folder.listFiles()) {
            try {
                addRecord(file);
            } catch (Exception e) {}
        }
    }

    public void addRecord(File file) throws Exception {
        addRecord(RecordState.load(file));
    }

    public void addRecord(RecordState record) {
        records.add(record);
        widget.addWidget(record);
    }

    public void removeRecord(RecordState record) {
        records.remove(record);
        widget.removeWidget(record);
    }

    public RecordState newRecord() {
        Date date = new Date();
        String name = "Unnamed";
        String author = Main.client.player.getName().getString();

        File file = new File(Main.me.records_folder,
            "record_" + RecordState.FILE_DATE_FORMAT.format(date) +
                "_" + Main.rand.nextInt(10) + ".rrm");

        RecordState state = new RecordState(
                file, name, date, author,
                new ArrayList<>(),
                null,
                null);

        addRecord(state);

        return state;
    }
}
