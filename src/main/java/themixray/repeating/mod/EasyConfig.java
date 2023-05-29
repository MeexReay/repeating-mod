package themixray.repeating.mod;

import org.yaml.snakeyaml.Yaml;

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
    private Yaml yaml;

    public EasyConfig(Path path, Map<String,Object> def) {
        this.path = path;
        this.file = path.toFile();
        this.data = new HashMap<>();
        this.yaml = new Yaml();
        
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

    private String toYaml(Map<String,Object> p) {
        return yaml.dump(p);
    }

    private Map<String,Object> toMap(String j) {
        return yaml.load(j);
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
            Files.write(path, toYaml(p).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
