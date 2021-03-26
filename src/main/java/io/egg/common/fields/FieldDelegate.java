package io.egg.common.fields;

import io.egg.common.configeditor.ConfigEditor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface FieldDelegate<T> {
    public byte[] serialize(Object o);
    public T deserialize(byte[] data);
    public ItemStack setMeta(Object value, ItemMeta meta);
    public ItemStack click(InventoryClickEvent e, ConfigEditor parent);
}
