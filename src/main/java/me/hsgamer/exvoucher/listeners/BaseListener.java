package me.hsgamer.exvoucher.listeners;

import me.hsgamer.exvoucher.ExtraVoucher;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class BaseListener
        implements Listener {

    protected final ExtraVoucher instance;

    BaseListener(ExtraVoucher instance) {
        this.instance = instance;
        this.register();
    }

    private void register() {
        Bukkit.getServer().getPluginManager().registerEvents(this, instance);
    }

}
