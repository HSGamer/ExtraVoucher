package me.hsgamer.exvoucher.data.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.hsgamer.topper.storage.sql.converter.ComplexSqlValueConverter;
import me.hsgamer.topper.storage.sql.converter.StringSqlValueConverter;
import me.hsgamer.topper.storage.sql.core.SqlValueConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class UserEntry {
    public static final UserEntry EMPTY = new UserEntry("", Collections.emptyMap(), Collections.emptyMap());

    public static final SqlValueConverter<UserEntry> CONVERTER = ComplexSqlValueConverter.<UserEntry>builder()
            .constructor(() -> EMPTY)
            .entry(
                    new StringSqlValueConverter("base64", "TINYTEXT"),
                    entry -> entry.base64,
                    (userEntry, base64) -> new UserEntry(base64, userEntry.usedItems, userEntry.usedGiftcodes)
            )
            .entry(
                    new StringSqlValueConverter("usedItems", "TEXT"),
                    userEntry -> {
                        JsonObject jsonObject = new JsonObject();
                        for (Map.Entry<String, Long> entry : userEntry.usedItems.entrySet()) {
                            jsonObject.addProperty(entry.getKey(), entry.getValue());
                        }
                        return jsonObject.toString();
                    },
                    (userEntry, string) -> {
                        JsonObject jsonObject = new JsonParser().parse(string).getAsJsonObject();
                        Map<String, Long> usedItems = new HashMap<>();
                        jsonObject.entrySet().forEach(entry -> {
                            String key = entry.getKey();
                            long timestamp = entry.getValue().getAsLong();
                            usedItems.put(key, timestamp);
                        });
                        return new UserEntry(userEntry.base64, usedItems, userEntry.usedGiftcodes);
                    }
            )
            .entry(
                    new StringSqlValueConverter("usedGiftcodes", "TEXT"),
                    userEntry -> {
                        JsonObject jsonObject = new JsonObject();
                        for (Map.Entry<String, Long> entry : userEntry.usedGiftcodes.entrySet()) {
                            jsonObject.addProperty(entry.getKey(), entry.getValue());
                        }
                        return jsonObject.toString();
                    },
                    (userEntry, string) -> {
                        JsonObject jsonObject = new JsonParser().parse(string).getAsJsonObject();
                        Map<String, Long> usedGiftcodes = new HashMap<>();
                        jsonObject.entrySet().forEach(entry -> {
                            String key = entry.getKey();
                            long timestamp = entry.getValue().getAsLong();
                            usedGiftcodes.put(key, timestamp);
                        });
                        return new UserEntry(userEntry.base64, userEntry.usedItems, usedGiftcodes);
                    }
            )
            .build();
    private final String base64;
    private final Map<String, Long> usedItems;
    private final Map<String, Long> usedGiftcodes;

    public UserEntry(String base64, Map<String, Long> usedItems, Map<String, Long> usedGiftcodes) {
        this.base64 = base64;
        this.usedItems = usedItems;
        this.usedGiftcodes = usedGiftcodes;
    }

    public UserEntry withBase64(String base64) {
        return new UserEntry(base64, usedItems, usedGiftcodes);
    }

    public UserEntry withUsedItem(String id, long timestamp) {
        Map<String, Long> usedItems = new HashMap<>(this.usedItems);
        usedItems.put(id, timestamp);
        return new UserEntry(base64, usedItems, usedGiftcodes);
    }

    public UserEntry withUsedGiftcode(String id, long timestamp) {
        Map<String, Long> usedGiftcodes = new HashMap<>();
        usedGiftcodes.put(id, timestamp);
        return new UserEntry(base64, usedItems, usedGiftcodes);
    }

    public String base64() {
        return base64;
    }

    public Map<String, Long> usedItems() {
        return usedItems;
    }

    public Map<String, Long> usedGiftcodes() {
        return usedGiftcodes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        UserEntry that = (UserEntry) obj;
        return Objects.equals(this.base64, that.base64) &&
                Objects.equals(this.usedItems, that.usedItems) &&
                Objects.equals(this.usedGiftcodes, that.usedGiftcodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base64, usedItems, usedGiftcodes);
    }

    @Override
    public String toString() {
        return "UserEntry[" +
                "base64=" + base64 + ", " +
                "usedItems=" + usedItems + ", " +
                "usedGiftcodes=" + usedGiftcodes + ']';
    }
}
