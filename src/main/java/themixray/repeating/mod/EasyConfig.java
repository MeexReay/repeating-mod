package themixray.repeating.mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class EasyConfig {
    public final Path path;
    public final File file;
    public Map<String,String> data;

    public EasyConfig(File f, Map<String,String> def) {
        this.path = f.toPath();
        this.file = f;
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

        for (Map.Entry<String,String> m:def.entrySet())
            if (!data.containsKey(m.getKey()))
                data.put(m.getKey(),m.getValue());

        save();
    }
    public EasyConfig(Path f, Map<String,String> def) {
        this(f.toFile(),def);
    }
    public EasyConfig(String parent,String child,Map<String,String> def) {
        this(new File(parent,child),def);
    }
    public EasyConfig(File parent,String child,Map<String,String> def) {
        this(new File(parent,child),def);
    }
    public EasyConfig(Path parent,String child,Map<String,String> def) {
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

    private String toText(Map<String,String> p) {
        StringBuilder t = new StringBuilder();
        for (Map.Entry<String,String> e:p.entrySet())
            t.append(e.getKey()).append("=").append(e.getValue()).append("\n");
        return t.toString();
    }
    private Map<String,String> toMap(String j) {
        Map<String,String> m = new HashMap<>();
        for (String l:j.split("\n")) {
            String s[] = l.split("=");
            m.put(s[0],s[1]);
        }
        return m;
    }

    private Map<String,String> read() {
        try {
            return toMap(Files.readString(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
    private void write(Map<String,String> p) {
        try {
            Files.write(path, toText(p).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
