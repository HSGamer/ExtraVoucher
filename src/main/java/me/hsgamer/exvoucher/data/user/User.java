package me.hsgamer.exvoucher.data.user;

import me.hsgamer.exvoucher.configs.Message;
import me.hsgamer.exvoucher.data.Constants;
import me.hsgamer.exvoucher.data.giftcode.Giftcode;
import me.hsgamer.exvoucher.data.item.Commands;
import me.hsgamer.exvoucher.data.item.Item;
import me.hsgamer.exvoucher.utils.Utils;
import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface User {
    default int getSpaceLeft(ItemStack item) {
        final Player player = this.getPlayer();
        ItemStack[] items = (VersionUtils.isAtLeast(9) ? player.getInventory().getStorageContents() : player.getInventory().getContents());
        int space = 0;
        for (ItemStack iStack : items) {
            if (space >= item.getAmount()) break;
            if ((iStack == null) || (iStack.getType() == Material.AIR)) {
                space += item.getMaxStackSize();
                continue;
            }
            if (!item.isSimilar(iStack)) continue;
            space += (iStack.getMaxStackSize() - iStack.getAmount());
        }
        if (space > 0) return Math.min(space, item.getAmount()); // Vẫn còn khoảng trống.
        return -1; // Không còn khoảng trống.
    }

    default String getName() {
        return getOffPlayer().getName();
    }

    default Player getPlayer() {
        return getOffPlayer().getPlayer();
    }

    default boolean hasPermission(String perm) {
        final Player player = this.getPlayer();
        if (player.isOp() || player.hasPermission(Constants.ADMIN_PERMISSION)) return true;
        return player.hasPermission(perm);
    }

    boolean canClick();

    default void executeCommand(String cmd) {
        this.executeCommand(cmd, null);
    }

    default void executeCommand(String cmd, Map<String, String> args) {
        cmd = cmd.replaceAll(Utils.getRegex("player((\\-|\\_)?name)?"), this.getName());

        Matcher matcher;

        if ((args != null) && (!args.isEmpty())) {
            matcher = Constants.ARGS_PATTERN.matcher(cmd);
            while (matcher.find()) {
                String index = matcher.group("index");
                if (!args.containsKey(index)) continue;
                cmd = cmd.replaceFirst(Constants.ARGS_PATTERN.pattern(), args.get(index));
            }
        }

        Pattern pattern = Pattern.compile("(<(?ium)(chance):(\\s)?(?<chance>\\d+(\\.\\d+)?)(%)?>)$");
        matcher = pattern.matcher(cmd);
        if (matcher.find()) {
            try {
                double chance = Double.parseDouble(matcher.group("chance")), rnd = ThreadLocalRandom.current().nextDouble() * 100;
                if (chance < rnd) return;
            } catch (NumberFormatException ignored) {
                return;
            }
            cmd = cmd.replaceAll(pattern.pattern(), "").trim();
        }

        final Player player = this.getPlayer();

        pattern = Pattern.compile("(?ium)(op:)(\\s)(?<value>.*)");
        matcher = pattern.matcher(cmd);
        if (matcher.find()) {
            cmd = matcher.group("value").trim();
            if (!player.isOp()) {
                // Phần này sẽ bị delay (tương tự như thằng SkullMeta#setOwner)
                try {
                    player.setOp(true);
                    Bukkit.getServer().dispatchCommand(player, cmd);
                } finally {
                    player.setOp(false);
                }
            } else Bukkit.getServer().dispatchCommand(player, cmd);
            return;
        }

        pattern = Pattern.compile("(?ium)(console:)(\\s)(?<value>.*)");
        matcher = pattern.matcher(cmd);
        if (matcher.find()) {
            cmd = matcher.group("value").trim();
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
            return;
        }

        pattern = Pattern.compile("(?ium)(sound:)(\\s)(?<name>[a-zA-Z0-9_]+)(:(?<distance>\\d+(\\.\\d+)?):(?<high>\\d+(\\.\\d+)?))?");
        matcher = pattern.matcher(cmd);
        if (matcher.find()) {
            String soundName = matcher.group("name").toUpperCase();
            try {
                Sound sound = Sound.valueOf(soundName);
                float distance = 4.0f, high = 1.0f;
                String next = matcher.group("distance");
                if (next != null) {
                    distance = Float.parseFloat(next);
                    high = Float.parseFloat(matcher.group("high"));
                }
                player.playSound(player.getLocation(), sound, distance, high);
            } catch (Exception ignored) {
            }
            return;
        }

        pattern = Pattern.compile("(?ium)(message:)(\\s)(?<value>.*)");
        matcher = pattern.matcher(cmd);
        if (matcher.find()) {
            player.sendMessage(matcher.group("value").trim());
            return;
        }

        pattern = Pattern.compile("(?ium)(broadcast:)(\\s)(?<value>.*)");
        matcher = pattern.matcher(cmd);
        if (matcher.find()) {
            Bukkit.broadcastMessage(matcher.group("value").trim());
            return;
        }

        Bukkit.getServer().dispatchCommand(player, cmd);
    }

    String getBase64();

    boolean isUsedItem(Item item);

    void setUsedItem(Item item);

    default void useItem(Item item, Map<String, String> args, boolean leftClick) {
        final Commands cmds = item.getCommands();
        if (!cmds.getRandomCmds().isEmpty()) {
            int rnd = ThreadLocalRandom.current().nextInt(cmds.getRandomCmds().size());
            this.executeCommand(cmds.getRandomCmds().get(rnd), args);
        }

        cmds.getClickCmds().forEach(cmd -> this.executeCommand(cmd, args));
        if (leftClick) cmds.getLeftClickCmds().forEach(cmd -> this.executeCommand(cmd, args));
        else cmds.getRightClickCmds().forEach(cmd -> this.executeCommand(cmd, args));

        setUsedItem(item);
    }

    boolean isRedeemedGiftcode(Giftcode giftcode);

    void setRedeemedGiftcode(Giftcode giftcode);

    default void redeemGiftcode(Giftcode giftcode) {
        final Player player = this.getPlayer();

        boolean noticed = false;
        List<ItemStack> items = giftcode.getItems();
        for (ItemStack item : items) {
            item = item.clone();
            int space = this.getSpaceLeft(item);
            if (space >= item.getAmount()) player.getInventory().addItem(item);
            else {
                if (!noticed) {
                    player.sendMessage(Message.getMessage("FAIL.item-dropped"));
                    noticed = true;
                }
                if (space == -1) player.getWorld().dropItem(player.getLocation(), item);
                else {
                    item.setAmount(item.getAmount() - space);
                    player.getWorld().dropItem(player.getLocation(), item);
                    item.setAmount(space);
                    player.getInventory().addItem(item);
                }
            }
        }

        List<String> cmds = giftcode.getCommands();
        if (!cmds.isEmpty()) cmds.forEach(this::executeCommand);

        setRedeemedGiftcode(giftcode);
    }

    OfflinePlayer getOffPlayer();
}
