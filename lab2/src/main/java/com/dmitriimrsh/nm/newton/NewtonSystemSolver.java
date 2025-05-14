package com.dmitriimrsh.nm.newton;

import com.dmitriimrsh.nm.lu.LUSolver;
import com.dmitriimrsh.nm.util.MatrixUtil;
import com.dmitriimrsh.nm.util.NormUtil;
import com.dmitriimrsh.nm.util.VectorUtil;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

import static com.dmitriimrsh.nm.util.Util.System.func_1;
import static com.dmitriimrsh.nm.util.Util.System.func_2;

public class NewtonSystemSolver {

    private final double[] x0;
    private final double eps;
    private final int size;

    private int iter;

    public NewtonSystemSolver(final double[] initialApprox,
                              final double epsilon) {
        this.size = initialApprox.length;
        this.x0 = new double[size];
        this.eps = epsilon;

        System.arraycopy(initialApprox, 0, x0, 0, initialApprox.length);
    }

    private double[][] buildJacobiMatrix(final DerivativeStructure... args) {
        DerivativeStructure f1 = func_1(args);
        DerivativeStructure f2 = func_2(args);

        double[][] jacobiMatrix = new double[][] {
                {f1.getPartialDerivative(1, 0), f1.getPartialDerivative(0, 1)},
                {f2.getPartialDerivative(1, 0), f2.getPartialDerivative(0, 1)}
        };

        if (jacobiMatrix[0][0] * jacobiMatrix[1][1] - jacobiMatrix[0][1] * jacobiMatrix[1][0] == eps) {
            throw new RuntimeException("Матрица Якоби вырождена");
        }

        return jacobiMatrix;
    }

    private double[] buildVectorFunction(final DerivativeStructure... args) {
        DerivativeStructure f1 = func_1(args);
        DerivativeStructure f2 = func_2(args);

        return new double[] {
                f1.getValue(), f2.getValue()
        };
    }

    private double[] getX(final double[] xPrev) {
        final int order = 2;

        final DerivativeStructure x1 = new DerivativeStructure(2, order, 0, xPrev[0]);
        final DerivativeStructure x2 = new DerivativeStructure(2, order, 1, xPrev[1]);

        final double[][] jacobiMatrix = buildJacobiMatrix(x1, x2);
        final double[][] invertedJacobiMatrix = new LUSolver(jacobiMatrix).getInvertedMatrix();
        final double[] vectorFunction = buildVectorFunction(x1, x2);

        return VectorUtil.calculateDiffForEqualSizedVectors(
                xPrev,
                MatrixUtil.multiplySquareMatrixOnVector(invertedJacobiMatrix, vectorFunction)
        );
    }

    public double[] solve() {
        iter = 1;
        double[] xPrev = new double[size];
        double[] x = new double[size];

        System.arraycopy(x0, 0, xPrev, 0, size);
        System.arraycopy(getX(xPrev), 0, x, 0, size);

        while (NormUtil.calcVectorNormByMaxElem(VectorUtil.calculateDiffForEqualSizedVectors(x, xPrev)) > eps) {
            System.arraycopy(x, 0, xPrev, 0, size);
            System.arraycopy(getX(xPrev), 0, x, 0, size);
            ++iter;
        }

        return x;
    }

    public boolean check(final double... args) {
        final int order = 2;

        final DerivativeStructure x1 = new DerivativeStructure(2, order, 0, args[0]);
        final DerivativeStructure x2 = new DerivativeStructure(2, order, 1, args[1]);

        return func_1(x1, x2).getValue() < eps && func_2(x1, x2).getValue() < eps;
    }

    public int getIter() {
        return iter;
    }
}
