package com.dmitriimrsh.nm.lab3.interpolation;

import java.util.List;
import java.util.function.Function;

public class Newton extends Solver {

    private double[] separatedDiffs;

    public Newton(final Function<Double, Double> y,
                  final List<Double> x) {
        super(y, x);
        init();
    }

    private void init() {
        separatedDiffs = new double[x.size()];

        for (int i = 0; i < x.size(); ++i) {
            if (i == 0) {
                separatedDiffs[i] = x.get(i);
                continue;
            }

            separatedDiffs[i] = getSeparatedDiff(0, i);
        }
    }

    private double getSeparatedDiff(final int leftBorder,
                                    final int rightBorder) {
        if (leftBorder == rightBorder) {
            return y.apply(x.get(leftBorder));
        }

        if (leftBorder == rightBorder - 1) {
            return (
                    getSeparatedDiff(leftBorder, leftBorder)
                    - getSeparatedDiff(rightBorder, rightBorder)
            ) /
                    (x.get(leftBorder) - x.get(rightBorder));
        }

        return (getSeparatedDiff(leftBorder, rightBorder - 1)
                - getSeparatedDiff(leftBorder + 1, rightBorder)
        ) /
                (x.get(leftBorder) - x.get(rightBorder));
    }

    private double getUniqueSeparatedDiff() {
        return (y.apply(x.get(1)) - y.apply(x.get(0))) / (x.get(1) - x.get(0));
    }

    @Override
    public double value(final double arg) {
        checkArgValidity(arg);

        double res = 0d;
        double temp;

        for (int i = 0; i < x.size(); ++i) {
            if (i == 0) {
                res += y.apply(x.get(0));
                continue;
            }

            if (i == 1) {
                temp = getUniqueSeparatedDiff();
            } else {
                temp = separatedDiffs[i];
            }

            for (int j = 0; j < i; ++j) {
                temp *= (arg - x.get(j));
            }

            res += temp;
        }

        return res;
    }

    @Override
    public void print() {
        double separatedDiff;
        System.out.printf("P%s(x) = ", x.size() - 1);

        for (int i = 0; i < x.size(); ++i) {
            if (i == 0) {
                System.out.printf("%.5f", y.apply(x.get(0)));
                continue;
            }

            if (i == 1) {
                separatedDiff = getUniqueSeparatedDiff();
            } else {
                separatedDiff = separatedDiffs[i];
            }

            if (separatedDiff >= 0d) {
                System.out.printf(" + %.5f", separatedDiff);
            } else {
                System.out.printf(" - %.5f", -separatedDiff);
            }

            for (int j = 0; j < i; ++j) {
                if (x.get(j) >= 0d) {
                    System.out.printf("(x - %s)", x.get(j));
                } else {
                    System.out.printf("(x + %s)", -x.get(j));
                }
            }
        }
        System.out.print("\n");
    }
}
