package me.hsgamer.exvoucher.data.item;

import me.hsgamer.exvoucher.ExtraVoucher;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class ItemManager {

    private final ExtraVoucher instance;
    private final File itemFolder;
    private final Map<String, Item> items;

    public ItemManager(ExtraVoucher instance) {
        this.instance = instance;
        this.itemFolder = new File(instance.getDataFolder(), "items");
        this.items = new HashMap<>();
        this.loadItems();
    }

    private void loadItems() {
        if (((!itemFolder.exists()) && itemFolder.mkdirs()) || (itemFolder.list().length < 1))
            instance.saveResource("items/example.yml", false);
        for (File file : itemFolder.listFiles()) {
            String id = this.getID(file);
            if (id == null) continue;
            items.put(id, new Item(file));
        }
        instance.getLogger().info("Successfully loaded " + items.size() + " item(s).");
    }

    public void reloadItems() {
        items.clear();
        this.loadItems();
    }

    public Item findByID(String id) {
        return items.getOrDefault(id, null);
    }

    private String getID(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String id = config.getString("Settings.ID", "");
        if (id.isEmpty()) {
            instance.getLogger().severe("The item ID in [" + file.getPath() + "] must not be null or empty!");
            return null;
        }
        if (!id.matches("[a-zA-Z0-9_]+")) {
            instance.getLogger().severe("The item ID in [" + file.getPath() + "] does not match: [a-zA-Z0-9_]+");
            return null;
        }
        if (items.containsKey(id)) {
            instance.getLogger().severe("The item ID in [" + file.getPath() + "] must be unique!");
            return null;
        }
        return id;
    }

    public File getItemFolder() {
        return this.itemFolder;
    }

    public Map<String, Item> getItems() {
        return this.items;
    }
}
