package me.hsgamer.exvoucher.commands.subs;

import io.github.projectunified.craftux.spigot.SpigotInventoryUI;
import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.entity.EntityScheduler;
import me.hsgamer.exvoucher.commands.handler.Command;
import me.hsgamer.exvoucher.commands.handler.CommandContext;
import me.hsgamer.exvoucher.commands.handler.CommandListener;
import me.hsgamer.exvoucher.commands.handler.CommandTarget;
import me.hsgamer.exvoucher.data.Constants;
import org.bukkit.entity.Player;

@Command(value = "list", permission = Constants.LIST_PERMISSION, target = CommandTarget.ONLY_PLAYER)
public final class ListCmd
        extends CommandListener {

    @Override
    public void execute(CommandContext context) {
        final Player player = (Player) context.getSender();
        AsyncScheduler.get(instance).run(() -> {
            SpigotInventoryUI inventoryUI = instance.getListGUI().getInventory(player.getUniqueId());
            EntityScheduler.get(instance, player).run(inventoryUI::open);
        });
    }

}
