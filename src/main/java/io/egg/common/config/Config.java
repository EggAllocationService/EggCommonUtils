package io.egg.common.config;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.common.fields.FieldDelegate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Config {
    /*
    Byte format:
    int version
    int fields;
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
        bb.writeInt(fields.length);
        for (Field f : fields) {
            FieldDelegate s = delegates.get(f.getType());
            if (s == null) {
                System.out.println("Could not find delegate for " + f.getType());
                continue;
            }
            byte[] data = s.serialize(f.get(o));
            bb.writeUTF(f.getName());
            bb.writeUTF(f.getType().getName());
            bb.writeInt(data.length);
            bb.write(data);
        }
        return bb.toByteArray();

    }

    public static <T> T load(byte[] data, Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, InvalidConfigException {
        T o = clazz.getConstructor().newInstance();
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        int version = bb.readInt();
        ConfigClass versionAnnotation = clazz.getAnnotation(ConfigClass.class);
        if (versionAnnotation == null) {
            throw new InvalidConfigException("Config does not have a version annotation");
        }
        if (version != versionAnnotation.version()) {
            System.out.println("Version mismatch, some settings may be reset to defaults.");
        }
        int fields = bb.readInt();
        for (int i = 0; i < fields; i++) {
            String name = bb.readUTF();
            try {

            }
        }
        return o;
    }



}
