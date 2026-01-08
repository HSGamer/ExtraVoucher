package me.hsgamer.exvoucher.data.item;

import java.util.List;

public final class Commands {

    private final List<String> randomCmds, leftClickCmds, rightClickCmds, clickCmds;

    Commands(Item item) {
        this.randomCmds = item.getConfig().getStringList("Commands.Random");
        this.leftClickCmds = item.getConfig().getStringList("Commands.LeftClick");
        this.rightClickCmds = item.getConfig().getStringList("Commands.RightClick");
        this.clickCmds = item.getConfig().getStringList("Commands.Click");
    }

    public List<String> getRandomCmds() {
        return this.randomCmds;
    }

    public List<String> getLeftClickCmds() {
        return this.leftClickCmds;
    }

    public List<String> getRightClickCmds() {
        return this.rightClickCmds;
    }

    public List<String> getClickCmds() {
        return this.clickCmds;
    }
}
