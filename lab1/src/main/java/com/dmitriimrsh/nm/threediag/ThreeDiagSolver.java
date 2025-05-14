package com.dmitriimrsh.nm.threediag;

public class ThreeDiagSolver {

    private final int size;
    private final double[] a;
    private final double[] b;
    private final double[] c;
    private final double[] P;
    private final double[] Q;

    public ThreeDiagSolver(final double[][] matrix,
                           final double[] d) {
        size = matrix.length;

        a = new double[size];
        b = new double[size];
        c = new double[size];
        P = new double[size];
        Q = new double[size];

        initializeRatioArrays(matrix);
        initializeSpecialRatioArrays(d);
    }

    public double[] solve() {
        double[] x = new double[size];

        x[size - 1] = Q[size - 1];
        for (int i = size - 2; i >= 0; --i) {
            x[i] = P[i] * x[i + 1] + Q[i];
        }

        return x;
    }

    private void initializeRatioArrays(final double[][] matrix) {
        double[] currentRow;
        for (int i = 0; i < matrix.length; ++i) {
            currentRow = matrix[i];

            if (i == 0) {
                a[i] = 0;
                initializeDoubleArrayElementFromSource(b, currentRow, i, 0);
                initializeDoubleArrayElementFromSource(c, currentRow, i, 1);
                continue;
            }

            if (i == matrix.length - 1) {
                initializeDoubleArrayElementFromSource(a, currentRow, i, 0);
                initializeDoubleArrayElementFromSource(b, currentRow, i, 1);
                c[i] = 0d;
                continue;
            }

            initializeDoubleArrayElementFromSource(a, currentRow, i, 0);
            initializeDoubleArrayElementFromSource(b, currentRow, i, 1);
            initializeDoubleArrayElementFromSource(c, currentRow, i, 2);
        }
    }

    private void initializeSpecialRatioArrays(final double[] d) {
        for (int i = 0; i < size; ++i) {
            if (i == 0) {
                P[i] = -c[i] / b[i];
                Q[i] = d[i] / b[i];
                continue;
            }

            if (i == size - 1) {
                P[i] = 0;
                Q[i] = (d[i] - a[i] * Q[i - 1]) / (b[i] + a[i] * P[i - 1]);
                continue;
            }

            P[i] = -c[i] / (b[i] + a[i] * P[i - 1]);
            Q[i] = (d[i] - a[i] * Q[i - 1]) / (b[i] + a[i] * P[i - 1]);
        }
    }

    private static void initializeDoubleArrayElementFromSource(final double[] target,
                                                               final double[] source,
                                                               final int targetIndex,
                                                               final int sourceIndex) {
        if (source.length > sourceIndex) {
            target[targetIndex] = source[sourceIndex];
        } else {
            target[targetIndex] = 0d;
        }
    }

}
