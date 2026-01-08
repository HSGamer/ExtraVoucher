package me.hsgamer.exvoucher.data.id;

import java.util.Map;

public interface ItemID {
    static ItemID of(String id, Map<String, String> arguments) {
        return new ItemID() {
            @Override
            public String getID() {
                return id;
            }

            @Override
            public Map<String, String> getArguments() {
                return arguments;
            }
        };
    }

    String getID();

    Map<String, String> getArguments();
}
