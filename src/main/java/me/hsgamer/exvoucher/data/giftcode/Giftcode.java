package me.hsgamer.exvoucher.data.giftcode;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.configs.Config;
import me.hsgamer.exvoucher.data.user.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Giftcode {

    private final ExtraVoucher instance;
    private final FileConfiguration config;

    private final String code;
    private final boolean locked, oneTimeUse;
    private final List<ItemStack> items;
    private final String expiryDate;
    private final List<String> conditions, permissions, commands;
    private final boolean matchAll;
    private int limitOfUse;

    Giftcode(String code) {
        this.instance = ExtraVoucher.getInstance();
        this.config = Config.get("giftcode");

        this.code = code;
        this.locked = config.getBoolean(code + ".Locked");
        this.oneTimeUse = config.getBoolean(code + ".OneTimeUse");
        this.limitOfUse = config.getInt(code + ".LimitOfUse", -1);
        this.expiryDate = config.getString(code + ".ExpiryDate", "");
        this.conditions = config.getStringList(code + ".Conditions");
        this.permissions = config.getStringList(code + ".Permissions");
        this.commands = config.getStringList(code + ".Commands");
        this.matchAll = config.getBoolean(code + ".MatchAll");

        this.items = config.getStringList(code + ".ItemIDs").stream()
                .filter(id -> {
                    if (!id.contains(":")) return (instance.getItemManager().findByID(id) != null);
                    return (instance.getItemManager().findByID(id.split(":")[0]) != null);
                })
                .map(id -> {
                    ItemStack item;
                    if (!id.contains(":"))
                        item = instance.getItemManager().findByID(id).getNBTBuilder().build();
                    else {
                        String[] split = id.split(":");
                        item = instance.getItemManager().findByID(split[0]).getNBTBuilder().addArgument(Arrays.copyOfRange(split, 1, split.length)).build();
                    }
                    return item;
                })
                .collect(Collectors.toList());
    }

    /*
     * Checkers:
     */

    public boolean checkConditions(User user) {
        if (conditions.isEmpty() || (!instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")))
            return true;

        final Player player = user.getPlayer();
        boolean result = false;

        for (String condition : conditions) {
            String[] split = condition.split(";");
            if (split.length != 3) continue;

            String input = PlaceholderAPI.setPlaceholders(player, split[0]), output = split[2];
            try {
                double inNum, outNum;
                switch (split[1]) {
                    case "==":
                        inNum = Double.parseDouble(input);
                        outNum = Double.parseDouble(output);
                        if (matchAll) {
                            if (inNum != outNum) return false;
                            result = true;
                        } else if (inNum == outNum) return true;
                        break;
                    case "!=":
                        inNum = Double.parseDouble(input);
                        outNum = Double.parseDouble(output);
                        if (matchAll) {
                            if (inNum == outNum) return false;
                            result = true;
                        } else if (inNum != outNum) return true;
                        break;
                    case "<":
                        inNum = Double.parseDouble(input);
                        outNum = Double.parseDouble(output);
                        if (matchAll) {
                            if (inNum >= outNum) return false;
                            result = true;
                        } else if (inNum < outNum) return true;
                        break;
                    case ">":
                        inNum = Double.parseDouble(input);
                        outNum = Double.parseDouble(output);
                        if (matchAll) {
                            if (inNum <= outNum) return false;
                            result = true;
                        } else if (inNum > outNum) return true;
                        break;
                    case "<=":
                        inNum = Double.parseDouble(input);
                        outNum = Double.parseDouble(output);
                        if (matchAll) {
                            if (inNum > outNum) return false;
                            result = true;
                        } else if (inNum <= outNum) return true;
                        break;
                    case ">=":
                        inNum = Double.parseDouble(input);
                        outNum = Double.parseDouble(output);
                        if (matchAll) {
                            if (inNum < outNum) return false;
                            result = true;
                        } else if (inNum >= outNum) return true;
                        break;
                    case "equals":
                        if (matchAll) {
                            if (!input.equals(output)) return false;
                            result = true;
                        } else if (input.equals(output)) return true;
                        break;
                    case "!equals":
                        if (matchAll) {
                            if (input.equals(output)) return false;
                            result = true;
                        } else if (!input.equals(output)) return true;
                        break;
                    case "equalsign":
                        if (matchAll) {
                            if (!input.equalsIgnoreCase(output)) return false;
                            result = true;
                        } else if (input.equalsIgnoreCase(output)) return true;
                        break;
                    case "!equalsign":
                        if (matchAll) {
                            if (input.equalsIgnoreCase(output)) return false;
                            result = true;
                        } else if (!input.equalsIgnoreCase(output)) return true;
                        break;
                    case "contains":
                        if (matchAll) {
                            if (!input.contains(output)) return false;
                            result = true;
                        } else if (input.contains(output)) return true;
                        break;
                    case "!contains":
                        if (matchAll) {
                            if (input.contains(output)) return false;
                            result = true;
                        } else if (!input.contains(output)) return true;
                        break;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return result;
    }

    public boolean checkPermissions(User user) {
        if (permissions.isEmpty()) return true;
        return permissions.stream().anyMatch(user::hasPermission);
    }

    /*
     * Setters:
     */

    public String getCode() {
        return this.code;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isOneTimeUse() {
        return this.oneTimeUse;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public String getExpiryDate() {
        return this.expiryDate;
    }

    public List<String> getConditions() {
        return this.conditions;
    }

    public List<String> getPermissions() {
        return this.permissions;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public boolean isMatchAll() {
        return this.matchAll;
    }

    public int getLimitOfUse() {
        return this.limitOfUse;
    }

    public synchronized void setLimitOfUse(int num) {
        this.limitOfUse = num;
        config.set(code + ".LimitOfUse", limitOfUse);
        Config.save("giftcode");
    }
}
