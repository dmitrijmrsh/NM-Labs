package com.dmitriimrsh.nm.util;

public class FunctionUtil {

    public static double sign(final double value) {
        if (value > 0d) {
            return 1d;
        }

        if (value == 0d) {
            return 0d;
        }

        return -1d;
    }

}
