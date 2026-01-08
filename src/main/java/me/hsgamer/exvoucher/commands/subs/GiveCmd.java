package me.hsgamer.exvoucher.commands.subs;

import me.hsgamer.exvoucher.builder.NBTBuilder;
import me.hsgamer.exvoucher.commands.handler.Command;
import me.hsgamer.exvoucher.commands.handler.CommandContext;
import me.hsgamer.exvoucher.commands.handler.CommandListener;
import me.hsgamer.exvoucher.configs.Message;
import me.hsgamer.exvoucher.data.Constants;
import me.hsgamer.exvoucher.data.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Command(value = "give", usage = "/{label} give <id> [player] [amount] [arguments]", permission = Constants.GIVE_PERMISSION, minArgs = 1)
public final class GiveCmd
        extends CommandListener {

    @Override
    public void execute(CommandContext context) {
        String id = context.getArguments()[0];

        Item item = instance.getItemManager().findByID(id);
        if (item == null) {
            context.sendMessage(Message.getMessage("FAIL.id-not-found").replaceAll(VALUE_REGEX, id));
            return;
        }
        NBTBuilder builder = item.getNBTBuilder();

        ItemStack iStack = builder.build();
        int cur = iStack.getAmount();

        if (context.getArguments().length == 1) {
            if (!context.isPlayer()) {
                context.sendMessage(Message.getMessage("FAIL.must-specify-player"));
                return;
            }
            final Player player = (Player) context.getSender();

            int free = this.getFreeSpace(player, iStack);
            if (free == -1) {
                context.sendMessage(Message.getMessage("FAIL.inventory-is-full"));
                return;
            }

            if (cur > free) {
                ItemStack toDrop = iStack.clone();
                toDrop.setAmount(cur - free);
                player.getWorld().dropItemNaturally(player.getLocation(), toDrop);
            }

            iStack.setAmount(free);
            player.getInventory().addItem(iStack);

            context.sendMessage(Message.getMessage("SUCCESS.Give.self")
                    .replaceAll(AMOUNT_REGEX, String.valueOf(iStack.getAmount()))
                    .replaceAll(ID_REGEX, id));
            return;
        }

        Player player = instance.getServer().getPlayer(context.getArguments()[1]);
        if ((player == null) || (!player.isOnline())) {
            context.sendMessage(Message.getMessage("FAIL.player-not-found").replaceAll(VALUE_REGEX, context.getArguments()[1]));
            return;
        }

        if (context.getArguments().length >= 3) {
            try {
                cur = Math.max(Math.min(Short.MAX_VALUE, Integer.parseInt(context.getArguments()[2])), 1);
            } catch (NumberFormatException ignored) {
                context.sendMessage(Message.getMessage("FAIL.not-number").replaceAll(VALUE_REGEX, context.getArguments()[2]));
                return;
            }
            if (context.getArguments().length > 3) {
                builder.addArgument(Arrays.copyOfRange(context.getArguments(), 3, context.getArguments().length));
                iStack = builder.build();
            }
            iStack.setAmount(cur);
        }

        int free = this.getFreeSpace(player, iStack);
        if (free == -1) {
            context.sendMessage(Message.getMessage("FAIL.inventory-is-full"));
            return;
        }

        if (cur > free) {
            ItemStack toDrop = iStack.clone();
            toDrop.setAmount(cur - free);
            player.getWorld().dropItemNaturally(player.getLocation(), toDrop);
        }

        iStack.setAmount(free);
        player.getInventory().addItem(iStack);

        context.sendMessage(Message.getMessage("SUCCESS.Give.sender")
                .replaceAll(AMOUNT_REGEX, String.valueOf(iStack.getAmount()))
                .replaceAll(ID_REGEX, id)
                .replaceAll(PLAYER_REGEX, player.getName()));
        player.sendMessage(Message.getMessage("SUCCESS.Give.target")
                .replaceAll(AMOUNT_REGEX, String.valueOf(iStack.getAmount()))
                .replaceAll(ID_REGEX, id)
                .replaceAll(PLAYER_REGEX, context.getSender().getName()));
    }

    /*
     * Trả về khoảng trống còn lại trong kho đồ của người chơi:
     * Sẽ là -1 nếu không còn khoảng trống nào.
     */
    private int getFreeSpace(Player player, ItemStack item) {
        ItemStack[] items = player.getInventory().getStorageContents();
        int empty = 0;
        for (ItemStack stack : items) {
            if ((stack == null) || (stack.getType() == Material.AIR)) {
                empty += item.getMaxStackSize();
                continue;
            }
            if (!item.isSimilar(stack)) continue;
            empty += (stack.getMaxStackSize() - stack.getAmount());
        }
        if (empty > 0) return Math.min(empty, item.getAmount());
        return -1;
    }

}
