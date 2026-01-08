package me.hsgamer.exvoucher.gui;

import io.github.projectunified.craftitem.spigot.core.SpigotItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Position;
import io.github.projectunified.craftux.mask.ButtonPaginatedMask;
import io.github.projectunified.craftux.mask.HybridMask;
import io.github.projectunified.craftux.mask.MaskUtils;
import io.github.projectunified.craftux.simple.SimpleButtonMask;
import io.github.projectunified.craftux.spigot.SpigotInventoryUI;
import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.builder.ItemBuilder;
import me.hsgamer.exvoucher.builder.NBTBuilder;
import me.hsgamer.exvoucher.configs.Config;
import me.hsgamer.exvoucher.configs.Message;
import me.hsgamer.exvoucher.data.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class ListGUI {
    private final Function<UUID, SpigotInventoryUI> inventoryFunction;
    private final Map<UUID, SpigotInventoryUI> inventoryMap = new ConcurrentHashMap<>();
    private final Map<UUID, Consumer<String[]>> confirmMap = new ConcurrentHashMap<>();

    public ListGUI(ExtraVoucher instance) {
        FileConfiguration config = Config.get("config");

        Sound sound;
        try {
            sound = Sound.valueOf(config.getString("ListGui.Sound", "").toUpperCase());
        } catch (Exception ignored) {
            sound = null;
        }
        Sound finalSound = sound;

        HybridMask mask = new HybridMask();

        List<Position> listPositions = MaskUtils.generateAreaPositions(Position.of(0, 0), Position.of(8, 4));
        ButtonPaginatedMask listMask = new ButtonPaginatedMask(uuid -> listPositions) {
            @Override
            public @NotNull List<Button> getButtons(@NotNull UUID uuid) {
                return instance.getItemManager().getItems()
                        .values()
                        .stream()
                        .map(item -> (Button) (uuid1, actionItem) -> {
                            SpigotItem spigotItem = new SpigotItem();
                            item.getItem().accept(spigotItem);
                            actionItem.setItem(spigotItem.getItemStack());
                            Player player = Bukkit.getPlayer(uuid1);
                            if (player != null && player.hasPermission(Constants.GIVE_PERMISSION)) {
                                actionItem.setAction(InventoryClickEvent.class, event -> {
                                    confirmMap.put(uuid1, args -> {
                                        NBTBuilder nbtBuilder = item.getNBTBuilder();
                                        nbtBuilder.addArgument(args);
                                        player.getInventory().addItem(nbtBuilder.build());
                                    });
                                    player.sendMessage(Message.getMessage("SUCCESS.confirm-item"));
                                    player.closeInventory();
                                });
                            }
                            return true;
                        })
                        .toList();
            }
        };
        mask.add(listMask);

        SimpleButtonMask decorativeMask = new SimpleButtonMask();
        mask.add(decorativeMask);

        Consumer<SpigotItem> decorativeItem = ItemBuilder.getItemStack(config, "ListGui.DecorativeItem");
        Button decorativeButton = (uuid, actionItem) -> {
            SpigotItem spigotItem = new SpigotItem();
            decorativeItem.accept(spigotItem);
            actionItem.setItem(spigotItem.getItemStack());
            return true;
        };
        for (int i = 0; i <= 8; i++) {
            decorativeMask.setButton(Position.of(i, 5), decorativeButton);
        }

        Consumer<SpigotItem> nextItem = ItemBuilder.getItemStack(config, "ListGui.NextButton");
        Button nextButton = (uuid, actionItem) -> {
            SpigotItem spigotItem = new SpigotItem();
            nextItem.accept(spigotItem);
            actionItem.setItem(spigotItem.getItemStack());
            actionItem.setAction(InventoryClickEvent.class, event -> {
                UUID eventUUID = event.getWhoClicked().getUniqueId();
                listMask.nextPage(eventUUID);
                SpigotInventoryUI spigotInventoryUI = inventoryMap.get(eventUUID);
                if (spigotInventoryUI != null) {
                    spigotInventoryUI.update();
                }
            });
            return true;
        };
        decorativeMask.setButton(Position.of(5, 5), nextButton);

        Consumer<SpigotItem> previousItem = ItemBuilder.getItemStack(config, "ListGui.NextButton");
        Button previousButton = (uuid, actionItem) -> {
            SpigotItem spigotItem = new SpigotItem();
            previousItem.accept(spigotItem);
            actionItem.setItem(spigotItem.getItemStack());
            actionItem.setAction(InventoryClickEvent.class, event -> {
                UUID eventUUID = event.getWhoClicked().getUniqueId();
                listMask.previousPage(eventUUID);
                SpigotInventoryUI spigotInventoryUI = inventoryMap.get(eventUUID);
                if (spigotInventoryUI != null) {
                    spigotInventoryUI.update();
                }
            });
            return true;
        };
        decorativeMask.setButton(Position.of(3, 5), previousButton);

        String title = config.getString("ListGui.Title");
        this.inventoryFunction = uuid -> {
            SpigotInventoryUI inventoryUI = new SpigotInventoryUI(uuid, title, 9 * 6) {
                @Override
                protected boolean onClick(InventoryClickEvent event) {
                    if (finalSound != null) {
                        Player player = (Player) event.getWhoClicked();
                        player.playSound(player.getLocation(), finalSound, 4.0f, 1.0f);
                    }
                    return super.onClick(event);
                }
            };
            inventoryUI.setMask(mask);
            return inventoryUI;
        };
    }

    public SpigotInventoryUI getInventory(UUID uuid) {
        SpigotInventoryUI inventory = inventoryMap.computeIfAbsent(uuid, inventoryFunction);
        inventory.update();
        return inventory;
    }

    public void confirm(UUID uuid, String[] args) {
        Consumer<String[]> confirmConsumer = confirmMap.remove(uuid);
        if (confirmConsumer != null) {
            confirmConsumer.accept(args);
        }
    }
}
