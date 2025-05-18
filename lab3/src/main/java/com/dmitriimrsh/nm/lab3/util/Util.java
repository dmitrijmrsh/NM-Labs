package com.dmitriimrsh.nm.lab3.util;

import java.util.List;
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

    public static class Anna {

        public static final List<Double> X = List.of(-1., 0., 3., 4.);

        public static final List<Double> Y = List.of(-2., 6., 0., 1.);

    }

    public static class Lagrange {

        public static final List<Double> X_test = List.of(0.1, 0.5, 0.9, 1.3);

        public static final Double X_star_test = 0.8;

        public static final Function<Double, Double> y_test = Math::log;

        public static final List<Double> X_a = List.of(-3., -1., 1., 3.);

        public static final List<Double> X_b = List.of(-3., 0., 1., 3.);

        public static final Double X_star = -0.5;

        public static final Function<Double, Double> y = Math::atan;

    }

    public static class Newton {

        public static final List<Double> X_test = List.of(0., 1.0, 2.0, 3.0);

        public static final Double X_star_test = 1.5;

        public static final Function<Double, Double> y_test = x -> Math.sin(Math.PI / 6.0 * x);

        public static final List<Double> X_a = List.of(-3., -1., 1., 3.);

        public static final List<Double> X_b = List.of(-3., 0., 1., 3.);

        public static final Double X_star = -0.5;

        public static final Function<Double, Double> y = Math::atan;

    }

    public static class Spline {

        public static final List<Double> X_test = List.of(0., 1., 2., 3., 4.);

        public static final List<Double> Y_test = List.of(0., 1.8415, 2.9093, 3.1411, 3.2432);

        public static final double X_star_test = 1.5;

        public static final List<Double> X = List.of(-3., -1., 1., 3., 5.);

        public static final List<Double> Y = List.of(-1.2490, -0.78540, 0.78540, 1.2490, 1.3734);

        public static final double X_star = -0.5;

    }


    public static class LSM {

        public static final List<Double> X_test = List.of(0., 1.7, 3.4, 5.1, 6.8, 8.5);

        public static final List<Double> Y_test = List.of(0., 1.3038, 1.8439, 2.2583, 2.6077, 2.9155);

        public static final List<Double> X = List.of(-5., -3., -1., 1., 3., 5.);

        public static final List<Double> Y = List.of(-1.3734, -1.249, -0.7854, 0.7854, 1.249, 1.3734);
    }

}
