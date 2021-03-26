package io.egg.common.configeditor;

import io.egg.common.config.Config;
import io.egg.common.config.ConfigField;
import io.egg.common.fields.FieldDelegate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigObjectScanner {
    public static ArrayList<OptionInfo> scan(Object o) throws IllegalAccessException {
        Field[] fields = o.getClass().getFields();
        ArrayList<OptionInfo> options = new ArrayList<>();
        for (Field f :  fields) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            if (f.getAnnotation(ConfigField.class) == null) {
                continue;
            }
            if (f.get(o) == null) {
                Bukkit.getLogger().warning(f.getName() + " is null, not displaying");
                continue;
            }

            // we have a field that is annotated as a config field, lets get to work;
            ConfigField cfg = f.getAnnotation(ConfigField.class);
            FieldDelegate delegate = Config.delegates.get(f.getType());
            if (delegate == null) {
                Bukkit.getLogger().warning(f.getType().getName() + " has no delegate!");
                continue;
            }
            ItemStack h = new ItemStack(cfg.icon());
            ItemMeta b = h.getItemMeta();
            if (cfg.displayName().equals("")) {
                b.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + f.getName()));
            } else {
                b.setDisplayName(ChatColor.translateAlternateColorCodes('&', cfg.displayName()));
            }
            delegate.setMeta(f.get(o), b);
            h.setItemMeta(b);
            OptionInfo oi = new OptionInfo();
            oi.delegate = delegate;
            oi.f = f;
            oi.icon = h;
            oi.name = f.getName();
            oi.slot = cfg.slot();
            options.add(oi);

        }
        return options;
    }
}
