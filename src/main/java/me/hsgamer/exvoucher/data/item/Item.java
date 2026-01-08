package me.hsgamer.exvoucher.data.item;

import io.github.projectunified.craftitem.spigot.core.SpigotItem;
import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.builder.ItemBuilder;
import me.hsgamer.exvoucher.builder.NBTBuilder;
import me.hsgamer.exvoucher.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class Item {

    private final ExtraVoucher instance = ExtraVoucher.getInstance();

    private final File file;
    private final FileConfiguration config;

    private final String id;

    private final Consumer<SpigotItem> item;

    private final Settings settings;
    private final Commands commands;

    Item(File file) {
        this.file = file;
        this.config = Utils.formatColor(YamlConfiguration.loadConfiguration(file));

        this.id = config.getString("Settings.ID");

        this.item = ItemBuilder.getItemStack(config, "Item");

        this.settings = new Settings(this);
        this.commands = new Commands(this);
    }

    public NBTBuilder getNBTBuilder() {
        return new NBTBuilder(this);
    }

    void saveConfig() {
        try {
            config.save(file);
        } catch (IOException error) {
            instance.getLogger().log(Level.SEVERE, "Could not save data for: " + file.getPath() + '.', error);
        }
    }

    FileConfiguration getConfig() {
        return this.config;
    }

    public String getId() {
        return this.id;
    }

    public Consumer<SpigotItem> getItem() {
        return this.item;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Commands getCommands() {
        return this.commands;
    }
}
