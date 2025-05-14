package com.dmitriimrsh.nm.iterations;

import com.dmitriimrsh.nm.util.MatrixUtil;
import com.dmitriimrsh.nm.util.NormUtil;
import com.dmitriimrsh.nm.util.VectorUtil;

import java.util.Map;
import java.util.function.Function;

public class IterationsSolver {

    protected final double[][] alpha;
    protected final double[] beta;
    protected final int size;
    protected final double eps;
    protected final boolean isAlphaNormLessThenOne;
    protected final Function<double[], Double> vectorNormFunction;
    protected Function<double[][], Double> matrixNormFunction;

    public IterationsSolver(final double[][] matrix,
                            final double[] b,
                            final double epsilon) {
        size = matrix.length;
        eps = epsilon;
        alpha = new double[size][size];
        beta = new double[size];

        calculateAlpha(matrix);
        calculateBeta(matrix, b);
        isAlphaNormLessThenOne = checkIfAlphaNormLessThenOne();
        vectorNormFunction = NormUtil::calcVectorNormByMaxElem;
    }

    protected void calculateAlpha(final double[][] matrix) {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (i == j) {
                    alpha[i][j] = 0d;
                    continue;
                }

                alpha[i][j] = -matrix[i][j] / matrix[i][i];
            }
        }
    }

    protected void calculateBeta(final double[][] matrix,
                               final double[] b) {
        for (int i = 0; i < size; ++i) {
            beta[i] = b[i] / matrix[i][i];
        }
    }

    protected boolean checkIfAlphaNormLessThenOne() {
        if (NormUtil.calcMatrixNormByColumns(alpha) < 1d) {
            matrixNormFunction = NormUtil::calcMatrixNormByColumns;
            return true;
        }

        if (NormUtil.calcMatrixNormByRows(alpha) < 1d) {
            matrixNormFunction = NormUtil::calcMatrixNormByRows;
            return true;
        }

        if (NormUtil.calcMatrixNormBySquareRoot(alpha) < 1d) {
            matrixNormFunction = NormUtil::calcMatrixNormBySquareRoot;
            return true;
        }

        return false;
    }

    protected double calculateErrorRate(final double[] xCurrent,
                                        final double[] xPrev) {
        double errorRate = vectorNormFunction.apply(
                VectorUtil.calculateDiffForEqualSizedVectors(xCurrent, xPrev)
        );
        if (isAlphaNormLessThenOne) {
            errorRate *= matrixNormFunction.apply(alpha) / (1 - matrixNormFunction.apply(alpha));
        }
        return errorRate;
    }

    public Map.Entry<Integer, double[]> solve() {
        double[] xCurrent;
        double[] xPrev = new double[size];
        System.arraycopy(beta, 0, xPrev, 0, size);

        int iter = 0;
        while (true) {
            ++iter;
            xCurrent = VectorUtil.calculateSumForEqualSizedVectors(
                    beta,
                    MatrixUtil.multiplySquareMatrixOnVector(alpha, xPrev)
            );
            double errorRate = calculateErrorRate(xCurrent, xPrev);
            if (errorRate < eps) break;
            System.arraycopy(xCurrent, 0, xPrev, 0, size);
        }

        return Map.entry(iter, xCurrent);
    }

    public double generateAprioriEstimation() {
        if (!isAlphaNormLessThenOne)
            throw new RuntimeException(
                    "Apriori estimation couldn't be calculated because alpha norm is not lest then 1"
            );

        return (
                Math.log10(eps) -
                Math.log10(vectorNormFunction.apply(beta)) +
                Math.log10(1d - matrixNormFunction.apply(alpha))
        )
                / Math.log10(matrixNormFunction.apply(alpha));
    }

    public void printAlpha() {
        MatrixUtil.printMatrix(alpha);
    }

    public void printBetaTransposed() {
        for (double number : beta) {
            System.out.print(String.format("%.4f", number) + " ");
        }
        System.out.print("\n");
    }
}
