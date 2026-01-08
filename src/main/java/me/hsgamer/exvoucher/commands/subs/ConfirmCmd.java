package me.hsgamer.exvoucher.commands.subs;

import me.hsgamer.exvoucher.commands.handler.Command;
import me.hsgamer.exvoucher.commands.handler.CommandContext;
import me.hsgamer.exvoucher.commands.handler.CommandListener;
import me.hsgamer.exvoucher.commands.handler.CommandTarget;
import me.hsgamer.exvoucher.data.Constants;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(value = "confirm", usage = "/{label} confirm [arguments]", permission = Constants.LIST_PERMISSION, target = CommandTarget.ONLY_PLAYER)
public class ConfirmCmd extends CommandListener {
    @Override
    public void execute(CommandContext context) {
        String[] args = context.getArguments();
        UUID uuid = ((Player) context.getSender()).getUniqueId();
        instance.getListGUI().confirm(uuid, args);
    }
}
