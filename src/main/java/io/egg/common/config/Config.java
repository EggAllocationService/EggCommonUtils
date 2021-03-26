package io.egg.common.config;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.common.fields.FieldDelegate;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Config {
    /*
    Byte format:
    int version
    Field[]
        int namelength
        byte[] name - UTF-8 string
        int typeLength
        byte[] type - type of field
        int datalength
        byte[] data
     */


    public static HashMap<Class<?>, FieldDelegate> delegates = new HashMap<>();
    public static byte[] toFile(Object o, Class<?> clazz) throws Exception {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        java.lang.reflect.Field[] fields = clazz.getFields();
        ConfigClass versionAnnotation = clazz.getAnnotation(ConfigClass.class);
        if (versionAnnotation == null) {
            throw new Exception("Config does not have a version annotation");
        }
        bb.writeInt(versionAnnotation.version());
        for (Field f : fields) {
            FieldDelegate s = delegates.get(f.getType());
            if (s == null) {
                System.out.println("Could not find delegate for " + f.getType());
                continue;
            }
            byte[] data = s.serialize(f.get(o));
            bb.write(stringToBytes(f.getName()));
            bb.write(stringToBytes(f.getType().getName()));
            bb.writeInt(data.length);
            bb.write(data);
        }
        return bb.toByteArray();

    }



    private static byte[] stringToBytes(String s) {
        ByteBuffer bb = ByteBuffer.allocate(s.getBytes(StandardCharsets.UTF_8).length + 4);
        bb.putInt(s.getBytes(StandardCharsets.UTF_8).length);
        bb.put(s.getBytes(StandardCharsets.UTF_8));
        return bb.array();
    }
}
