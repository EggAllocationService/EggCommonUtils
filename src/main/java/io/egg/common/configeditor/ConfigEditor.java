package io.egg.common.configeditor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ConfigEditor implements Listener {
    Object config;
    Inventory i;
    public HashMap<Integer, OptionInfo> icons;
    Player p;
    JavaPlugin plugin;
    boolean waitingForText = false;
    Consumer<String> textCallback = null;

    public ConfigEditor(Object o, Player target, JavaPlugin pln) throws IllegalAccessException {
        p = target;
        config = o;
        plugin = pln;
        i = Bukkit.createInventory(null, 54, "Config Editor");
        int index = 0;
        for (OptionInfo opt : ConfigObjectScanner.scan(o)) {
            if (opt.slot == -1) {
                i.setItem(index, opt.icon);
                icons.put(index, opt);
                index++;
            } else {
                i.setItem(opt.slot, opt.icon);
                icons.put(opt.slot, opt);
            }
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void chat(PlayerChatEvent e ) {
        if (e.getPlayer() != p) return;
        if (!waitingForText) return;
        String msg = e.getMessage();
        if (msg.equalsIgnoreCase("cancel")) {
            waitingForText = false;
            textCallback = null;
            return;
        }
        textCallback.accept(msg);
        waitingForText = false;
        textCallback = null;

    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getInventory() == i) {
            HandlerList.unregisterAll(this);
        }
    }

}
