package io.egg.common.fields;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.common.configeditor.ConfigEditor;
import io.egg.common.configeditor.OptionInfo;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class StringDelegate implements FieldDelegate<String>{
    @Override
    public byte[] serialize(Object o) {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeUTF((String) o);
        return bb.toByteArray();
    }

    @Override
    public String deserialize(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        return bb.readUTF();
    }
    @Override
    public void setMeta(Object value, ItemMeta meta) {
        String val = (String) value;
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&rType: &a&lSTRING"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&rValue: &a&l" + val));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&2Left-Click to change"));
        meta.setLore(lore);
    }

    @Override
    public void click(InventoryClickEvent e, ConfigEditor parent, OptionInfo opt) {
        if (e.getClick() == ClickType.LEFT) {
            parent.requestText("Enter some text", response -> {
                try {

                    opt.f.set(parent.config, response);
                } catch (Exception ex) {

                    e.getWhoClicked().sendMessage("There was an error!");
                    ex.printStackTrace();
                }
            });
        }
    }
}
