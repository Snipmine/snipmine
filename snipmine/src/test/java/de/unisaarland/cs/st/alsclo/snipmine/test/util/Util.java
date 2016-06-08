package de.unisaarland.cs.st.alsclo.snipmine.test.util;

/**
 * @author Alex Schlosser
 */
public class Util {

    public static String blur(String s) {
        return s.replaceAll("([a-zA-z_]+)\\d+", "$1").replaceAll("\\s|`", "");
    }
}
