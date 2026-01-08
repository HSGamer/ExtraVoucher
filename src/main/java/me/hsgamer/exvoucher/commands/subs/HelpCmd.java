package me.hsgamer.exvoucher.commands.subs;

import me.hsgamer.exvoucher.commands.handler.Command;
import me.hsgamer.exvoucher.commands.handler.CommandContext;
import me.hsgamer.exvoucher.commands.handler.CommandListener;
import me.hsgamer.exvoucher.configs.Message;
import me.hsgamer.exvoucher.data.Constants;

@Command(value = {"help", "?"}, permission = Constants.HELP_PERMISSION)
public final class HelpCmd
        extends CommandListener {

    @Override
    public void execute(CommandContext context) {
        context.sendMessage(Message.getMessage("HELP.header").replaceAll(VERSION_REGEX, instance.getDescription().getVersion()));
        context.sendMessage(Message.getMessage("HELP.help").replaceAll(LABEL_REGEX, context.getLabel()));
        if (this.hasPermission(context.getSender(), Constants.GIVE_PERMISSION)) {
            context.sendMessage(Message.getMessage("HELP.give").replaceAll(LABEL_REGEX, context.getLabel()));
        }
        if (this.hasPermission(context.getSender(), Constants.LIST_PERMISSION)) {
            context.sendMessage(Message.getMessage("HELP.list").replaceAll(LABEL_REGEX, context.getLabel()));
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
