package com.dmitriimrsh.nm.util;

public class VectorUtil {

    public static double[] calculateDiffForEqualSizedVectors(final double[] left,
                                                             final double[] right) {
        if (left.length != right.length)
            throw new RuntimeException("Sizes of vectors are not equal");

        final int size = left.length;
        double[] res = new double[size];

        for (int i = 0; i < size; ++i) {
            res[i] = left[i] - right[i];
        }

        return res;
    }

    public static double[] calculateSumForEqualSizedVectors(final double[] left,
                                                            final double[] right) {
        if (left.length != right.length)
            throw new RuntimeException("Sizes of vectors are not equal");

        final int size = left.length;
        double[] res = new double[size];

        for (int i = 0; i < size; ++i) {
            res[i] = left[i] + right[i];
        }

        return res;
    }

    public static double[] multiplyVectorOnNumber(final double[] vector,
                                                  final double number) {
        double[] res = new double[vector.length];

        for (int i = 0; i < res.length; ++i) {
            res[i] = vector[i] * number;
        }

        return res;
    }

    public static boolean compare(final double[] left,
                                  final double[] right,
                                  final double eps) {
        if (left.length != right.length)
            throw new RuntimeException("Vectors lengths are not equal");

        final int size = left.length;

        for (int i = 0; i < size; ++i) {
            if (Math.abs(left[i] - right[i]) >= eps) {
                return false;
            }
        }

        return true;
    }

    public static void printVector(final double[] vector) {
        for (var elem : vector) {
            System.out.printf("%.4f ", elem);
        }
        System.out.print("\n");
    }

}
