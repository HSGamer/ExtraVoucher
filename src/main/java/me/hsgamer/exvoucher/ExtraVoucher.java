package me.hsgamer.exvoucher;

import io.github.projectunified.craftux.spigot.SpigotInventoryUIListener;
import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.exvoucher.commands.Commands;
import me.hsgamer.exvoucher.commands.handler.CommandHandler;
import me.hsgamer.exvoucher.configs.Config;
import me.hsgamer.exvoucher.configs.Message;
import me.hsgamer.exvoucher.configs.Setting;
import me.hsgamer.exvoucher.data.giftcode.GiftcodeManager;
import me.hsgamer.exvoucher.data.id.ItemIDManager;
import me.hsgamer.exvoucher.data.item.ItemManager;
import me.hsgamer.exvoucher.data.user.UserManager;
import me.hsgamer.exvoucher.gui.ListGUI;
import me.hsgamer.exvoucher.listeners.PlayerListener;
import me.hsgamer.hscore.license.common.LicenseStatus;
import me.hsgamer.hscore.license.polymart.PolymartLicenseChecker;
import me.hsgamer.hscore.license.spigotmc.SpigotLicenseChecker;
import me.hsgamer.hscore.license.template.LicenseTemplate;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public final class ExtraVoucher extends JavaPlugin {

    private static ExtraVoucher instance;

    private ItemManager itemManager;

    private UserManager userManager;

    private GiftcodeManager giftcodeManager;

    private ItemIDManager itemIDManager;

    private ListGUI listGUI;

    public static ExtraVoucher getInstance() {
        return ExtraVoucher.instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        new Metrics(this, 13670);

        this.loadFile();
        this.loadData();
        this.registerCommands();
        this.registerEvents();

        getLogger().info("Plugin loaded successfully!");
        checkLicense();
    }

    @Override
    public void onDisable() {
        if (userManager != null) userManager.unregister();
        HandlerList.unregisterAll(this);
    }

    private void loadFile() {
        this.getDataFolder().mkdirs();

        Config.load(null, "config", "config.yml");
        Config.load(null, "message", "messages.yml");
        Config.load(null, "giftcode", "giftcode.yml");
    }

    private void loadData() {
        Setting.loadSetting();
        Message.loadMessages();

        this.itemIDManager = new ItemIDManager(this);
        this.itemManager = new ItemManager(this);
        this.userManager = new UserManager(this);
        userManager.register();
        this.giftcodeManager = new GiftcodeManager(this);

        this.listGUI = new ListGUI(this);
    }

    private void registerCommands() {
        CommandHandler handler = new CommandHandler();
        handler.addPrimaryCommand(new Commands());
    }

    private void registerEvents() {
        new PlayerListener(this);
        new SpigotInventoryUIListener(this).register();
    }

    public void callEvent(Event event) {
        getServer().getPluginManager().callEvent(event);
    }

    private void checkLicense() {
        LicenseTemplate template = new LicenseTemplate(new SpigotLicenseChecker("99117"), new PolymartLicenseChecker("1882", true, true));
        template.addDefaultMessage(this.getName());
        AsyncScheduler.get(this).run(() -> {
            Map.Entry<LicenseStatus, List<String>> result = template.getResult();
            result.getValue().forEach(result.getKey() == LicenseStatus.VALID ? getLogger()::info : getLogger()::warning);
        });
    }

    public ItemManager getItemManager() {
        return this.itemManager;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public GiftcodeManager getGiftcodeManager() {
        return this.giftcodeManager;
    }

    public ItemIDManager getItemIDManager() {
        return this.itemIDManager;
    }

    public ListGUI getListGUI() {
        return this.listGUI;
    }
}
