package io.egg.common.test;

import com.google.common.io.Files;
import io.egg.common.config.Config;
import io.egg.common.fields.StringDelegate;

import java.io.File;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        readTest();
    }

    public static void readTest() throws Exception {
        byte[] file = Files.toByteArray(new File("config.bin"));
        ExampleConfig s = Config.load(file, ExampleConfig.class);
        System.out.println(s.cat);
        System.out.println(s.coolCat);
        System.out.println(s.catAge);
        byte[] newConfig = Config.toFile(s);
        Files.write(newConfig,new File("config.bin") );
    }

    public static void writeTest() throws Exception {
        ExampleConfig c;
        try {
            c = Config.defaults(ExampleConfig.class);
            System.out.println(c.cat);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        c.cat = "cool dog";
        byte[] data = Config.toFile(c);
        Files.write(data, Paths.get("config.bin").toFile());
    }
}
