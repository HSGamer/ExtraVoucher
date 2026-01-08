package me.hsgamer.exvoucher.data;

import java.util.regex.Pattern;

public final class Constants {

    public static final Pattern ARGS_PATTERN = Pattern.compile("([%|{](?ium)(arg(ument)?s_)(?<index>\\d+)[}|%])");
    private static final String PREFIX = "exvoucher.";
    public static final String ADMIN_PERMISSION = PREFIX + "admin";
    public static final String BYPASS_SLOWCLICK_PERMISSION = PREFIX + "bypass.slowclick";
    public static final String BYPASS_LOCKED_PERMISSION = PREFIX + "bypass.locked";
    public static final String BYPASS_CONFIRMUSE_PERMISSION = PREFIX + "bypass.confirmuse";
    public static final String BYPASS_REMOVEONUSE_PERMISSION = PREFIX + "bypass.removeonuse";
    public static final String BYPASS_ONETIMEUSE_PERMISSION = PREFIX + "bypass.onetimeuse";
    public static final String BYPASS_LIMITOFUSE_PERMISSION = PREFIX + "bypass.limitofuse";
    public static final String BYPASS_EXPIRYDATE_PERMISSION = PREFIX + "bypass.expirydate";
    public static final String BYPASS_CONDITIONS_PERMISSION = PREFIX + "bypass.conditions";
    public static final String BYPASS_PERMISSIONS_PERMISSION = PREFIX + "bypass.permissions";
    public static final String BYPASS_PLAYERS_PERMISSION = PREFIX + "bypass.players";
    public static final String BYPASS_WORLDS_PERMISSION = PREFIX + "bypass.worlds";
    public static final String HELP_PERMISSION = PREFIX + "command.help";
    public static final String GIVE_PERMISSION = PREFIX + "command.give";
    public static final String LIST_PERMISSION = PREFIX + "command.list";
    public static final String REDEEM_PERMISSION = PREFIX + "command.redeem";
    public static final String RELOAD_PERMISSION = PREFIX + "command.reload";

    private Constants() {
    }

}
