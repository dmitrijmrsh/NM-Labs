package com.dmitriimrsh.nm.lab3.interpolation;

import com.dmitriimrsh.nm.lu.LUSolver;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;

public class Spline {

    private final List<Double> x;
    private final List<Double> y;
    private final int size;

    private List<Double> h;
    private List<Double> c;
    private List<Double> a;
    private List<Double> b;
    private List<Double> d;

    public Spline(final List<Double> x,
                  final List<Double> y) {
        this.x = x;
        this.y = y;

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
            d.add((c.get(i) - c.get(i - 1)) / 3 * h.get(i));
        }

        d.add(-c.get(size - 2) / (3 * h.get(size - 1)));
    }

    public double value(final double arg) {
        for (int i = 0; i < x.size(); ++i) {
            if (arg == x.get(i)) {
                return y.get(i);
            }
        }

        int index = 1;

        while (arg < x.get(index)) {
            ++index;
            if (index > size - 1) {
                throw new RuntimeException();
            }
        }

        return polynomialValue(index, arg);
    }

    private double polynomialValue(final int index,
                                   final double arg) {
        return a.get(index)
                + b.get(index) * (arg - x.get(index))
                + c.get(index) * FastMath.pow((arg - x.get(index)), 2)
                + d.get(index) * FastMath.pow((arg - x.get(index)), 3);
    }

    public boolean check() {
        for (int i = 0; i < x.size(); ++i) {
            if (!y.get(i).equals(value(x.get(i)))) {
                return false;
            }
        }

        return true;
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
