package com.dmitriimrsh.nm.qr;

import com.dmitriimrsh.nm.util.FunctionUtil;
import com.dmitriimrsh.nm.util.MatrixUtil;
import com.dmitriimrsh.nm.util.NormUtil;

public class QRDecomposition {

    private final double[][] Q;
    private final double[][] R;

    private QRDecomposition(final double[][] Q,
                            final double[][] R) {
        this.Q = Q;
        this.R = R;
    }

    private static double[][] getHouseholderMatrix(final double[][] A,
                                                   final int iter) {
        MatrixUtil.validateMatrixIsSquared(A);

        double[] v = new double[A.length];

        for (int i = 0; i < iter; ++i) {
            v[i] = 0;
        }

        double[] temp = new double[A.length - iter];
        for (int i = 0; i < temp.length; ++i) {
            temp[i] = A[iter + i][iter];
        }

        v[iter] = A[iter][iter] + FunctionUtil.sign(A[iter][iter])
                * NormUtil.calcVectorNormBySquareRoot(temp);

        for (int i = iter + 1; i < A.length; ++i) {
            v[i] = A[i][iter];
        }

        double[][] numerator = new double[v.length][v.length];
        for (int i = 0; i < v.length; ++i) {
            for (int j = 0; j < v.length; ++j) {
                numerator[i][j] = v[i] * v[j];
            }
        }

        double denominator = 0d;
        for (double value : v) {
            denominator += value * value;
        }

        MatrixUtil.divideMatrixOnNumber(numerator, denominator);
        MatrixUtil.multiplyMatrixOnNumber(numerator, 2d);

        return MatrixUtil.calculateDiffForSquaredMatrix(
                MatrixUtil.calculateIdentityMatrix(A.length),
                numerator
        );
    }

    public static QRDecomposition getQRDecomposition(final double[][] A) {
        double[][] Q  = MatrixUtil.calculateIdentityMatrix(A.length);
        double[][] R = MatrixUtil.copySquaredMatrix(A);

        for (int i = 0; i < A.length - 1; ++i) {
            double[][] H = getHouseholderMatrix(R, i);
            Q = MatrixUtil.multiplyTwoSquaredMatrix(Q, H);
            R = MatrixUtil.multiplyTwoSquaredMatrix(H, R);
        }

        return new QRDecomposition(Q, R);
    }

    public double[][] getQ() {
        return Q;
    }

    public double[][] getR() {
        return R;
    }

}
