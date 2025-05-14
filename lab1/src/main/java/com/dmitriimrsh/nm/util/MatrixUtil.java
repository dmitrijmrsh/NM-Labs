package com.dmitriimrsh.nm.util;

public class MatrixUtil {

    public static void printMatrix(final double[][] matrix) {
        for (double[] row : matrix) {
            for (double number : row) {
                System.out.print(String.format("%.4f", number) + " ");
            }
            System.out.println();
        }
    }

    public static double[][] transposeSquareMatrix(final double[][] matrix) {
        double[][] transposed = new double[matrix.length][matrix.length];

        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                transposed[i][j] = matrix[j][i];
            }
        }

        return transposed;
    }

    public static double[] multiplySquareMatrixOnVector(final double[][] matrix,
                                                        final double[] vector) {
        final int size = matrix.length;

        if (vector.length != size)
            throw new RuntimeException("Invalid dimensions");

        double[] res = new double[size];

        for (int i = 0; i < size; ++i) {
            res[i] = 0d;
            for (int j = 0; j < size; j++) {
                res[i] += matrix[i][j] * vector[j];
            }
        }

        return res;
    }

    public static double[][] multiplyTwoSquaredMatrix(final double[][] left,
                                                      final double[][] right) {
        if (left.length != right.length)
            throw new RuntimeException("Invalid dimensions");

        final int size = left.length;

        double[][] res = new double[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                for (int k = 0; k < size; ++k) {
                    res[i][j] += left[i][k] * right[k][j];
                }
            }
        }

        return res;
    }

    public static void multiplyMatrixOnNumber(final double[][] matrix,
                                              final double number) {
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                matrix[i][j] *= number;
            }
        }
    }

    public static void divideMatrixOnNumber(final double[][] matrix,
                                            final double number) {
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                matrix[i][j] /= number;
            }
        }
    }

    public static double[][] calculateDiffForSquaredMatrix(final double[][] left,
                                                           final double[][] right) {
        if (left.length != right.length)
            throw new RuntimeException("Invalid dimensions");

        final int size = left.length;

        double[][] res = new double[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                res[i][j] = left[i][j] - right[i][j];
            }
        }

        return res;
    }

    public static double[][] calculateIdentityMatrix(final int size) {
        double[][] E = new double[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (i == j) {
                    E[i][j] = 1d;
                    continue;
                }

                E[i][j] = 0d;
            }
        }

        return E;
    }

    public static void validateSquareMatrixSymmetry(final double[][] matrix) {
        final double[][] transposed = MatrixUtil.transposeSquareMatrix(matrix);

        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                if (transposed[i][j] != matrix[i][j])
                    throw new RuntimeException("Squared matrix doesn't have symmetry");
            }
        }
    }

    public static void validateMatrixIsSquared(final double[][] matrix) {
        final int rows = matrix.length;

        for (double[] row : matrix) {
            if (row.length != rows) {
                throw new RuntimeException("Matrix is not squared");
            }
        }
    }

    public static double[][] copySquaredMatrix(final double[][] matrix) {
        double[][] copy = new double[matrix.length][matrix.length];

        for (int i = 0; i < matrix.length; ++i) {
            System.arraycopy(matrix[i], 0, copy[i], 0, matrix.length);
        }

        return copy;
    }
}
