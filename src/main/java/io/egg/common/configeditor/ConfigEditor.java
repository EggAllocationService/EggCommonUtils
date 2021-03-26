package io.egg.common.configeditor;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ConfigEditor implements Listener {
    public Object config;
    Inventory i;
    public HashMap<Integer, OptionInfo> icons;
    Player p;
    JavaPlugin plugin;
    boolean waitingForText = false;
    Consumer<String> textCallback = null;
    boolean mayClose = false;

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

    public void requestText(String prompt, Consumer<String> callback) {
        mayClose = true;
        textCallback = callback;
        p.closeInventory();
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        p.sendTitle(prompt,  "Type your response in chat (it will be hidden)", 300, 3000, 300);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Type 'cancel' to cancel"));

    }

    public void render() {
        i.clear();
        for (OptionInfo opt : icons.values()) {
            ItemMeta a = opt.icon.getItemMeta();
            try {
                ItemMeta b = opt.icon.getItemMeta();
                Object value = opt.f.get(config);
                opt.delegate.setMeta(value, b);
                opt.icon.setItemMeta(b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Integer di : icons.keySet()) {
            i.setItem(di, icons.get(di).icon);
        }
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getClickedInventory() != i) return;
        e.setCancelled(true);
        OptionInfo opt = icons.get(e.getSlot());
        if (opt != null) {
            opt.delegate.click(e,  this, opt);

        }

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
        e.setCancelled(true);
        textCallback.accept(msg);
        waitingForText = false;
        textCallback = null;
        p.openInventory(i);
        render();

    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getInventory() == i && !mayClose) {
            HandlerList.unregisterAll(this);
        }
    }

}
