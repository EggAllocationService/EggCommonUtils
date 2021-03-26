package io.egg.common.fields;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.common.config.Config;

public class StringDelegate implements FieldDelegate<String>{
    static {
        Config.delegates.put(String.class, new StringDelegate());
    }
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
}
