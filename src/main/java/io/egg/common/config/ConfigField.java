package io.egg.common.config;

//import org.bukkit.Material;

import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigField {
    Material icon() default Material.IRON_BARS;
    int slot() default -1;
    String displayName() default "";
}
