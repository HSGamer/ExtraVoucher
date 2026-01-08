package me.hsgamer.exvoucher.data.id;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface ItemIDProvider {
    void apply(ItemStack item, ItemID id);

    Optional<ItemID> getID(ItemStack item);
}
