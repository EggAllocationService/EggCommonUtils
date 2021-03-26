package io.egg.common.configeditor;

import io.egg.common.fields.FieldDelegate;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class OptionInfo {
    public FieldDelegate delegate;
    public String name;
    public ItemStack icon;
    public Field f;
    public int slot;
}
