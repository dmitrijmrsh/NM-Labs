package com.dmitriimrsh.nm.newton;

import com.dmitriimrsh.nm.util.Util;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

import static com.dmitriimrsh.nm.util.Util.Linear.ARGS_COUNT;
import static com.dmitriimrsh.nm.util.Util.Linear.func;

public class NewtonLinearSolver {

    private final double x0;
    private final double eps;

    private int iter;

    public NewtonLinearSolver(final double x0,
                              final double a,
                              final double b,
                              final double eps) {
        checkParams(a, b, x0, eps);

        this.x0 = x0;
        this.eps = eps;
    }

    private DerivativeStructure getDerivativeStructure(final DerivativeStructure x) {
        return x.sin().subtract(x.multiply(x).multiply(2.0)).add(0.5);
    }

    private void checkDerivativeIsOnlyPositiveOrNegative(final double a,
                                                         final double b,
                                                         final int order) {
        double[] args = Util.linspace(a, b, ARGS_COUNT);

        boolean isFirstArgPositive;

        isFirstArgPositive = getDerivativeStructure(
                new DerivativeStructure(1, 3, 0, args[0])
        ).getPartialDerivative(order) > 0d;

        for (int i = 1; i < args.length; ++i) {
            DerivativeStructure argument = new DerivativeStructure(1, 3, 0, args[i]);
            DerivativeStructure derivativeStructure = getDerivativeStructure(argument);

            if (isFirstArgPositive) {
                if (derivativeStructure.getPartialDerivative(order) < 0d) {
                    throw new RuntimeException(
                            "Производная функции порядка %s не имеет постоянный знак на отрезке [%s, %s]"
                                    .formatted(order, a, b)
                    );
                }
            } else {
                if (derivativeStructure.getPartialDerivative(order) > 0d) {
                    throw new RuntimeException(
                            "Производная функции порядка %s не имеет постоянный знак на отрезке [%s, %s]"
                                    .formatted(order, a, b)
                    );
                }
            }
        }
    }

    private void checkParams(final double a,
                             final double b,
                             final double x0,
                             final double eps) {
        checkDerivativeIsOnlyPositiveOrNegative(a, b, 1);
        checkDerivativeIsOnlyPositiveOrNegative(a, b, 2);

        if (func.apply(a) * func.apply(b) >= 0) {
            throw new RuntimeException("Концы отрезка не соответствуют критерию сходимости");
        }

        DerivativeStructure initialApproximation = new DerivativeStructure(1,3,0, x0);
        DerivativeStructure derivativeStructure = getDerivativeStructure(initialApproximation);

        if (func.apply(x0) * derivativeStructure.getPartialDerivative(2) <= 0) {
            throw new RuntimeException("Начальное приближение не соответствует критерию сходимости");
        }

        double[] args = Util.linspace(a, b, ARGS_COUNT);
        for (double x : args) {
            DerivativeStructure argument = new DerivativeStructure(1, 3, 0, x);
            derivativeStructure = getDerivativeStructure(argument);

            if (Math.abs(derivativeStructure.getPartialDerivative(1)) < eps) {
                throw new RuntimeException("Первая производная функции не может принимать 0-вые значения");
            }
        }
    }

    private double getX(final double xPrev) {
        DerivativeStructure argument = new DerivativeStructure(1, 3, 0, xPrev);
        DerivativeStructure derivativeStructure = getDerivativeStructure(argument);

        return xPrev - func.apply(xPrev) / derivativeStructure.getPartialDerivative(1);
    }

    public double solve() {
        iter = 1;
        double xPrev = x0;
        double x = getX(xPrev);

        while (Math.abs(x - xPrev) >= eps) {
            ++iter;
            xPrev = x;
            x = getX(xPrev);
        }

        return x;
    }

    public boolean check(final double x) {
        return func.apply(x) < eps;
    }

    public int getIter() {
        return iter;
    }
}
