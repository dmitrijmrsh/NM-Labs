package com.dmitriimrsh.nm.jacobi;

import com.dmitriimrsh.nm.util.MatrixUtil;
import com.dmitriimrsh.nm.util.VectorUtil;

import java.util.ArrayList;
import java.util.List;

public class JacobiSolver {

    private final double[][] matrix;
    private final double eps;
    private final double[] eigenValues;
    private final double[][] eigenVectors;
    private int iterCount;

    public JacobiSolver(final double[][] inputMatrix,
                        final double epsilon) {
        MatrixUtil.validateSquareMatrixSymmetry(inputMatrix);

        matrix = MatrixUtil.copySquaredMatrix(inputMatrix);
        eps = epsilon;
        eigenValues = new double[inputMatrix.length];
        eigenVectors = new double[inputMatrix.length][inputMatrix.length];
        iterCount = 0;
    }

    public void solve() {
        double[][] A = MatrixUtil.copySquaredMatrix(matrix);
        double phi;

        double max;
        int maxRowIndex;
        int maxColumnIndex;

        iterCount = 0;

        List<double[][]> uMatrixList = new ArrayList<>();

        while (true) {
            max = -1d;
            maxRowIndex = -1;
            maxColumnIndex = -1;

            ++iterCount;

            for (int i = 0; i < A.length; ++i) {
                for (int j = 0; j < A.length; ++j) {
                    if (i >= j) {
                        continue;
                    }

                    if (Math.abs(A[i][j]) > max) {
                        max = Math.abs(A[i][j]);
                        maxRowIndex = i;
                        maxColumnIndex = j;
                    }
                }
            }

            phi = calculatePhi(A, maxRowIndex, maxColumnIndex);
            double[][] U = calculateU(phi, maxRowIndex, maxColumnIndex, A.length);

            uMatrixList.add(U);

            A = MatrixUtil.multiplyTwoSquaredMatrix(
                    MatrixUtil.multiplyTwoSquaredMatrix(
                            MatrixUtil.transposeSquareMatrix(U),
                            A
                    ),
                    U
            );

            if (calculateMatrixNorm(A) < eps) {
                for (int i = 0; i < eigenValues.length; ++i) {
                    eigenValues[i] = A[i][i];
                }

                double[][] temp = uMatrixList.get(0);
                for (int i = 1; i < uMatrixList.size(); ++i) {
                    temp = MatrixUtil.multiplyTwoSquaredMatrix(temp, uMatrixList.get(i));
                }

                for (int i = 0; i < temp.length; ++i) {
                    System.arraycopy(temp[i], 0, eigenVectors[i], 0, temp.length);
                }

                break;
            }
        }
    }

    private double calculatePhi(final double[][] A,
                                final int maxRowIndex,
                                final int maxColumnIndex) {
        if (A[maxRowIndex][maxRowIndex] == A[maxColumnIndex][maxColumnIndex]) {
            return Math.PI / 4.0;
        }

        return 0.5 * Math.atan(
                2.0 * A[maxRowIndex][maxColumnIndex]
                /
                (A[maxRowIndex][maxRowIndex] - A[maxColumnIndex][maxColumnIndex])
        );
    }

    private double[][] calculateU(final double phi,
                                  final int maxRowIndex,
                                  final int maxColumnIndex,
                                  final int size) {
        double[][] U = new double[size][size];

        for (int i = 0; i < U.length; ++i) {
            for (int j = 0; j < U.length; ++j) {
                if (i == maxRowIndex && j == maxColumnIndex) {
                    U[i][j] = - Math.sin(phi);
                    continue;
                }

                if (i == maxColumnIndex && j == maxRowIndex) {
                    U[i][j] = Math.sin(phi);
                    continue;
                }

                if (i == maxRowIndex && j == maxRowIndex) {
                    U[i][j] = Math.cos(phi);
                    continue;
                }

                if (i == maxColumnIndex && j == maxColumnIndex) {
                    U[i][j] = Math.cos(phi);
                    continue;
                }

                if (i == j) {
                    U[i][j] = 1d;
                    continue;
                }

                U[i][j] = 0d;
            }
        }

        return U;
    }

    private double calculateMatrixNorm(final double[][] A) {
        double t = 0d;

        for (int i = 0; i < A.length; ++i) {
            for (int j = 0; j < A.length; ++j) {
                if (i >= j) {
                    continue;
                }

                t += A[i][j] * A[i][j];
            }
        }

        return Math.sqrt(t);
    }

    public boolean check() {
        for (int i = 0; i < eigenValues.length; ++i) {
            double[] eigenVector = getEigenVector(i);

            double[] matrixOnVector = MatrixUtil.multiplySquareMatrixOnVector(
                    matrix, eigenVector
            );
            double[] vectorOnNumber = VectorUtil.multiplyVectorOnNumber(
                    eigenVector, eigenValues[i]
            );

            if (!VectorUtil.compare(matrixOnVector, vectorOnNumber, eps)) {
                return false;
            }
        }

        return true;
    }

    private double[] getEigenVector(final int index) {
        double[] eigenVector = new double[eigenVectors.length];

        for (int i = 0; i < eigenVector.length; ++i) {
            eigenVector[i] = eigenVectors[i][index];
        }

        return eigenVector;
    }

    public int getIterCount() {
        return iterCount;
    }

    public double[] getEigenValues() {
        return eigenValues;
    }

    public double[][] getEigenVectors() {
        return eigenVectors;
    }
}
