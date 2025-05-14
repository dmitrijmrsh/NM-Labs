package com.dmitriimrsh.nm.iterations;

import com.dmitriimrsh.nm.util.Util;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

import java.util.Arrays;
import java.util.Objects;

import static com.dmitriimrsh.nm.util.Util.Linear.*;

public class IterationsLinearSolver {

    private final double x0;
    private final double q;
    private final double eps;

    private int iter;

    public IterationsLinearSolver(final double x0,
                                  final double a,
                                  final double b,
                                  final double eps) {
        double max = checkParamsThenReturnMax(a, b);

        this.x0 = x0;
        this.eps = eps;
        this.q = max;
    }

    private DerivativeStructure getDerivativeStructure(final DerivativeStructure x) {
        return x.sin()
                .add(0.5)
                .multiply(0.5)
                .sqrt();
    }

    private double checkParamsThenReturnMax(final double a,
                                            final double b) {
        double[] args = Util.linspace(a, b, ARGS_COUNT);

        for (double x : args) {
            if (phi.apply(x) < a || phi.apply(x) > b) {
                throw new RuntimeException("Не выполняется первое условие сходимости");
            }
        }

        args = Arrays.stream(args).filter(arg -> arg > a && arg < b).toArray();
        Double max = null;

        for (double x : args) {
            DerivativeStructure argument = new DerivativeStructure(1, 3, 0, x);
            DerivativeStructure derivativeStructure = getDerivativeStructure(argument);

            double value = derivativeStructure.getPartialDerivative(1);

            if (Objects.isNull(max)) {
                max = value;
                continue;
            }

            if (value > max) {
                max = value;
            }
        }

        if (Objects.isNull(max)) {
            throw new NullPointerException();
        }

        if (max >= 1) {
            throw new RuntimeException("Не выполняется второе условие сходимости");
        }

        return max;
    }

    public double solve() {
        iter = 1;
        double xPrev = x0;
        double x = phi.apply(xPrev);

        while (q / (1 - q) * Math.abs(x - xPrev) > eps) {
            ++iter;
            xPrev = x;
            x = phi.apply(xPrev);
        }

        return x;
    }

    public boolean check(final double x) {
        return func.apply(x) <= eps;
    }

    public int getIter() {
        return iter;
    }

    public double getQ() {
        return q;
    }
}
