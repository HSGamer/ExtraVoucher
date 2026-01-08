package me.hsgamer.exvoucher.commands.handler;

import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.data.Constants;
import me.hsgamer.exvoucher.utils.Utils;
import org.bukkit.command.CommandSender;

public abstract class CommandBase {

    protected final ExtraVoucher instance;
    protected final String VERSION_REGEX, LABEL_REGEX, VALUE_REGEX, USAGE_REGEX,
            AMOUNT_REGEX, ID_REGEX, PLAYER_REGEX;

    CommandBase() {
        this.instance = ExtraVoucher.getInstance();

        this.VERSION_REGEX = Utils.getRegex("ver(sion)?");
        this.LABEL_REGEX = Utils.getRegex("label");
        this.VALUE_REGEX = Utils.getRegex("value");
        this.USAGE_REGEX = Utils.getRegex("usage");

        this.AMOUNT_REGEX = Utils.getRegex("amount");
        this.ID_REGEX = Utils.getRegex("id");
        this.PLAYER_REGEX = Utils.getRegex("player");
    }

    protected boolean hasPermission(CommandSender sender, String perm) {
        if (sender.isOp() || sender.hasPermission(Constants.ADMIN_PERMISSION)) return true;
        return sender.hasPermission(perm);
    }

}
