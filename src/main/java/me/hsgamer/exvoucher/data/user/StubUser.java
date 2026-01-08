package me.hsgamer.exvoucher.data.user;

import me.hsgamer.exvoucher.data.giftcode.Giftcode;
import me.hsgamer.exvoucher.data.item.Item;
import me.hsgamer.topper.data.core.DataEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class StubUser implements User {
    private final DataEntry<UUID, UserEntry> entry;
    private final UserManager userManager;
    private final OfflinePlayer offPlayer;

    public StubUser(DataEntry<UUID, UserEntry> entry, UserManager userManager) {
        this.entry = entry;
        this.userManager = userManager;
        this.offPlayer = Bukkit.getOfflinePlayer(entry.getKey());
    }

    @Override
    public boolean canClick() {
        return userManager.canClick(entry.getKey());
    }

    @Override
    public String getBase64() {
        return entry.getValue().base64();
    }

    @Override
    public boolean isUsedItem(Item item) {
        return entry.getValue().usedItems().containsKey(item.getId());
    }

    @Override
    public void setUsedItem(Item item) {
        entry.setValue(userEntry -> userEntry.withUsedItem(item.getId(), System.currentTimeMillis()));
    }

    @Override
    public boolean isRedeemedGiftcode(Giftcode giftcode) {
        return entry.getValue().usedGiftcodes().containsKey(giftcode.getCode());
    }

    @Override
    public void setRedeemedGiftcode(Giftcode giftcode) {
        entry.setValue(userEntry -> userEntry.withUsedGiftcode(giftcode.getCode(), System.currentTimeMillis()));
    }

    @Override
    public OfflinePlayer getOffPlayer() {
        return offPlayer;
    }
}
