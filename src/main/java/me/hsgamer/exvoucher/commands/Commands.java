package me.hsgamer.exvoucher.commands;

import me.hsgamer.exvoucher.commands.handler.Command;
import me.hsgamer.exvoucher.commands.handler.CommandContext;
import me.hsgamer.exvoucher.commands.handler.CommandListener;
import me.hsgamer.exvoucher.commands.subs.*;
import me.hsgamer.exvoucher.configs.Message;
import me.hsgamer.exvoucher.data.Constants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command(value = "extravoucher", permission = Constants.HELP_PERMISSION)
public final class Commands
        extends CommandListener {

    public Commands() {
        add(new HelpCmd());
        add(new GiveCmd());
        add(new ListCmd());
        add(new ConfirmCmd());
        add(new RedeemCmd());
        add(new ReloadCmd());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return null;
        Player player = (Player) sender;

        List<String> cmds = Arrays.asList(
                this.hasPermission(player, Constants.HELP_PERMISSION) ? "help" : "",
                this.hasPermission(player, Constants.GIVE_PERMISSION) ? "give" : "",
                this.hasPermission(player, Constants.LIST_PERMISSION) ? "list" : "",
                this.hasPermission(player, Constants.LIST_PERMISSION) ? "confirm" : "",
                this.hasPermission(player, Constants.REDEEM_PERMISSION) ? "redeem" : "",
                this.hasPermission(player, Constants.RELOAD_PERMISSION) ? "reload" : ""
        );

        String args0 = args[0].toLowerCase();
        if (args.length == 1) return cmds.stream().filter(cmd -> cmd.startsWith(args0)).collect(Collectors.toList());

        String args1 = args[1].toLowerCase();
        if ((args.length == 2) && args0.equals("give")) return instance.getItemManager()
                .getItems()
                .keySet()
                .stream()
                .filter(name -> name.toLowerCase().startsWith(args1))
                .collect(Collectors.toList());

        return null;
    }

    @Override
    public void execute(CommandContext context) {
        context.sendMessage(Message.getMessage("HELP.header").replaceAll(VERSION_REGEX, instance.getDescription().getVersion()));
        context.sendMessage(Message.getMessage("HELP.help").replaceAll(LABEL_REGEX, context.getLabel()));
        if (this.hasPermission(context.getSender(), Constants.GIVE_PERMISSION)) {
            context.sendMessage(Message.getMessage("HELP.give").replaceAll(LABEL_REGEX, context.getLabel()));
        }
        if (this.hasPermission(context.getSender(), Constants.LIST_PERMISSION)) {
            context.sendMessage(Message.getMessage("HELP.list").replaceAll(LABEL_REGEX, context.getLabel()));
            context.sendMessage(Message.getMessage("HELP.confirm").replaceAll(LABEL_REGEX, context.getLabel()));
        }
        if (this.hasPermission(context.getSender(), Constants.REDEEM_PERMISSION)) {
            context.sendMessage(Message.getMessage("HELP.redeem").replaceAll(LABEL_REGEX, context.getLabel()));
        }
        if (this.hasPermission(context.getSender(), Constants.RELOAD_PERMISSION)) {
            context.sendMessage(Message.getMessage("HELP.reload").replaceAll(LABEL_REGEX, context.getLabel()));
        }
        context.sendMessage(Message.getMessage("HELP.footer").replaceAll(VERSION_REGEX, instance.getDescription().getVersion()));
    }

}
