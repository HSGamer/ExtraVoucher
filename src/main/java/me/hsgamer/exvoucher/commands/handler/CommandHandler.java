package me.hsgamer.exvoucher.commands.handler;

import me.hsgamer.exvoucher.configs.Message;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class CommandHandler
        extends CommandBase
        implements CommandExecutor {

    private final Map<String, CommandListener> listeners;

    public CommandHandler() {
        this.listeners = new HashMap<>();
    }

    public void addPrimaryCommand(CommandListener listener) {
        if (!listener.getClass().isAnnotationPresent(Command.class)) return;
        Command cmd = listener.getClass().getAnnotation(Command.class);
        listeners.put(cmd.value()[0], listener);

        instance.getCommand(cmd.value()[0]).setExecutor(this);
        instance.getCommand(cmd.value()[0]).setTabCompleter(listener);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        CommandListener listener = listeners.get(command.getLabel());

        while (args.length > 0) {
            CommandListener subCmd = listener.getCommand(args[0]);
            if (subCmd == null) break;
            listener = subCmd;
            args = Arrays.copyOfRange(args, 1, args.length);
        }
        if (!this.check(sender, listener.getClass().getAnnotation(Command.class), label, args)) return true;

        listener.execute(new CommandContext(sender, label, args));
        return true;
    }

    private boolean check(CommandSender sender, Command cmd, String label, String[] args) {
        if ((!cmd.permission().isEmpty()) && (!hasPermission(sender, cmd.permission()))) {
            sender.sendMessage(Message.getMessage("FAIL.no-permission"));
            return false;
        }

        if (args.length < cmd.minArgs()) {
            if (!cmd.usage().isEmpty())
                sender.sendMessage(Message.getMessage("FAIL.missing-args").replaceAll(USAGE_REGEX, cmd.usage().replaceAll(LABEL_REGEX, label)));
            else sender.sendMessage(Message.getMessage("FAIL.missing-args"));
            return false;
        }

        switch (cmd.target()) {
            case ONLY_PLAYER:
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Message.getMessage("FAIL.only-players"));
                    return false;
                }
                break;
            case ONLY_CONSOLE:
                if (!(sender instanceof ConsoleCommandSender)) {
                    sender.sendMessage(Message.getMessage("FAIL.only-console"));
                    return false;
                }
        }

        return true;
    }

}
