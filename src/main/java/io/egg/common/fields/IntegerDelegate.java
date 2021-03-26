package io.egg.common.fields;

import io.egg.common.config.Config;

import java.nio.ByteBuffer;

public class IntegerDelegate implements FieldDelegate<Integer> {
    static {
        Config.delegates.put(Integer.class, new IntegerDelegate());
    }
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
}
