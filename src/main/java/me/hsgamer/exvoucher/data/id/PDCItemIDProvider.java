package me.hsgamer.exvoucher.data.id;

import me.hsgamer.exvoucher.ExtraVoucher;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PDCItemIDProvider implements ItemIDProvider {
    private final ExtraVoucher instance;
    private final NamespacedKey idKey;
    private final NamespacedKey argumentsKey;

    public PDCItemIDProvider(ExtraVoucher instance) {
        this.instance = instance;
        this.idKey = new NamespacedKey(instance, "id");
        this.argumentsKey = new NamespacedKey(instance, "arguments");
    }

    @Override
    public void apply(ItemStack item, ItemID id) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(idKey, PersistentDataType.STRING, id.getID());

        Map<String, String> arguments = id.getArguments();
        if (!arguments.isEmpty()) {
            PersistentDataContainer argumentsContainer = container.getAdapterContext().newPersistentDataContainer();
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                argumentsContainer.set(new NamespacedKey(instance, entry.getKey()), PersistentDataType.STRING, entry.getValue());
            }
            container.set(argumentsKey, PersistentDataType.TAG_CONTAINER, argumentsContainer);
        }

        item.setItemMeta(meta);
    }

    @Override
    public Optional<ItemID> getID(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Optional.empty();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        String id = container.get(idKey, PersistentDataType.STRING);
        if (id == null) return Optional.empty();

        Map<String, String> arguments = new HashMap<>();
        PersistentDataContainer argumentsContainer = container.get(argumentsKey, PersistentDataType.TAG_CONTAINER);
        if (argumentsContainer != null) {
            for (NamespacedKey argumentKey : argumentsContainer.getKeys()) {
                String value = argumentsContainer.get(argumentKey, PersistentDataType.STRING);
                if (value != null) {
                    arguments.put(argumentKey.getKey(), value);
                }
            }
        }

        return Optional.of(ItemID.of(id, arguments));
    }
}
