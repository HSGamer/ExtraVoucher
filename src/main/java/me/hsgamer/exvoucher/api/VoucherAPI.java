package me.hsgamer.exvoucher.api;

import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.data.giftcode.Giftcode;
import me.hsgamer.exvoucher.data.item.Item;
import me.hsgamer.exvoucher.data.user.User;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class VoucherAPI {

    private static VoucherAPI instance;
    private final ExtraVoucher main;

    private VoucherAPI() {
        main = ExtraVoucher.getInstance();
    }

    public static VoucherAPI getInstance() {
        if (instance == null) instance = new VoucherAPI();
        return instance;
    }

    public User getUser(UUID uuid) {
        return main.getUserManager().findByUUID(uuid);
    }

    public User getUser(Player player) {
        return main.getUserManager().findByPlayer(player);
    }

    @Deprecated
    public User getUser(String name) {
        return main.getUserManager().findByName(name);
    }

    public Item getItem(String id) {
        return main.getItemManager().findByID(id);
    }

    public Giftcode getGiftcode(String code) {
        return main.getGiftcodeManager().findByCode(code);
    }

}
