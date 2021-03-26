package io.egg.common.fields;


import io.egg.common.configeditor.ConfigEditor;
import io.egg.common.configeditor.OptionInfo;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Consumer;

public class IntegerDelegate implements FieldDelegate<Integer> {
    @Override
    public byte[] serialize(Object o) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt((Integer) o);
        return bb.array();
    }

    @Override
    public Integer deserialize(byte[] data) {
        return ByteBuffer.wrap(data).getInt();
    }

    @Override
    public void setMeta(Object value, ItemMeta meta) {
       Integer val = (Integer) value;
       ArrayList<String> lore = new ArrayList<>();
       lore.add(ChatColor.translateAlternateColorCodes('&', "&rType: &a&lINTEGER"));
       lore.add(ChatColor.translateAlternateColorCodes('&', "&rValue: &a&l" + val));
       lore.add(ChatColor.translateAlternateColorCodes('&', "&2Left-Click to change"));
       meta.setLore(lore);
    }

    @Override
    public void click(InventoryClickEvent e, ConfigEditor parent, OptionInfo opt) {
        if (e.getClick() == ClickType.LEFT) {
            parent.requestText("Enter a number", response -> {
               try {
                   Integer a = Integer.valueOf(response);
                   opt.f.set(parent.config, a);
               } catch (Exception ex) {

                   e.getWhoClicked().sendMessage("That is not a number!");
               }
            });
        }
    }
}
