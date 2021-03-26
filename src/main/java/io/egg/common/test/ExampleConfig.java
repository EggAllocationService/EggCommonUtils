package io.egg.common.test;


import io.egg.common.config.ConfigClass;
import io.egg.common.config.ConfigDefault;
import io.egg.common.config.ConfigField;

@ConfigClass(version = 3)
public class ExampleConfig {
    @ConfigField
    public String cat;
    @ConfigDefault(target = "cat")
    public static final String catDefault = "Nootka";
    @ConfigField
    public String coolCat;
    @ConfigDefault(target = "coolCat")
    public static final String coolestCat = "Smokey";

    @ConfigField
    public Integer catAge;

    @ConfigDefault(target = "catAge")
    public static final Integer ageDefault = 15;


}
