package com.dmitriimrsh.nm.qr;

import com.dmitriimrsh.nm.util.MatrixUtil;
import com.dmitriimrsh.nm.util.NormUtil;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.analysis.solvers.PolynomialSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;
import java.util.List;

public class QRSolver {

    private final double[][] matrix;
    private final double eps;
    private final List<Complex> eigenValues;
    private int iterCount;

    public QRSolver(final double[][] inputMatrix,
                    final double epsilon) {
        MatrixUtil.validateMatrixIsSquared(inputMatrix);

        matrix = MatrixUtil.copySquaredMatrix(inputMatrix);
        eps = epsilon;
        eigenValues = new ArrayList<>();
        iterCount = 0;
    }

    private List<Complex> getRoots(final double[][] A,
                                   final int iter) {
        final int size = A.length;
        final boolean flag = iter + 1 < size;

        final double a11 = A[iter][iter];
        final double a12 = flag ? A[iter][iter + 1] : 0d;
        final double a21 = flag ? A[iter + 1][iter] : 0d;
        final double a22 = flag ? A[iter + 1][iter + 1] : 0d;

        double[] coeffs = {a11 * a22 - a12 * a21, -a11 - a22, 1};
        LaguerreSolver solver = new LaguerreSolver();

        return List.of(solver.solveAllComplex(coeffs, 0));
    }

    private boolean isComplex(final double[][] A,
                              final int iter,
                              final double epsilon) {
        QRDecomposition qrDecomposition = QRDecomposition.getQRDecomposition(A);

        double[][] next = MatrixUtil.multiplyTwoSquaredMatrix(
                qrDecomposition.getR(), qrDecomposition.getQ()
        );

        List<Complex> lambda1 = getRoots(A, iter);
        List<Complex> lambda2 = getRoots(next, iter);

        boolean firstIsComplex = lambda1.get(0).subtract(lambda2.get(0)).getImaginary() != 0d;
        boolean secondIsComplex = lambda1.get(1).subtract(lambda2.get(1)).getImaginary() != 0d;

        return (firstIsComplex || secondIsComplex) &&
                lambda1.get(0).subtract(lambda2.get(0)).abs() <= epsilon
                && lambda1.get(1).subtract(lambda2.get(1)).abs() <= epsilon;
    }

    private List<Complex> getEigenValue(double[][] current,
                                        final int columnIndex,
                                        final double epsilon) {
        int tempSize;
        iterCount = 0;
        while (true) {
            QRDecomposition qrDecomposition = QRDecomposition.getQRDecomposition(current);

            current = MatrixUtil.multiplyTwoSquaredMatrix(
                    qrDecomposition.getR(), qrDecomposition.getQ()
            );

            tempSize = current.length - columnIndex - 1;

            double[] vec1 = new double[Math.max(tempSize, 0)];
            for (int i = 0; i < vec1.length; ++i) {
                vec1[i] = current[i + columnIndex + 1][columnIndex];
            }

            tempSize = current.length - columnIndex - 2;

            double[] vec2 = new double[Math.max(tempSize, 0)];
            for (int i = 0; i < vec2.length; ++i) {
                vec2[i] = current[i + columnIndex + 2][columnIndex];
            }

            if (NormUtil.calcVectorNormBySquareRoot(vec1) <= epsilon) {
                if (columnIndex == current.length - 2) {
                    return List.of(
                            Complex.valueOf(current[columnIndex][columnIndex]),
                            Complex.valueOf(current[columnIndex + 1][columnIndex + 1])
                    );
                }
                return List.of(Complex.valueOf(current[columnIndex][columnIndex]));
            }

            if (NormUtil.calcVectorNormBySquareRoot(vec2) <= epsilon && isComplex(current, columnIndex, epsilon)) {
                return getRoots(current, columnIndex);
            }
        }
    }

    public void solve() {
        final int size = matrix.length;
        double[][] current = MatrixUtil.copySquaredMatrix(matrix);

        int columnIndex = 0;

        while (columnIndex < size) {
            List<Complex> ans = getEigenValue(current, columnIndex, eps);

            if (ans.size() == 2) {
                eigenValues.add(ans.get(0));
                eigenValues.add(ans.get(1));
                columnIndex += 2;
            } else if (ans.size() == 1) {
                eigenValues.add(ans.get(0));
                columnIndex += 1;
            } else {
                throw new RuntimeException("Invalid size of eigen values sublist");
            }
        }
    }

    public List<Complex> getEigenValues() {
        return eigenValues;
    }

}
