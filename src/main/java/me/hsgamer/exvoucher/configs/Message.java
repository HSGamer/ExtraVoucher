package me.hsgamer.exvoucher.configs;

import me.hsgamer.exvoucher.utils.Utils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class Message {

    private static final Map<String, Object> maps = new ConcurrentHashMap<>();
    private static FileConfiguration config;
    private static String prefix;

    private Message() {
    }

    public static void loadMessages() {
        config = Config.get("message");

        prefix = config.getString("PREFIX");

        maps.clear();
        config.getKeys(true).forEach(key -> {
            if ((key.equals("PREFIX")) || (config.isConfigurationSection(key))) return;
            Object val = config.get(key);
            if (val instanceof String) val = String.valueOf(val).replaceAll(Utils.getRegex("prefix"), prefix);
            else if (val instanceof List)
                val = ((List<?>) val).stream()
                        .map(obj -> String.valueOf(obj).replaceAll(Utils.getRegex("prefix"), prefix))
                        .collect(Collectors.toList());
            maps.put(key, val);
        });
    }


    public static String getMessage(String path) {
        Validate.isTrue(maps.containsKey(path), "Could not find the message path: " + path + " in the messages.yml file!");
        return String.valueOf(maps.get(path));
    }

    public static String getPrefix() {
        return prefix;
    }
}
