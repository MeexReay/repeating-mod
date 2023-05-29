package themixray.repeating.mod;

import org.json.simple.parser.*;
import org.json.simple.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class EasyConfig {
    public final Path path;
    public final File file;
    public Map<String,Object> data;

    public EasyConfig(Path path, Map<String,Object> def) {
        this.path = path;
        this.file = path.toFile();
        this.data = new HashMap<>();
        
        if (!file.exists()) {
            try {
                file.createNewFile();
                write(def);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reload();
    }

    public EasyConfig(Path path) {
        this(path,new HashMap<>());
    }

    public void reload() {
        data = read();
    }
    
    public void save() {
        write(data);
    }

    private String toJson(Map<String,Object> p) {
        return JSONValue.toJSONString(p);
    }

    private Map<String,Object> toMap(String j) {
        return (Map<String, Object>) JSONValue.parse(j);
    }

    private Map<String,Object> read() {
        try {
            return toMap(Files.readString(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private void write(Map<String,Object> p) {
        try {
            Files.write(path,toJson(p).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
