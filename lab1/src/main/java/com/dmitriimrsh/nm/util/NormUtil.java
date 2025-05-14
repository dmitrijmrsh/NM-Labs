package com.dmitriimrsh.nm.util;

public class NormUtil {

    public static double calcVectorNormBySum(final double[] vector) {
        double norm = 0d;
        for (double elem : vector) {
            norm += Math.abs(elem);
        }
        return norm;
    }

    public static double calcVectorNormBySquareRoot(final double[] vector) {
        double norm = 0d;
        for (double elem : vector) {
            norm += elem * elem;
        }
        return Math.sqrt(norm);
    }

    public static double calcVectorNormByMaxElem(final double[] vector) {
        double norm = 0d;
        for (double elem : vector) {
            norm = Math.max(norm, Math.abs(elem));
        }
        return norm;
    }

    public static double calcMatrixNormByColumns(final double[][] matrix) {
        final int size = matrix.length;
        double norm = 0d;
        for (int j = 0; j < size; ++j) {
            if (matrix[j].length != size)
                throw new RuntimeException("Matrix is not square");

            double sum = 0d;
            for (int i = 0; i < size; ++i) {
                sum += Math.abs(matrix[i][j]);
            }

            norm = Math.max(norm, sum);
        }
        return norm;
    }

    public static double calcMatrixNormBySquareRoot(final double[][] matrix) {
        final int size = matrix.length;
        double norm = 0d;
        for (double[] row : matrix) {
            if (row.length != size)
                throw new RuntimeException("Matrix is not square");

            for (double elem : row) {
                norm += elem * elem;
            }
        }
        return Math.sqrt(norm);
    }

    public static double calcMatrixNormByRows(final double[][] matrix) {
        final int size = matrix.length;
        double norm = 0d;
        for (int i = 0; i < size; ++i) {
            if (matrix[i].length != size)
                throw new RuntimeException("Matrix is not square");

            double sum = 0d;
            for (int j = 0; j < size; ++j) {
                sum += Math.abs(matrix[i][j]);
            }

            norm = Math.max(norm, sum);
        }
        return norm;
    }



}
