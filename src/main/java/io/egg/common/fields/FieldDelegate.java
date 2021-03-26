package io.egg.common.fields;

import io.egg.common.configeditor.ConfigEditor;
import io.egg.common.configeditor.OptionInfo;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface FieldDelegate<T> {
    public byte[] serialize(Object o);
    public T deserialize(byte[] data);
    public void setMeta(Object value, ItemMeta meta);
    public void click(InventoryClickEvent e, ConfigEditor parent, OptionInfo opt);
}
