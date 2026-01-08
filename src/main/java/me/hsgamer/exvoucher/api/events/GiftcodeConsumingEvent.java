package me.hsgamer.exvoucher.api.events;

import me.hsgamer.exvoucher.data.giftcode.Giftcode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class GiftcodeConsumingEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Giftcode giftcode;
    private boolean cancelled;

    public GiftcodeConsumingEvent(Player player, Giftcode gift) {
        super(player);
        this.giftcode = gift;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Giftcode getGiftcode() {
        return this.giftcode;
    }
}
