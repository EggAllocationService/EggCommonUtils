package io.egg.common.fields;


import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.ByteBuffer;

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
    public ItemStack setMeta(Object value, ItemMeta meta) {
        return null;
    }
}
