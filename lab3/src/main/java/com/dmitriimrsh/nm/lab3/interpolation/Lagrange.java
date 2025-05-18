package com.dmitriimrsh.nm.lab3.interpolation;

import com.dmitriimrsh.nm.lab3.util.Util;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Lagrange extends Solver {

    private List<DerivativeStructure> derivatives;
    private PolynomialFunction polynomialFunction;
    private List<Double> coeffs;

    public Lagrange(final Function<Double,Double> y,
                    final List<Double> x) {
        super(y, x);
        init();
        build();
    }

    private void init() {
        polynomialFunction = new PolynomialFunction(new double[] {-x.get(0), 1});
        for (int i = 1; i < x.size(); ++i) {
            PolynomialFunction factor = new PolynomialFunction(new double[] {-x.get(i), 1});
            polynomialFunction = polynomialFunction.multiply(factor);
        }

        final int params = 1;
        final int order = 1;
        final int index = 0;

        derivatives = new ArrayList<>();
        for (var value : x) {
            derivatives.add(new DerivativeStructure(params, order, index, value));
        }
    }

    private void build() {
        coeffs = new ArrayList<>();

        for (int i = 0; i < x.size(); ++i) {
            coeffs.add(
                    y.apply(x.get(i))
                    / polynomialFunction.value(derivatives.get(i)).getPartialDerivative(1)
            );
        }
    }

    @Override
    public double value(final double arg) {
        checkArgValidity(arg);

        double res = 0d;
        double temp;

        for (int i = 0; i < coeffs.size(); ++i) {
            temp = coeffs.get(i);
            for (int j = 0; j < x.size(); ++j) {
                if (i == j) {
                    continue;
                }

                temp *= (arg - x.get(j));
            }
            res += temp;
        }

        return res;
    }

    @Override
    public void print() {
        System.out.printf("L%s(x) = ", x.size() - 1);
        for (int i = 0; i < coeffs.size(); ++i) {
            if (i == 0) {
                System.out.printf("%.5f", coeffs.get(i));
            } else {
                if (coeffs.get(i) >= 0d) {
                    System.out.printf(" + %.5f", coeffs.get(i));
                } else {
                    System.out.printf(" - %.5f", -coeffs.get(i));
                }
            }

            for (int j = 0; j < x.size(); ++j) {
                if (j == i) {
                    continue;
                }

                if (x.get(j) >= 0d) {
                    System.out.printf("(x - %s)", x.get(j));
                } else {
                    System.out.printf("(x + %s)", -x.get(j));
                }
            }
        }
        System.out.print("\n");
    }

    @Override
    public void visualize() {
        final int num = 1000;
        final double[] xData = Util.linspace(x.get(0), x.get(x.size() - 1), num);

        double[] yData = new double[num];
        for (int i = 0; i < num; ++i){
            yData[i] = value(xData[i]);
        }

        XYChart chart = QuickChart.getChart(
                "Интерполяция методом Лагранжа",
                "X",
                "Y",
                "y(x)",
                xData,
                yData
        );

        chart.getStyler().setXAxisDecimalPattern("#0.0");

        new SwingWrapper<>(chart).displayChart();
    }
}
