package io.egg.common.config;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.common.fields.FieldDelegate;
import io.egg.common.fields.IntegerDelegate;
import io.egg.common.fields.StringDelegate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public static <T> void registerDelegate(Class<T> t, FieldDelegate<T> d) {
        delegates.put(t, d);
    }


    public static HashMap<Class<?>, FieldDelegate> delegates = new HashMap<>();
    static {
        delegates.put(String.class, new StringDelegate());
        delegates.put(Integer.class, new IntegerDelegate());
    }


    public static byte[] toFile(Object o) throws Exception {
        Class<?> clazz = o.getClass();
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        List<Field> fields = Arrays.asList(clazz.getFields());
        ConfigClass versionAnnotation = clazz.getAnnotation(ConfigClass.class);
        if (versionAnnotation == null) {
            throw new Exception("Config does not have a version annotation");
        }
        bb.writeInt(versionAnnotation.version());
        int fieldCount = 0;
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) {
                if (f.get(o) != null) {
                    fieldCount ++;
                }
            }
        }
        bb.writeInt(fieldCount);
        for (Field f : fields) {

            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            if (f.get(o) == null) {
                continue;
            }
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

        HashMap<String, Boolean> modified = new HashMap<>();

        for (Field fb : clazz.getFields()) {
            if (Modifier.isStatic(fb.getModifiers())) {
                continue;
            }

            modified.put(fb.getName(), false);
        }

        for (int i = 0; i < fields; i++) {
            String name = bb.readUTF();
            Field f;
            try {
                f = clazz.getField(name);
            } catch (NoSuchFieldException e) {
                continue;
            }
            Class valueKind;
            String valueKindName = bb.readUTF();
            try {
                valueKind = Class.forName(valueKindName);
            } catch (ClassNotFoundException e) {
                System.out.println("Could not find type " + valueKindName + "for property " + name);
                continue;
            }
            FieldDelegate decoder = delegates.get(valueKind);
            if (decoder == null) {
                System.out.println("Could not find decode delegate for type " + valueKindName + "for property " + name);
                continue;
            }
            byte[] valueData = new byte[bb.readInt()];
            bb.readFully(valueData);
            Object value = decoder.deserialize(valueData);
            f.set(o, value);
            modified.put(f.getName(), true);
        }

        // Make sure that we at least try to set default values


        for (Field f : clazz.getFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            if (!modified.get(f.getName())) {
                // this field was not present in the saved config
               // or was not loaded somehow, try to find a default class for it
                Field[] fs = clazz.getFields();
                Field target = null;
                for (Field fz : fs) {
                    ConfigDefault d;

                    d = fz.getAnnotation(ConfigDefault.class);
                    if (d == null) {
                        continue;
                    }
                    if (d.target().equals(f.getName())) {
                        target = fz;
                        break;
                    }
                }
                if (target == null) continue;
                System.out.println("Setting default value for " + f.getName() + " using " + target.getName());
                f.set(o, target.get(null));
            }
        }
        return o;
    }


    public static <T> T defaults(Class<T> clazz) throws Exception {
        HashMap<String, Field> configOptions = new HashMap<>();
        ArrayList<Field> defaults = new ArrayList<>();

        T o = clazz.getConstructor().newInstance();

        for (Field f : clazz.getFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                ConfigDefault def;
                try {
                    def = f.getAnnotation(ConfigDefault.class);
                    defaults.add(f);
                } catch(Exception e) {
                    continue;
                }

            } else {
                configOptions.put(f.getName(), f);
            }
        }

        for (Field f :defaults) {
            // we know that F is a static field with @ConfigDefault
            ConfigDefault def;
            def = f.getAnnotation(ConfigDefault.class);
            if (def == null) {
                continue;
            }
            Field target = configOptions.get(def.target());
            if (target == null) {
                continue;
            }
            try {
                target.set(o, f.get(null));

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not set default for " + def.target());
            }
        }
        return o;
    }


}
