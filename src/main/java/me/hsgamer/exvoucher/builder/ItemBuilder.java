package me.hsgamer.exvoucher.builder;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import io.github.projectunified.craftitem.core.ItemModifier;
import io.github.projectunified.craftitem.modifier.NameModifier;
import io.github.projectunified.craftitem.spigot.core.SpigotItem;
import io.github.projectunified.craftitem.spigot.core.SpigotItemModifier;
import io.github.projectunified.craftitem.spigot.modifier.EnchantmentModifier;
import io.github.projectunified.craftitem.spigot.modifier.ItemFlagModifier;
import io.github.projectunified.craftitem.spigot.modifier.LoreModifier;
import io.github.projectunified.craftitem.spigot.skull.SkullModifier;
import io.github.projectunified.uniitem.all.AllItemProvider;
import io.github.projectunified.uniitem.api.Item;
import io.github.projectunified.uniitem.api.ItemKey;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ItemBuilder {
    private static final AllItemProvider provider = new AllItemProvider();

    private static final Pattern
            HDB_PATTERN = Pattern.compile("(?ium)(hdb)-(?<value>[a-zA-Z0-9]+)"),
            PLAYER_PATTERN = Pattern.compile("(?ium)(player)-(?<value>[a-zA-Z0-9]+)");

    private ItemBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static Consumer<SpigotItem> getItemStack(ConfigurationSection config, String path) {
        Supplier<ItemStack> itemSupplier;
        List<ItemModifier> itemModifiers = new ArrayList<>();

        String model = config.getString(path + ".Model");
        String texture = config.getString(path + ".Texture");
        if (!Strings.isNullOrEmpty(model)) {
            itemSupplier = () -> {
                try {
                    ItemKey itemKey = ItemKey.fromString(model);
                    Item item = provider.wrap(itemKey);
                    ItemStack itemStack = item.bukkitItem();
                    if (itemStack == null) {
                        itemStack = new ItemStack(Material.STONE);
                    }
                    return itemStack;
                } catch (Exception e) {
                    return new ItemStack(Material.STONE);
                }
            };
        } else if (!Strings.isNullOrEmpty(texture)) {
            Matcher matcher = HDB_PATTERN.matcher(texture);
            if (matcher.find()) {
                if (!Bukkit.getServer().getPluginManager().isPluginEnabled("HeadDatabase")) {
                    itemSupplier = () -> new ItemStack(Material.PLAYER_HEAD);
                } else {
                    String ID = matcher.group("value");
                    HeadDatabaseAPI api = new HeadDatabaseAPI();
                    itemSupplier = () -> {
                        ItemStack itemStack = api.getItemHead(ID);
                        if (itemStack != null) {
                            if (itemStack.hasItemMeta()) {
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                assert itemMeta != null;
                                itemMeta.setDisplayName(null);
                                itemMeta.setLore(null);
                                itemStack.setItemMeta(itemMeta);
                            }
                        } else {
                            itemStack = new ItemStack(Material.PLAYER_HEAD);
                        }
                        return itemStack;
                    };
                }
            } else {
                String textureValue;
                Matcher pMatcher = PLAYER_PATTERN.matcher(texture);
                if (pMatcher.matches()) {
                    textureValue = pMatcher.group("value");
                } else {
                    textureValue = texture;
                }
                itemSupplier = () -> new ItemStack(Material.PLAYER_HEAD);
                if (!textureValue.equalsIgnoreCase("unknown")) {
                    itemModifiers.add(new SkullModifier(textureValue));
                }
            }
        } else {
            String materialName = config.getString(path + ".Material", "");
            Material material = Material.matchMaterial(materialName);
            itemSupplier = () -> new ItemStack(Objects.requireNonNullElse(material, Material.STONE));
        }

        int amount = config.getInt(path + ".Amount");
        itemModifiers.add((item, translator) -> item.setAmount(Math.max(1, amount)));

        Integer customModelData = config.contains(path + ".CustomModelData") ? config.getInt(path + ".CustomModelData") : null;
        if (VersionUtils.isAtLeast(14) && customModelData != null) {
            itemModifiers.add((SpigotItemModifier) (item, translator) -> item.editMeta(meta1 -> meta1.setCustomModelData(customModelData)));
        }

        List<String> flags = config.getStringList(path + ".HideFlags");
        if (!flags.isEmpty()) {
            itemModifiers.add(new ItemFlagModifier(flags));
        }

        List<String> enchants = config.getStringList(path + ".Enchantments");
        if (!enchants.isEmpty()) {
            itemModifiers.add(new EnchantmentModifier(enchants, ','));
        }


        String name = config.getString(path + ".Name");
        if (name != null && !name.isEmpty()) {
            itemModifiers.add(new NameModifier(name));
        }

        List<String> lore = config.getStringList(path + ".Lore");
        if (!lore.isEmpty()) {
            itemModifiers.add(new LoreModifier(lore));
        }

        Supplier<ItemStack> finalSpigotItemSupplier = itemSupplier;
        return spigotItem -> {
            spigotItem.setItemStack(finalSpigotItemSupplier.get());
            for (ItemModifier itemModifier : itemModifiers) {
                itemModifier.modify(spigotItem);
            }
        };
    }
}
