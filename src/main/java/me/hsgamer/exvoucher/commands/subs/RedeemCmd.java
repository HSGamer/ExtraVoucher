package me.hsgamer.exvoucher.commands.subs;

import me.hsgamer.exvoucher.api.events.GiftcodeConsumingEvent;
import me.hsgamer.exvoucher.commands.handler.Command;
import me.hsgamer.exvoucher.commands.handler.CommandContext;
import me.hsgamer.exvoucher.commands.handler.CommandListener;
import me.hsgamer.exvoucher.commands.handler.CommandTarget;
import me.hsgamer.exvoucher.configs.Message;
import me.hsgamer.exvoucher.data.Constants;
import me.hsgamer.exvoucher.data.giftcode.Giftcode;
import me.hsgamer.exvoucher.data.user.User;
import me.hsgamer.exvoucher.utils.DateUtils;
import org.bukkit.entity.Player;

@Command(value = "redeem", usage = "/{label} redeem <code>", permission = Constants.REDEEM_PERMISSION, target = CommandTarget.ONLY_PLAYER, minArgs = 1)
public final class RedeemCmd
        extends CommandListener {

    @Override
    public void execute(CommandContext context) {
        String code = context.getArguments()[0];
        if (!code.matches("[a-zA-Z0-9_]+")) {
            context.sendMessage(Message.getMessage("FAIL.invalid-code").replaceAll(VALUE_REGEX, code));
            return;
        }
        final Player player = (Player) context.getSender();

        final Giftcode giftcode = instance.getGiftcodeManager().findByCode(code);
        if (giftcode == null) {
            context.sendMessage(Message.getMessage("FAIL.giftcode-not-found").replaceAll(VALUE_REGEX, code));
            return;
        }
        final User user = instance.getUserManager().findByPlayer(player);

        if ((!user.hasPermission(Constants.BYPASS_LOCKED_PERMISSION)) && giftcode.isLocked()) {
            context.sendMessage(Message.getMessage("FAIL.giftcode-locked"));
            return;
        }

        GiftcodeConsumingEvent consumingEvent = new GiftcodeConsumingEvent(player, giftcode);
        instance.callEvent(consumingEvent);
        if (consumingEvent.isCancelled()) return;

        String expiryDate = giftcode.getExpiryDate();
        if ((!user.hasPermission(Constants.BYPASS_EXPIRYDATE_PERMISSION)) && DateUtils.isExpired(expiryDate)) {
            context.sendMessage(Message.getMessage("FAIL.giftcode-expired"));
            return;
        }

        if (((!user.hasPermission(Constants.BYPASS_CONDITIONS_PERMISSION)) && (!giftcode.checkConditions(user))) ||
                ((!user.hasPermission(Constants.BYPASS_PERMISSIONS_PERMISSION)) && (!giftcode.checkPermissions(user)))) {
            context.sendMessage(Message.getMessage("FAIL.cannot-redeem-giftcode"));
            return;
        }

        if ((!user.hasPermission(Constants.BYPASS_ONETIMEUSE_PERMISSION)) && giftcode.isOneTimeUse() && user.isRedeemedGiftcode(giftcode)) {
            context.sendMessage(Message.getMessage("FAIL.giftcode-already-redeemed"));
            return;
        }

        if (!user.hasPermission(Constants.BYPASS_LIMITOFUSE_PERMISSION)) {
            int uses = giftcode.getLimitOfUse();
            if (uses != -1) {
                if (uses <= 0) {
                    context.sendMessage(Message.getMessage("FAIL.end-of-usable"));
                    return;
                }
                giftcode.setLimitOfUse(--uses);
            }
        }

        user.redeemGiftcode(giftcode);
    }

}
