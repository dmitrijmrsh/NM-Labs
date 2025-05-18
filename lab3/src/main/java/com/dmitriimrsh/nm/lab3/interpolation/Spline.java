package com.dmitriimrsh.nm.lab3.interpolation;

import com.dmitriimrsh.nm.lab3.util.Util;
import com.dmitriimrsh.nm.lu.LUSolver;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.knowm.xchart.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Spline {

    private final List<Double> x;
    private final List<Double> y;
    private final int size;

    private List<Double> h;
    private List<Double> c;
    private List<Double> a;
    private List<Double> b;
    private List<Double> d;

    private static final double eps = 0.000001;

    public Spline(final List<Double> x,
                  final List<Double> y) {
        this.x = x;
        this.y = y;

        if (x.isEmpty()) {
            throw new RuntimeException("Набор узлов не может быть пустым");
        }

        if (x.size() == 1) {
            throw new RuntimeException("В непустом наборе узлов их кол-во должно быть 2 и более");
        }

        if (x.size() != y.size()) {
            throw new RuntimeException("Некорректные размерности");
        }

        this.size = x.size();

        init();
        buildC();
        buildA();
        buildB();
        buildD();
    }

    private void init() {
        h = new ArrayList<>();
        h.add(0d);

        for (int i = 1; i < x.size(); ++i) {
            h.add(x.get(i) - x.get(i - 1));
        }
    }

    private void buildC() {
        c = new ArrayList<>();
        c.add(0d);

        double[][] coeffs = new double[size - 2][size - 2];
        double[] b = new double[size - 2];

        coeffs[0][0] = 2 * (h.get(1) + h.get(2));
        coeffs[0][1] = h.get(2);
        b[0] = 3 * ((y.get(2) - y.get(1)) / h.get(2) - (y.get(1) - y.get(0)) / h.get(1));

        for (int i = 3; i <= size - 2; ++i) {
            coeffs[i - 2][i - 3] = h.get(i - 1);
            coeffs[i - 2][i - 2] = 2 * (h.get(i - 1) + h.get(i));
            coeffs[i - 2][i - 1] = h.get(i);
            b[i - 2] = 3 * ((y.get(i) - y.get(i - 1)) / h.get(i) - (y.get(i - 1) - y.get(i - 2)) / h.get(i - 1));
        }

        coeffs[coeffs.length - 1][coeffs.length - 2] = h.get(size - 2);
        coeffs[coeffs.length - 1][coeffs.length - 1] = 2 * (h.get(size - 2) + h.get(size - 1));
        b[b.length - 1] = 3 * ((y.get(size - 1) - y.get(size - 2)) / h.get(size - 1) - (y.get(size - 2) - y.get(size - 3)) / h.get(size - 2));

        LUSolver luSolver = new LUSolver(coeffs);
        double[] res = luSolver.solveEquation(b);

        for (var value : res) {
            c.add(value);
        }
    }

    private void buildA() {
        a = new ArrayList<>();

        for (int i = 1; i < size; ++i) {
            a.add(y.get(i - 1));
        }
    }

    private void buildB() {
        b = new ArrayList<>();

        for (int i = 1; i <= size - 2; ++i) {
            b.add((y.get(i) - y.get(i - 1)) / h.get(i) - h.get(i) * (c.get(i) + 2 * c.get(i - 1)) / 3.);
        }

        b.add((y.get(size - 1) - y.get(size - 2)) / h.get(size - 1) - (2. / 3.) * h.get(size - 1) * c.get(size - 2));
    }

    private void buildD() {
        d = new ArrayList<>();

        for (int i = 1; i < size - 1; ++i) {
            d.add((c.get(i) - c.get(i - 1)) / (3. * h.get(i)));
        }

        d.add(-c.get(size - 2) / (3. * h.get(size - 1)));
    }

    public double value(final double arg) {
        for (int i = 0; i < x.size() - 1; ++i) {
            if (arg >= x.get(i) && arg <= x.get(i + 1)) {
                return polynomial(i, arg).getValue();
            }
        }
        throw new RuntimeException("Аргумент не входит в область определения интерполяции");
    }

    private DerivativeStructure polynomial(final int index,
                                           final double arg) {
        DerivativeStructure x = new DerivativeStructure(1, 3, 0, arg);
        DerivativeStructure difference = x.add(-this.x.get(index));
        return difference.pow(3).multiply(this.d.get(index))
                .add(difference.pow(2).multiply(this.c.get(index)))
                .add(difference.multiply(this.b.get(index)))
                .add(a.get(index));
    }

    public boolean check() {
        /*
           Левый и правый крайние узлы
        */
        if (Math.abs(y.get(0) - value(x.get(0))) >= eps) {
            return false;
        }

        if (Math.abs(y.get(y.size() - 1) - value(x.get(x.size() - 1))) >= eps) {
            return false;
        }

        /*
           Непрерывность сплайна в не краевых узлах,
           равенство соответствующих значений функции и
           сплайна
        */
        for (int i = 1; i < x.size() - 1; ++i) {
            if (Math.abs(polynomial(i - 1, x.get(i)).getValue() - polynomial(i, x.get(i)).getValue()) >= eps) {
                return false;
            }
            if (Math.abs(y.get(i) - value(x.get(i))) >= eps) {
                return false;
            }
        }

        /*
           Непрерывность сплайна в не краевых узлах
           по первой производной
        */
        for (int i = 1; i < x.size() - 1; ++i) {
            if (
                    Math.abs(polynomial(i - 1, x.get(i)).getPartialDerivative(1)
                    - polynomial(i, x.get(i)).getPartialDerivative(1)) >= eps
            ) {
                return false;
            }
        }

        /*
           Непрерывность сплайна в не краевых узлах
           по второй производной
        */
        for (int i = 1; i < x.size() - 1; ++i) {
            if (
                    Math.abs(polynomial(i - 1, x.get(i)).getPartialDerivative(2)
                    - polynomial(i, x.get(i)).getPartialDerivative(2)) >= eps
            ) {
                return false;
            }
        }

        /*
           Вторые производные сплайнов в узлах
           x_0 и x_n равны нулю
        */
        if (Math.abs(polynomial(0, x.get(0)).getPartialDerivative(2)) >= eps) {
            return false;
        }

        return !(Math.abs(polynomial(x.size() - 2, x.get(x.size() - 1)).getPartialDerivative(2)) >= eps);
    }

    public void visualize() {
        final int num = 1000;
        final double[] xData = Util.linspace(x.get(0), x.get(x.size() - 1), num);

        double[] yData = new double[num];
        for (int i = 0; i < num; ++i){
            yData[i] = value(xData[i]);
        }

        XYChart chart = QuickChart.getChart(
                "Интерполяция кубическим сплайном",
                "X",
                "Y",
                "y(x)",
                xData,
                yData
        );

        chart.getStyler().setXAxisDecimalPattern("#0.0");

        new SwingWrapper<>(chart).displayChart();
    }

    public List<Double> getC() {
        return c;
    }

    public List<Double> getA() {
        return a;
    }

    public List<Double> getB() {
        return b;
    }

    public List<Double> getD() {
        return d;
    }
}
