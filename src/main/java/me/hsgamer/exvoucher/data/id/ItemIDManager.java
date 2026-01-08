package me.hsgamer.exvoucher.data.id;

import me.hsgamer.exvoucher.ExtraVoucher;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ItemIDManager {
    private final List<ItemIDProvider> providers = new ArrayList<>();

    public ItemIDManager(ExtraVoucher instance) {
        if (instance.getServer().getPluginManager().getPlugin("NBTAPI") != null) {
            providers.add(new NBTItemIDProvider());
            instance.getLogger().info("Added NBTAPI to ID Providers");
        }
        try {
            Class.forName("org.bukkit.persistence.PersistentDataContainer");
            providers.add(new PDCItemIDProvider(instance));
            instance.getLogger().info("Added PDC to ID Providers");
        } catch (Exception ignored) {
            // IGNORED
        }

        if (providers.isEmpty()) {
            Logger logger = instance.getLogger();
            logger.severe("No ID providers found! You must install one for the plugin to properly work");
            logger.warning("Supported Providers:");
            logger.warning("- NBT API: https://www.spigotmc.org/resources/7939/");
            logger.warning("- PDC: MC 1.16.3 and above");
        }
    }

    public void applyID(ItemStack itemStack, ItemID id) {
        for (ItemIDProvider provider : providers) {
            provider.apply(itemStack, id);
        }
    }

    public Optional<ItemID> getItemID(ItemStack itemStack) {
        for (ItemIDProvider provider : providers) {
            Optional<ItemID> optionalItemID = provider.getID(itemStack);
            if (optionalItemID.isPresent()) {
                return optionalItemID;
            }
        }
        return Optional.empty();
    }
}
