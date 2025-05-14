package com.dmitriimrsh.nm.lu;

import com.dmitriimrsh.nm.util.MatrixUtil;

public class LUSolver {

    private final int size;
    private int replacementCount;
    private final double[][] L;
    private final double[][] U;

    public LUSolver(final double[][] matrix) {
        replacementCount = 0;
        size = matrix.length;
        U = new double[size][size];
        L = new double[size][size];

        for (int i = 0; i < size; ++i) {
            if (matrix[i].length != size)
                throw new RuntimeException("Input matrix is not square");

            System.arraycopy(matrix[i], 0, U[i], 0, size);
        }

        decompose();
    }

    public double[] solveEquation(final double[] b) {
        if (b.length != size)
            throw new RuntimeException("Invalid size of b vector");

        if (b.length == 0) {
            return null;
        }

        double sum;
        double[] z = new double[size];
        z[0] = b[0];

        for (int i = 1; i < size; ++i) {
            sum = 0d;

            for (int j = 0; j <= i - 1; ++j) {
                sum += L[i][j] * z[j];
            }

            z[i] = b[i] - sum;
        }

        double[] x = new double[size];
        x[size - 1] = z[size - 1] / U[size - 1][size - 1];

        for (int i = size - 2; i >= 0; --i) {
            sum = 0d;

            for (int j = i + 1; j <= size - 1; ++j) {
                sum += U[i][j] * x[j];
            }

            x[i] =  1 / U[i][i] * (z[i] - sum);
        }

        return x;
    }

    public double getDeterminant() {
        double det = Math.pow(-1, replacementCount);

        for (int i = 0; i < size; ++i) {
            det *= U[i][i];
        }

        return det;
    }

    public double[][] getInvertedMatrix() {
        double[][] E = new double[size][size];
        double[][] results = new double[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (i == j) {
                    E[i][j] = 1d;
                } else {
                    E[i][j] = 0d;
                }
            }
        }

        for (int i = 0; i < size; ++i) {
            double[] res = solveEquation(E[i]);
            System.arraycopy(res, 0, results[i], 0, res.length);
        }

        return MatrixUtil.transposeSquareMatrix(results);
    }

    private void decompose() {
        for (int i = 0; i < size; ++i) {
            checkIfMainElementEqualsZero(i);
            calculateColumnForL(i);
            for (int j = i + 1; j < size; ++j) {
                calculateRowForU(j, i);
            }
        }
    }

    private void checkIfMainElementEqualsZero(final int rowAndColumnIndex) {
        if (Double.valueOf(U[rowAndColumnIndex][rowAndColumnIndex]).equals(0d)) {
            for (int rowIndex = rowAndColumnIndex + 1; rowIndex < size; ++rowIndex) {
                if (!Double.valueOf(U[rowIndex][rowAndColumnIndex]).equals(0d)) {
                    double[] temp = new double[size];
                    System.arraycopy(U[rowAndColumnIndex], 0, temp, 0, size);
                    System.arraycopy(U[rowIndex], 0, U[rowAndColumnIndex], 0, size);
                    System.arraycopy(temp, 0, U[rowIndex], 0, size);
                    ++replacementCount;
                }
            }
        }
    }

    private void calculateColumnForL(final int columnIndex) {
        for (int i = columnIndex; i < size; ++i) {
            L[i][columnIndex] = U[i][columnIndex] / U[columnIndex][columnIndex];
        }
    }

    private void calculateRowForU(final int rowIndex,
                                  final int startColumnIndex) {
        for (int j = startColumnIndex; j < size; ++j) {
            U[rowIndex][j] = U[rowIndex][j] - L[rowIndex][startColumnIndex] * U[startColumnIndex][j];
        }
    }

    public void printL() {
        MatrixUtil.printMatrix(L);
    }

    public void printU() {
        MatrixUtil.printMatrix(U);
    }
}
