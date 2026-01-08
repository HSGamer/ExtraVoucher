package me.hsgamer.exvoucher.listeners;

import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.api.events.ItemConsumingEvent;
import me.hsgamer.exvoucher.configs.Message;
import me.hsgamer.exvoucher.data.Constants;
import me.hsgamer.exvoucher.data.id.ItemID;
import me.hsgamer.exvoucher.data.item.Item;
import me.hsgamer.exvoucher.data.item.Settings;
import me.hsgamer.exvoucher.data.user.User;
import me.hsgamer.exvoucher.data.user.UserManager;
import me.hsgamer.exvoucher.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class PlayerListener
        extends BaseListener {

    private final List<String> antiDoubleClick;
    private final Map<UUID, ItemID> confirm;

    public PlayerListener(ExtraVoucher instance) {
        super(instance);
        this.antiDoubleClick = new ArrayList<>();
        this.confirm = new HashMap<>();
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UserManager manager = instance.getUserManager();
        manager.getOrCreateEntry(player.getUniqueId());
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
        final ItemStack iStack = event.getItemInHand();
        if ((iStack == null) || (iStack.getType() == Material.AIR) || instance.getItemIDManager().getItemID(iStack).isPresent())
            return;
        event.setCancelled(true);
    }

    @EventHandler
    void onClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        boolean leftClick = false;

        final Action action = event.getAction();
        if (action == Action.PHYSICAL) return;
        else if (action == Action.RIGHT_CLICK_BLOCK) {
            if (antiDoubleClick.contains(player.getName())) {
                antiDoubleClick.remove(player.getName());
                return;
            } else antiDoubleClick.add(player.getName());
        } else if (action != Action.RIGHT_CLICK_AIR) leftClick = true;

        final ItemStack iStack = event.getItem();
        if ((iStack == null) || (iStack.getType() == Material.AIR)) return;

        Optional<ItemID> optionalItemID = instance.getItemIDManager().getItemID(iStack);
        if (optionalItemID.isEmpty()) return;
        ItemID itemID = optionalItemID.get();

        final User user = instance.getUserManager().findByPlayer(player);
        event.setCancelled(true);

        if ((!user.hasPermission(Constants.BYPASS_SLOWCLICK_PERMISSION)) && (!user.canClick())) {
            player.sendMessage(Message.getMessage("FAIL.must-click-slowly"));
            return;
        }

        final Item item = instance.getItemManager().findByID(itemID.getID());
        if (item == null) return;
        final Settings settings = item.getSettings();

        if ((!user.hasPermission(Constants.BYPASS_LOCKED_PERMISSION)) && settings.isLocked()) {
            player.sendMessage(Message.getMessage("FAIL.item-locked"));
            return;
        }

        final ItemConsumingEvent consumingEvent = new ItemConsumingEvent(player, iStack);
        instance.callEvent(consumingEvent);
        if (consumingEvent.isCancelled()) return;

        String expiryDate = settings.getExpiryDate();
        if ((!user.hasPermission(Constants.BYPASS_EXPIRYDATE_PERMISSION)) && DateUtils.isExpired(expiryDate)) {
            player.sendMessage(Message.getMessage("FAIL.item-expired"));
            return;
        }

        if (((!user.hasPermission(Constants.BYPASS_CONDITIONS_PERMISSION)) && (!settings.checkConditions(user))) ||
                ((!user.hasPermission(Constants.BYPASS_PERMISSIONS_PERMISSION)) && (!settings.checkPermission(user))) ||
                ((!user.hasPermission(Constants.BYPASS_PLAYERS_PERMISSION)) && (!settings.checkPlayer(user))) ||
                ((!user.hasPermission(Constants.BYPASS_WORLDS_PERMISSION)) && (!settings.checkWorld(user)))) {
            player.sendMessage(Message.getMessage("FAIL.cannot-use-item"));
            return;
        }

        final UUID uuid = player.getUniqueId();
        if ((!user.hasPermission(Constants.BYPASS_CONFIRMUSE_PERMISSION)) && settings.isConfirmUse() && (!confirm.containsKey(uuid))) {
            confirm.put(uuid, itemID);
            player.sendMessage(Message.getMessage("SUCCESS.confirm-use"));
            return;
        }
        confirm.remove(uuid);

        if ((!user.hasPermission(Constants.BYPASS_ONETIMEUSE_PERMISSION)) && settings.isOneTimeUse() && user.isUsedItem(item)) {
            player.sendMessage(Message.getMessage("FAIL.item-already-used"));
            return;
        }

        if (!user.hasPermission(Constants.BYPASS_LIMITOFUSE_PERMISSION)) {
            int uses = settings.getLimitOfUse();
            if (uses != -1) {
                if (uses <= 0) {
                    player.sendMessage(Message.getMessage("FAIL.end-of-usable"));
                    return;
                }
                settings.setLimitOfUse(--uses);
            }
        }
        if ((!user.hasPermission(Constants.BYPASS_REMOVEONUSE_PERMISSION)) && settings.isRemoveOnUse()) {
            int cur = iStack.getAmount();
            if (cur > 1) iStack.setAmount(--cur);
            else player.getInventory().removeItem(iStack);
        }

        user.useItem(item, itemID.getArguments(), leftClick);
    }

}
