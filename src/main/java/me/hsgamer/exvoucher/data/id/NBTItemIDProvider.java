package me.hsgamer.exvoucher.data.id;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NBTItemIDProvider implements ItemIDProvider {
    @Override
    public void apply(ItemStack item, ItemID id) {
        NBT.modify(item, nbt -> {
            ReadWriteNBT compound = nbt.getOrCreateCompound("ExVoucher");
            compound.setString("ItemID", id.getID());
            Map<String, String> arguments = id.getArguments();
            if (!arguments.isEmpty()) {
                ReadWriteNBT args = compound.getOrCreateCompound("Arguments");
                for (Map.Entry<String, String> entry : arguments.entrySet()) {
                    args.setString(entry.getKey(), entry.getValue());
                }
            }
        });
    }

    @Override
    public Optional<ItemID> getID(ItemStack item) {
        return NBT.get(item, nbt -> {
            ReadableNBT compound = nbt.getCompound("ExVoucher");
            if (compound == null) return Optional.empty();
            String id = compound.getString("ItemID");
            if (id == null) return Optional.empty();
            ReadableNBT args = compound.getCompound("Arguments");
            Map<String, String> arguments = new HashMap<>();
            if (args != null) {
                for (String key : args.getKeys()) {
                    String value = args.getString(key);
                    if (value != null) {
                        arguments.put(key, value);
                    }
                }
            }
            return Optional.of(ItemID.of(id, arguments));
        });
    }
}
