package me.hsgamer.exvoucher.data.item;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.data.user.User;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class Settings {

    private final ExtraVoucher instance;
    private final Item item;

    private final boolean locked, confirmUse, removeOnUse, oneTimeUse;
    private final String expiryDate;
    private final List<String> conditions;
    private final boolean matchAll;
    private final boolean permissionsAsWhitelist, playersAsWhitelist, worldsAsWhitelist;
    private final List<String> permissions, players, worlds;
    private int limitOfUse;

    Settings(Item item) {
        this.instance = ExtraVoucher.getInstance();
        this.item = item;

        this.locked = item.getConfig().getBoolean("Settings.Locked");
        this.confirmUse = item.getConfig().getBoolean("Settings.ConfirmUse");
        this.removeOnUse = item.getConfig().getBoolean("Settings.RemoveOnUse");
        this.oneTimeUse = item.getConfig().getBoolean("Settings.OneTimeUse");
        this.limitOfUse = item.getConfig().getInt("Settings.LimitOfUse", -1);
        this.expiryDate = item.getConfig().getString("Settings.ExpiryDate", "");

        this.conditions = item.getConfig().getStringList("Settings.Conditions")
                .stream()
                .filter(condition -> condition.split(";").length == 3)
                .collect(Collectors.toList());
        this.matchAll = item.getConfig().getBoolean("Settings.MatchAll");

        this.permissionsAsWhitelist = item.getConfig().getBoolean("Settings.Permissions.AsWhitelist");
        this.playersAsWhitelist = item.getConfig().getBoolean("Settings.Players.AsWhitelist");
        this.worldsAsWhitelist = item.getConfig().getBoolean("Settings.Worlds.AsWhitelist");

        this.permissions = item.getConfig().getStringList("Settings.Permissions.List");
        this.players = item.getConfig().getStringList("Settings.Players.List");
        this.worlds = item.getConfig().getStringList("Settings.Worlds.List");
    }

    /*
     * Checkers
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

    public boolean checkPermission(User user) {
        if (permissions.isEmpty()) return true;
        if (permissionsAsWhitelist) return permissions.stream().anyMatch(user::hasPermission);
        return permissions.stream().noneMatch(user::hasPermission);
    }

    public boolean checkPlayer(User user) {
        if (players.isEmpty()) return true;
        if (playersAsWhitelist) return players.stream().anyMatch(user.getName()::equalsIgnoreCase);
        return players.stream().noneMatch(user.getName()::equalsIgnoreCase);
    }

    public boolean checkWorld(User user) {
        if (worlds.isEmpty()) return true;
        if (worldsAsWhitelist) return worlds.stream().anyMatch(user.getPlayer().getWorld().getName()::equalsIgnoreCase);
        return worlds.stream().noneMatch(user.getPlayer().getWorld().getName()::equalsIgnoreCase);
    }

    /*
     * Setters
     */

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isConfirmUse() {
        return this.confirmUse;
    }

    public boolean isRemoveOnUse() {
        return this.removeOnUse;
    }

    public boolean isOneTimeUse() {
        return this.oneTimeUse;
    }

    public String getExpiryDate() {
        return this.expiryDate;
    }

    public List<String> getConditions() {
        return this.conditions;
    }

    public boolean isMatchAll() {
        return this.matchAll;
    }

    public boolean isPermissionsAsWhitelist() {
        return this.permissionsAsWhitelist;
    }

    public boolean isPlayersAsWhitelist() {
        return this.playersAsWhitelist;
    }

    public boolean isWorldsAsWhitelist() {
        return this.worldsAsWhitelist;
    }

    public List<String> getPermissions() {
        return this.permissions;
    }

    public List<String> getPlayers() {
        return this.players;
    }

    public List<String> getWorlds() {
        return this.worlds;
    }

    public int getLimitOfUse() {
        return this.limitOfUse;
    }

    public synchronized void setLimitOfUse(int num) {
        this.limitOfUse = num;
        item.getConfig().set("Settings.LimitOfUse", limitOfUse);
        item.saveConfig();
    }
}
