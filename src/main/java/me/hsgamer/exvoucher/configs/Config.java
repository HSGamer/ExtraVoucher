package me.hsgamer.exvoucher.configs;

import com.google.common.base.Strings;
import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.utils.Utils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Config {

    private static final ExtraVoucher instance;
    private static final File folder;

    private static final Map<String, File> files;
    private static final Map<String, FileConfiguration> configs;

    static {
        instance = ExtraVoucher.getInstance();
        folder = instance.getDataFolder();

        files = new ConcurrentHashMap<>();
        configs = new ConcurrentHashMap<>();
    }

    private static void copySrc(InputStream input, File output) throws IOException {
        FileOutputStream writer = new FileOutputStream(output);

        byte[] BUFFER = new byte[1024];
        int len;
        while ((len = input.read(BUFFER)) != -1) writer.write(BUFFER, 0, len);

        writer.flush();
        writer.close();
        input.close();
    }

    public synchronized static void load(String path, String key, String fileName) {
        try {
            if (!files.containsKey(key)) {
                File file = new File(folder + (Strings.isNullOrEmpty(path) ? "" : (File.separator + path.replace('.', File.separatorChar))), fileName);

                if (!file.exists()) {
                    File parent = file.getParentFile();
                    if (!parent.exists()) parent.mkdirs();

                    if (file.createNewFile()) instance.getLogger().info("Created default " + file.getName() + " file.");

                    InputStream stream = Config.class.getResourceAsStream("/" + (Strings.isNullOrEmpty(path) ? "" : (path.replace('.', File.separatorChar)) + "/") + fileName);
                    if (stream != null) copySrc(stream, file);
                }

                FileConfiguration config = new YamlConfiguration();
                config.load(file);

                files.put(key, file);
                configs.put(fileName, config);
            } else configs.get(fileName).load(files.get(key));
            Utils.formatColor(configs.get(fileName));
        } catch (IOException | InvalidConfigurationException | NullPointerException error) {
            error.printStackTrace();
        }
    }

    public static void save(String key) {
        File configF = files.get(key);
        FileConfiguration config = configs.get(configF.getName());
        try {
            config.save(configF);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public static void reload(String key) {
        File configF = files.get(key);
        FileConfiguration config = configs.get(configF.getName());
        try {
            config.load(configF);
            Utils.formatColor(config);
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
    }

    public static void reloadAllConfigs() {
        for (String keys : files.keySet()) reload(keys);
    }

    public static FileConfiguration get(String key) {
        return configs.get(files.get(key).getName());
    }

}
