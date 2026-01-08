package me.hsgamer.exvoucher.data.giftcode;

import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.configs.Config;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public final class GiftcodeManager {

    private final ExtraVoucher instance;
    private final Map<String, Giftcode> giftcodes;
    private FileConfiguration config;

    public GiftcodeManager(ExtraVoucher instance) {
        this.instance = instance;
        this.giftcodes = new HashMap<>();
        this.loadGiftcodes();
    }

    private void loadGiftcodes() {
        this.config = Config.get("giftcode");
        for (String code : config.getKeys(false)) {
            if (!this.checkValid(code)) continue;
            giftcodes.put(code, new Giftcode(code));
        }
        instance.getLogger().info("Successfully loaded " + giftcodes.size() + " giftcode(s).");
    }

    public void reloadGiftcodes() {
        giftcodes.clear();
        this.loadGiftcodes();
    }

    public Giftcode findByCode(String code) {
        return giftcodes.getOrDefault(code, null);
    }

    private boolean checkValid(String code) {
        if (!code.matches("[a-zA-Z0-9_]+")) {
            instance.getLogger().severe("The code=[" + code + "] in [giftcode.yml] does not match regular expression: [a-zA-Z0-9_]+");
            return false;
        }
        if (giftcodes.containsKey(code)) {
            instance.getLogger().severe("The code=[" + code + "] must be unique!");
            return false;
        }
        return true;
    }

}
