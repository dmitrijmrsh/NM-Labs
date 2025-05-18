package com.dmitriimrsh.nm.lab3.lsm;

import com.dmitriimrsh.nm.lab3.util.Util;
import com.dmitriimrsh.nm.lu.LUSolver;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.util.FastMath;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.List;

public class LSMSolver {

    private final List<Double> x;
    private final List<Double> y;
    private final int size;

    public LSMSolver(final List<Double> x,
                     final List<Double> y) {
        if (x.size() != y.size()) {
            throw new RuntimeException("Некорректные размерности");
        }
        this.size = x.size();
        this.x = x;
        this.y = y;
    }

    public double[] getApproxPolynomial(final int degree) {
        int rows = degree + 1;
        int cols = degree + 1;

        double[][] matrix = new double[rows][cols];

        int k;
        double value;

        for (int i = 0; i < rows; ++i) {
            k = 0;
            for (int j = 0; j < cols; ++j) {
                value = 0d;

                for (int m = 0; m < this.size; ++m) {
                    value += FastMath.pow(x.get(m), k + i);
                }

                matrix[i][j] = value;
                ++k;
            }
        }

        double[] values = new double[rows];
        k = 0;

        for (int i = 0; i < rows; ++i) {
            value = 0d;

            for (int j = 0; j < this.size; ++j) {
                value += y.get(j) * FastMath.pow(x.get(j), k);
            }

            values[i] = value;
            ++k;
        }

        LUSolver luSolver = new LUSolver(matrix);

        return luSolver.solveEquation(values);
    }

    public static double getSumOfSquaredErrors(final List<Double> a,
                                               final List<Double> y,
                                               final List<Double> x) {
        if (x.size() != y.size()) {
            throw new RuntimeException("Некорректные размерности");
        }

        final int size = x.size();
        double sum = 0d;

        for (int i = 0; i < size; ++i) {
            sum += (getPolynomial(a, x.get(i)).getValue() - y.get(i))
                    * (getPolynomial(a, x.get(i)).getValue() - y.get(i));
        }

        return sum;
    }

    public static void visualize(final List<Double> a,
                                 final List<Double> y,
                                 final List<Double> x) {
        final int num = 1000;
        final double[] xData = Util.linspace(x.get(0), x.get(x.size() - 1), num);

        double[] yData = new double[num];
        for (int i = 0; i < num; ++i) {
            yData[i] = getPolynomial(a, xData[i]).getValue();
        }

        int degree = a.size() - 1;

        XYChart chart = new XYChartBuilder()
                .title("Приближающий многочлен степени %d".formatted(degree))
                .xAxisTitle("X")
                .yAxisTitle("Y")
                .width(800)
                .height(600)
                .build();

        XYSeries polynomialSeries = chart.addSeries("y(x)", xData, yData);
        polynomialSeries.setLineColor(Color.BLUE);

        double[] nodeX = x.stream().mapToDouble(Double::doubleValue).toArray();
        double[] nodeY = y.stream().mapToDouble(Double::doubleValue).toArray();
        XYSeries nodeSeries = chart.addSeries("Узлы", nodeX, nodeY);

        nodeSeries.setLineColor(Color.RED);
        nodeSeries.setMarkerColor(Color.RED);
        nodeSeries.setMarker(SeriesMarkers.CIRCLE);
        nodeSeries.setLineStyle(SeriesLines.NONE);

        chart.getStyler().setXAxisDecimalPattern("#0.0");

        new SwingWrapper<>(chart).displayChart();
    }

    public static void print(final List<Double> a) {
        if (a.isEmpty()) {
            return;
        }

        System.out.printf("%.4f + ", a.get(0));
        for (int i = 1; i < a.size(); ++i) {
            if (i != a.size() - 1) {
                System.out.printf("( %.4f ) * x^%d + ", a.get(i), i);
                continue;
            }
            System.out.printf("( %.4f ) * x^%d", a.get(i), i);
        }
        System.out.print("\n");
    }

    private static DerivativeStructure getPolynomial(final List<Double> a,
                                                     final double x) {
        DerivativeStructure arg = new DerivativeStructure(1, 1, 0, x);

        DerivativeStructure polynomial = arg.pow(0).multiply(a.get(0));

        for (int i = 1; i < a.size(); ++i) {
            polynomial = polynomial.add(arg.pow(i).multiply(a.get(i)));
        }

        return polynomial;
    }

}
