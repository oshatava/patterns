package com.osh.patterns.lib.list;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public final class Printer {
    public static <T> String print(Iterable<T> in) {
        StringBuilder ret = new StringBuilder();
        ret.append("[");
        boolean isFirst = true;
        for (T t : in) {
            if (!isFirst)
                ret.append(", ");
            ret.append(t.toString());
            isFirst = false;
        }
        ret.append("]");
        return ret.toString();
    }

    public static String print(int[] in) {
        StringBuilder ret = new StringBuilder();
        ret.append("[");
        boolean isFirst = true;
        for (int t : in) {
            if (!isFirst)
                ret.append(", ");
            ret.append(Integer.toString(t));
            isFirst = false;
        }
        ret.append("]");
        return ret.toString();
    }
}
