package me.hsgamer.exvoucher.configs;

import me.hsgamer.topper.storage.sql.core.SqlDatabaseSetting;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.Map;

public final class Setting {

    private static FileConfiguration config;
    private static boolean checkForUpdates;
    private static String DBType, DBDatabase, DBHost, DBUsername, DBPassword;
    private static int DBPort;
    private static String dateFormat;
    private static double delayTimeOnClick;

    private Setting() {
    }

    public static void loadSetting() {
        config = Config.get("config");

        checkForUpdates = config.getBoolean("CheckForUpdates", true);

        DBType = config.getString("Database.Type", "SQLite").toUpperCase();
        DBDatabase = config.getString("Database.Database", "exstorage");
        DBHost = config.getString("Database.Host", "127.0.0.1");
        DBPort = config.getInt("Database.Port", 3306);
        DBUsername = config.getString("Database.Username", "root");
        DBPassword = config.getString("Database.Password", "");

        dateFormat = config.getString("DateFormat", "MM/dd/yyyy HH:mm:ss");

        delayTimeOnClick = config.getDouble("DelayTimeOnClick", -1.0);
    }

    public static SqlDatabaseSetting getSqlDatabaseSetting() {
        return new SqlDatabaseSetting() {
            @Override
            public String getHost() {
                return DBHost;
            }

            @Override
            public String getPort() {
                return String.valueOf(DBPort);
            }

            @Override
            public String getDatabase() {
                return DBDatabase;
            }

            @Override
            public String getUsername() {
                return DBUsername;
            }

            @Override
            public String getPassword() {
                return DBPassword;
            }

            @Override
            public boolean isUseSSL() {
                return false;
            }

            @Override
            public Map<String, Object> getDriverProperties() {
                return Collections.emptyMap();
            }

            @Override
            public Map<String, Object> getClientProperties() {
                return Collections.emptyMap();
            }
        };
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static boolean isCheckForUpdates() {
        return checkForUpdates;
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static double getDelayTimeOnClick() {
        return delayTimeOnClick;
    }

    public static String getDBType() {
        return DBType;
    }
}
