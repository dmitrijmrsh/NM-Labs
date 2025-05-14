package com.dmitriimrsh.nm.seidel;

import com.dmitriimrsh.nm.iterations.IterationsSolver;
import com.dmitriimrsh.nm.lu.LUSolver;
import com.dmitriimrsh.nm.util.MatrixUtil;
import com.dmitriimrsh.nm.util.VectorUtil;

import java.util.Map;

public class SeidelSolver extends IterationsSolver {

    private final double[][] B;
    private final double[][] C;
    private final double[][] seidelAlpha;
    private final double[] seidelBeta;

    public SeidelSolver(final double[][] matrix,
                        final double[] b,
                        final double epsilon) {
        super(matrix, b, epsilon);
        B = new double[size][size];
        C = new double[size][size];
        calculateB();
        calculateC();
        LUSolver luSolver = new LUSolver(calculateDiffEB());
        seidelAlpha = MatrixUtil.multiplyTwoSquaredMatrix(
                luSolver.getInvertedMatrix(),
                C
        );
        seidelBeta = MatrixUtil.multiplySquareMatrixOnVector(
                luSolver.getInvertedMatrix(),
                beta
        );
    }

    @Override
    public Map.Entry<Integer, double[]> solve() {
        double[] xCurrent;
        double[] xPrev = new double[size];
        System.arraycopy(seidelBeta, 0, xPrev, 0, size);

        int iter = 0;
        while (true) {
            ++iter;
            xCurrent = VectorUtil.calculateSumForEqualSizedVectors(
                    seidelBeta,
                    MatrixUtil.multiplySquareMatrixOnVector(seidelAlpha, xPrev)
            );
            double errorRate = calculateErrorRate(xCurrent, xPrev);
            if (errorRate < eps) break;
            System.arraycopy(xCurrent, 0, xPrev, 0, size);
        }

        return Map.entry(iter, xCurrent);
    }

    @Override
    public double generateAprioriEstimation() {
        if (!isAlphaNormLessThenOne)
            throw new RuntimeException(
                    "Apriori estimation couldn't be calculated because alpha norm is not lest then 1"
            );

        return (
                Math.log10(eps) -
                        Math.log10(vectorNormFunction.apply(seidelBeta)) +
                        Math.log10(1d - matrixNormFunction.apply(seidelAlpha))
        )
                / Math.log10(matrixNormFunction.apply(seidelAlpha));
    }

    @Override
    protected double calculateErrorRate(final double[] xCurrent,
                                        final double[] xPrev) {
        double errorRate = vectorNormFunction.apply(
                VectorUtil.calculateDiffForEqualSizedVectors(xCurrent, xPrev)
        );
        if (isAlphaNormLessThenOne) {
            errorRate *= matrixNormFunction.apply(C) / (1 - matrixNormFunction.apply(alpha));
        }
        return errorRate;
    }

    private void calculateB() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (j >= i) {
                    B[i][j] = 0d;
                    continue;
                }

                B[i][j] = alpha[i][j];
            }
        }
    }

    private void calculateC() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (j < i) {
                    C[i][j] = 0d;
                    continue;
                }

                C[i][j] = alpha[i][j];
            }
        }
    }

    private double[][] calculateDiffEB() {
        double[][] E = MatrixUtil.calculateIdentityMatrix(size);
        return MatrixUtil.calculateDiffForSquaredMatrix(E, B);
    }

    public void printB() {
        MatrixUtil.printMatrix(B);
    }

    public void printC() {
        MatrixUtil.printMatrix(C);
    }

}
