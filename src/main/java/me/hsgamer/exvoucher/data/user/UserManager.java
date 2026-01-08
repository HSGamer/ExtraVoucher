package me.hsgamer.exvoucher.data.user;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.exvoucher.ExtraVoucher;
import me.hsgamer.exvoucher.configs.Setting;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.agent.core.AgentHolder;
import me.hsgamer.topper.agent.core.DataEntryAgent;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.data.simple.SimpleDataHolder;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.sql.converter.UUIDSqlValueConverter;
import me.hsgamer.topper.storage.sql.core.SqlDataStorageSupplier;
import me.hsgamer.topper.storage.sql.mysql.MySqlDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.SqliteDataStorageSupplier;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager extends SimpleDataHolder<UUID, UserEntry> implements AgentHolder<UUID, UserEntry> {
    private final ExtraVoucher instance;
    private final List<Agent> agents;
    private final List<DataEntryAgent<UUID, UserEntry>> entryAgents;
    private final Map<UUID, Double> nextClickMap = new ConcurrentHashMap<>();

    public UserManager(ExtraVoucher instance) {
        this.instance = instance;
        boolean isMySql = Setting.getDBType().equalsIgnoreCase("mysql");
        SqlDataStorageSupplier supplier = isMySql
                ? new MySqlDataStorageSupplier(Setting.getSqlDatabaseSetting(), JavaSqlClient::new)
                : new SqliteDataStorageSupplier(instance.getDataFolder(), Setting.getSqlDatabaseSetting(), JavaSqlClient::new);
        DataStorage<UUID, UserEntry> storage = supplier.getStorage(
                "playerdata",
                new UUIDSqlValueConverter("playerUUID"),
                UserEntry.CONVERTER
        );
        StorageAgent<UUID, UserEntry> storageAgent = new StorageAgent<>(storage);

        this.agents = List.of(
                storageAgent,
                storageAgent.getLoadAgent(this),
                new SpigotRunnableAgent(storageAgent, AsyncScheduler.get(instance), 20L)
        );
        this.entryAgents = List.of(storageAgent);
    }

    @Override
    public UserEntry getDefaultValue() {
        return UserEntry.EMPTY;
    }

    @Override
    public List<Agent> getAgents() {
        return agents;
    }

    @Override
    public List<DataEntryAgent<UUID, UserEntry>> getEntryAgents() {
        return entryAgents;
    }

    public boolean canClick(UUID uuid) {
        double delay = Setting.getDelayTimeOnClick();
        if (delay == -1) return true;
        double current = (System.currentTimeMillis() / 1000.0);
        double nextClick = nextClickMap.getOrDefault(uuid, 0D);
        if (current < nextClick) return false;
        nextClickMap.put(uuid, current + delay);
        return true;
    }

    public User findByUUID(UUID uuid) {
        return new StubUser(getOrCreateEntry(uuid), this);
    }

    public User findByPlayer(Player player) {
        return findByUUID(player.getUniqueId());
    }

    @Deprecated
    public User findByName(String name) {
        return findByUUID(instance.getServer().getOfflinePlayer(name).getUniqueId());
    }
}
