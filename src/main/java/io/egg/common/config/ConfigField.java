package io.egg.common.config;

import org.bukkit.Material;

public @interface ConfigField {
    Material icon() default Material.IRON_BARS;
}
