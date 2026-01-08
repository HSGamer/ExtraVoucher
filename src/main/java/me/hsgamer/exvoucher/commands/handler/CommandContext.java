package me.hsgamer.exvoucher.commands.handler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandContext {

    private final CommandSender sender;
    private final String label;
    private final String[] arguments;

    CommandContext(CommandSender sender, String label, String[] arguments) {
        this.sender = sender;
        this.label = label;
        this.arguments = arguments;
    }

    public boolean isPlayer() {
        return (sender instanceof Player);
    }

    public void sendMessage(String msg) {
        sender.sendMessage(msg);
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public String getLabel() {
        return this.label;
    }

    public String[] getArguments() {
        return this.arguments;
    }
}
