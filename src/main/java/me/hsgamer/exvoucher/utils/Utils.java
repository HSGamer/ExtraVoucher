package me.hsgamer.exvoucher.utils;

import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Utils {

    private static final Pattern HEX_PATTERN = Pattern.compile("(?<!\\\\)(#[a-fA-F0-9]{6})");

    /*
     * Định dạng các mã màu trong các file config:
     */

    private Utils() {
    }

    public static <T extends ConfigurationSection> T formatColor(T section) {
        if (section == null) return null;
        section.getKeys(true).stream().forEach(keys -> {
            if (section.isConfigurationSection(keys)) return;
            Object val = section.get(keys);
            if (val instanceof String) section.set(keys, colorize(String.valueOf(val)));
            else if (val instanceof List) section.set(keys, colorize((List<?>) val));
        });
        return section;
    }

    public static String getRegex(String... inputs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(?ium)(");
        Arrays.stream(inputs).forEach(input -> {
            builder.append("\\{").append(input).append("\\}")
                    .append('|')
                    .append("\\%").append(input).append("\\%")
                    .append('|');
        });
        builder.deleteCharAt(builder.length() - 1).append(")");
        return builder.toString();
    }

    public static String colorize(String input) {
        if (VersionUtils.isAtLeast(16)) {
            Matcher matcher = HEX_PATTERN.matcher(input);
            while (matcher.find()) {
                String color = matcher.group(0);
                input = input.replace(color, "" + ChatColor.of(color));
            }
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<?> colorize(List<?> input) {
        if (input == null) return null;
        if (input.isEmpty()) return input;
        return input.stream().map(key -> colorize(String.valueOf(key))).collect(Collectors.toList());
    }

}
