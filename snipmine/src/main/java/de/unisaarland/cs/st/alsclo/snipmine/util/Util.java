package de.unisaarland.cs.st.alsclo.snipmine.util;

import java.util.Arrays;

/**
 * @author Alex Schlosser
 */
public class Util {

    public static <T> boolean allInstanceOf(T[] array, Class<?>... c){
        return Arrays.stream(array).allMatch(x -> instanceOfAny(x, c));
    }
    public static <T> boolean instanceOfAny(T t, Class<?>... c){
        return Arrays.stream(c).anyMatch(x -> x.isInstance(t));
    }
    public static void expect(boolean val) {
        if (!val) {
            throw new RuntimeException("Malformed AST");
        }
    }
}
