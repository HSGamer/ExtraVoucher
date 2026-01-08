package me.hsgamer.exvoucher.builder;

import io.github.projectunified.craftitem.spigot.core.SpigotItem;
import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.data.id.ItemID;
import me.hsgamer.exvoucher.data.item.Item;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static me.hsgamer.exvoucher.data.Constants.ARGS_PATTERN;

public final class NBTBuilder {

    private final Item item;
    private final Map<String, String> arguments = new LinkedHashMap<>();
    private int count = 0;

    public NBTBuilder(Item item) {
        this.item = item;
    }

    public NBTBuilder addArgument(String... arguments) {
        boolean isQuote = false;
        StringBuilder builder = new StringBuilder();

        for (String string : arguments) {
            char[] chars = string.toCharArray();
            for (char c : chars) {
                if (c == '"') {
                    isQuote = !isQuote;
                    continue;
                }
                builder.append(c);
            }

            if (isQuote) {
                builder.append(' ');
                continue;
            }

            this.arguments.put(String.valueOf(++count), builder.toString());
            builder = new StringBuilder();
        }

        return this;
    }

    public ItemStack build() {
        SpigotItem spigotItem = new SpigotItem();
        item.getItem().accept(spigotItem);
        spigotItem.editMeta(meta -> {
            Matcher matcher;
            if (meta.hasDisplayName()) {
                String name = meta.getDisplayName();
                matcher = ARGS_PATTERN.matcher(name);
                while (matcher.find()) {
                    String index = matcher.group("index");
                    if (!arguments.containsKey(index)) continue;
                    name = name.replaceFirst(ARGS_PATTERN.pattern(), arguments.get(index));
                }
                meta.setDisplayName(name);
            }
            if (meta.hasLore()) {
                List<String> lores = meta.getLore();
                for (int i = 0; i < lores.size(); i++) {
                    String lore = lores.get(i);
                    matcher = ARGS_PATTERN.matcher(lore);
                    while (matcher.find()) {
                        String index = matcher.group("index");
                        if (!arguments.containsKey(index)) continue;
                        lore = lore.replaceFirst(ARGS_PATTERN.pattern(), arguments.get(index));
                    }
                    lores.set(i, lore);
                }
                meta.setLore(lores);
            }
        });
        spigotItem.edit(itemStack -> ExtraVoucher.getInstance().getItemIDManager().applyID(itemStack, ItemID.of(item.getId(), arguments)));
        return spigotItem.getItemStack();
    }

}
