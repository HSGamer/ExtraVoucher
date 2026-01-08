package me.hsgamer.exvoucher.commands.subs;

import me.hsgamer.exvoucher.commands.handler.Command;
import me.hsgamer.exvoucher.commands.handler.CommandContext;
import me.hsgamer.exvoucher.commands.handler.CommandListener;
import me.hsgamer.exvoucher.configs.Config;
import me.hsgamer.exvoucher.configs.Message;
import me.hsgamer.exvoucher.configs.Setting;
import me.hsgamer.exvoucher.data.Constants;

@Command(value = {"reload", "rld", "rl"}, permission = Constants.RELOAD_PERMISSION)
public final class ReloadCmd
        extends CommandListener {

    @Override
    public void execute(CommandContext context) {
        Config.reloadAllConfigs();

        Setting.loadSetting();
        Message.loadMessages();

        instance.getItemManager().reloadItems();
        instance.getGiftcodeManager().reloadGiftcodes();

        context.sendMessage(Message.getMessage("SUCCESS.config-reload"));
    }

}
