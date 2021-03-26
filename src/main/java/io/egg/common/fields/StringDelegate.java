package io.egg.common.fields;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

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
}
