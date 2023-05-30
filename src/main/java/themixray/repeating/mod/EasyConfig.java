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

    public EasyConfig(File f, Map<String,Object> def) {
        this.path = f.toPath();
        this.file = f;
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

        for (Map.Entry<String,Object> m:def.entrySet())
            if (!data.containsKey(m.getKey()))
                data.put(m.getKey(),m.getValue());

        save();
    }
    public EasyConfig(Path f, Map<String,Object> def) {
        this(f.toFile(),def);
    }
    public EasyConfig(String parent,String child,Map<String,Object> def) {
        this(new File(parent,child),def);
    }
    public EasyConfig(File parent,String child,Map<String,Object> def) {
        this(new File(parent,child),def);
    }
    public EasyConfig(Path parent,String child,Map<String,Object> def) {
        this(new File(parent.toFile(),child),def);
    }

    public EasyConfig(File f) {
        this(f,new HashMap<>());
    }
    public EasyConfig(Path path) {
        this(path.toFile(),new HashMap<>());
    }
    public EasyConfig(String parent,String child) {
        this(new File(parent,child),new HashMap<>());
    }
    public EasyConfig(File parent,String child) {
        this(new File(parent,child),new HashMap<>());
    }
    public EasyConfig(Path parent,String child) {
        this(new File(parent.toFile(),child),new HashMap<>());
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
        return (Map<String, Object>) yaml.load(j);
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
