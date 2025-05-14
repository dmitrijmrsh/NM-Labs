package com.dmitriimrsh.nm.iterations;

import com.dmitriimrsh.nm.util.NormUtil;
import com.dmitriimrsh.nm.util.VectorUtil;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

import static com.dmitriimrsh.nm.util.Util.System.*;
import static com.dmitriimrsh.nm.util.Util.System.func_2;

public class IterationsSystemSolver {

    private final double[] x0;
    private final double[] a;
    private final double[] b;
    private final int size;
    private final double eps;
    private final double q;

    private int iter;

    public IterationsSystemSolver(final double[] initialApprox,
                                  final double[] leftBorders,
                                  final double[] rightBorders,
                                  final double epsilon) {
        checkInput(initialApprox, leftBorders, rightBorders);

        this.size = initialApprox.length;
        this.x0 = new double[size];
        this.a = new double[size];
        this.b = new double[size];
        this.eps = epsilon;

        System.arraycopy(initialApprox, 0, this.x0, 0, size);
        System.arraycopy(leftBorders, 0, this.a, 0, size);
        System.arraycopy(rightBorders, 0, this.b, 0, size);

        this.q = checkDerivativeMatrixThenReturnMax();
    }

    private double[][] buildDerivativeMatrix(final DerivativeStructure... args) {
        DerivativeStructure phi1 = phi_1(args[0]);
        DerivativeStructure phi2 = phi_2(args[1]);

        return new double[][] {
                {phi1.getPartialDerivative(1, 0), phi1.getPartialDerivative(0, 1)},
                {phi2.getPartialDerivative(1, 0), phi2.getPartialDerivative(0, 1)}
        };
    }

    private void checkInput(final double[] initialApprox,
                            final double[] leftBorders,
                            final double[] rightBorders) {
        if (initialApprox.length != leftBorders.length) {
            throw new RuntimeException("Некорректные размерности");
        }

        if (leftBorders.length != rightBorders.length) {
            throw new RuntimeException("Некорректные размерности");
        }

        for (int i = 0; i < initialApprox.length; ++i) {
            if (initialApprox[i] < leftBorders[i] || initialApprox[i] > rightBorders[i]) {
                throw new RuntimeException("Выход за границы области");
            }
        }
    }

    private double checkDerivativeMatrixThenReturnMax() {
        final int order = 2;
        final DerivativeStructure x1 = new DerivativeStructure(2, order, 0, b[0]);
        final DerivativeStructure x2 = new DerivativeStructure(2, order, 1, b[1]);

        double[][] derivativeMatrix = buildDerivativeMatrix(x1, x2);
        double max = 0d;

        for (double[] row : derivativeMatrix) {
            double sum = 0d;
            for (double value : row) {
                sum += Math.abs(value);
            }
            if (sum > max) {
                max = sum;
            }
        }

        if (max >= 1) {
            throw new RuntimeException("Не выполняется условие сходимости");
        }

        return max;
    }

    private double[] getX(final double[] xPrev) {
        final int order = 2;
        final DerivativeStructure x1Prev = new DerivativeStructure(2, order, 0, xPrev[0]);
        final DerivativeStructure x2Prev = new DerivativeStructure(2, order, 1, xPrev[1]);

        final double x1 = phi_1(x2Prev).getValue();
        final double x2 = phi_2(x1Prev).getValue();

        final boolean x1IsValid = x1 >= a[0] && x1 <= b[0];
        final boolean x2IsValid = x2 >= a[1] && x2 <= b[1];

        if (!x1IsValid || !x2IsValid) {
            throw new RuntimeException("Выход за границы области");
        }

        return new double[] {x1, x2};
    }

    private boolean endingCheck(final double[] xPrev,
                                final double[] x) {
        final double[] diff = VectorUtil.calculateDiffForEqualSizedVectors(x, xPrev);
        final double norm = NormUtil.calcVectorNormByMaxElem(diff);
        return q / (1 - q) * norm <= eps;
    }

    public double[] solve() {
        iter = 1;
        double[] xPrev = new double[size];
        double[] x = new double[size];

        System.arraycopy(x0, 0, xPrev, 0, size);
        System.arraycopy(getX(xPrev), 0, x, 0, size);

        while (!endingCheck(xPrev, x)) {
            System.arraycopy(x, 0, xPrev, 0, size);
            System.arraycopy(getX(xPrev), 0, x, 0, size);
            ++iter;
        }

        return x;
    }

    public boolean check(final double... args) {
        final DerivativeStructure x1 = new DerivativeStructure(2, 2, 0, args[0]);
        final DerivativeStructure x2 = new DerivativeStructure(2, 2, 1, args[1]);

        return func_1(x1, x2).getValue() < eps && func_2(x1, x2).getValue() < eps;
    }

    public int getIter() {
        return iter;
    }

    public double getQ() {
        return q;
    }
}
