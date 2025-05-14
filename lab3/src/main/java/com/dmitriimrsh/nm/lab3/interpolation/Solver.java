package com.dmitriimrsh.nm.lab3.interpolation;

import java.util.List;
import java.util.function.Function;

public abstract class Solver {

    protected final Function<Double, Double> y;
    protected final List<Double> x;

    protected Solver(final Function<Double, Double> y,
                     final List<Double> x) {
        this.y = y;
        this.x = x;
    }

    abstract public double value(final double arg);

    public boolean check() {
        for (double value : x) {
            if (!y.apply(value).equals(value(value))) {
                return false;
            }
        }

        return true;
    }

    protected void checkArgValidity(final double arg) {
        if (arg < x.get(0) || arg > x.get(x.size() - 1)) {
            throw new RuntimeException(("Аргумент не находится в области определения интерполяции"));
        }
    }

    public double absError(final double arg) {
        return Math.abs(value(arg) - y.apply(arg));
    }

    abstract public void print();

}
