package com.dmitriimrsh.nm.util;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

import java.util.function.Function;

public class Util {

    public static double[] linspace(final double start,
                                    final double end,
                                    final int num) {
        double[] arr = new double[num];
        double step = (end - start) / (num - 1);
        for(int i = 0; i < num; i++) {
            arr[i] = start + i * step;
        }
        return arr;
    }

    public static class System {

        public static DerivativeStructure func_1(final DerivativeStructure... args) {
            DerivativeStructure x1 = args[0];
            DerivativeStructure x2 = args[1];
            return x1.subtract(x2.cos()).subtract(1);
        }

        public static DerivativeStructure func_2(final DerivativeStructure... args) {
            DerivativeStructure x1 = args[0];
            DerivativeStructure x2 = args[1];
            return x2.subtract(x1.sin()).subtract(1);
        }

        public static DerivativeStructure phi_1(final DerivativeStructure... args) {
            DerivativeStructure x2 = args[0];
            return x2.cos().add(1);
        }

        public static DerivativeStructure phi_2(final DerivativeStructure... args) {
            DerivativeStructure x1 = args[0];
            return x1.sin().add(1);
        }

    }

    public static class Linear {

        public static int ARGS_COUNT = 1000;

        // Поскольку ищем только положительный x, то не рассматриваем случай, когда x = - sqrt(...)

        public static Function<Double, Double> phi = x ->
                Math.sqrt((Math.sin(x) + 0.5) / 2);

        public static Function<Double,Double> func = x ->
                Math.sin(x) - 2 * x * x + 0.5;

    }
}
