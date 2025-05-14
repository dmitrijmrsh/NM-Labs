package com.dmitriimrsh.nm.lab3;

import com.dmitriimrsh.nm.lab3.interpolation.Lagrange;
import com.dmitriimrsh.nm.lab3.interpolation.Newton;
import com.dmitriimrsh.nm.lab3.interpolation.Solver;
import com.dmitriimrsh.nm.lab3.interpolation.Spline;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Main {

    private static final List<Double> X_lagrange_test = List.of(0.1, 0.5, 0.9, 1.3);
    private static final Double X_star_lagrange_test = 0.8;
    private static final Function<Double, Double> y_lagrange_test = Math::log;

    private static final List<Double> X_newton_test = List.of(0., 1.0, 2.0, 3.0);
    private static final Double X_star_newton_test = 1.5;
    private static final Function<Double, Double> y_newton_test = x -> Math.sin(Math.PI / 6.0 * x);

    private static final List<Double> X_a = List.of(-3., -1., 1., 3.);
    private static final List<Double> X_b = List.of(-3., 0., 1., 3.);
    private static final Double X_star = -3.5;
    private static final Function<Double, Double> y = Math::atan;

    public static void main(String[] args) {
        System.out.print(
                """
                1 - Интерполяционные многочлены Лагранжа и Ньютона
                2 - Сплайн-интерполяция
                """
        );

        System.out.print("Введите номер задания: ");

        Scanner in = new Scanner(System.in);
        int number = in.nextInt();

        switch (number) {
            case 1 -> task1();
            case 2 -> task2();
            default -> System.out.println("Такого номера задания нет");
        }
    }

    private static void task1() {
        try {
            System.out.print("\n");
            System.out.println("Многочлен Лагранжа:");

            Solver lagrangeSolver = new Lagrange(y, X_a);

            lagrangeSolver.print();

            if (lagrangeSolver.check()) {
                System.out.println("Проверка выполнена успешно");
            } else {
                throw new RuntimeException("Проверка не пройдена");
            }

            System.out.printf("Значение многочлена в X*: %.5f\n", lagrangeSolver.value(X_star));
            System.out.printf("Значение функции в X*: %.5f\n", y.apply(X_star));
            System.out.printf("Абсолютная погрешность: %.5f\n", lagrangeSolver.absError(X_star));
        } catch (RuntimeException ex) {
            System.out.printf("Ошибка: %s", ex.getMessage());
        }

        try {
            System.out.print("\n");
            System.out.println("Многочлен Ньютона:");

            Solver newtonSolver = new Newton(y, X_b);

            newtonSolver.print();

            if (newtonSolver.check()) {
                System.out.println("Проверка выполнена успешно");
            } else {
                throw new RuntimeException("Проверка не пройдена");
            }

            System.out.printf("Значение многочлена в X*: %.5f\n", newtonSolver.value(X_star));
            System.out.printf("Значение функции в X*: %.5f\n", y.apply(X_star));
            System.out.printf("Абсолютная погрешность: %.5f\n", newtonSolver.absError(X_star));
        } catch (RuntimeException ex) {
            System.out.printf("Ошибка: %s", ex.getMessage());
        }
    }

    private static void task2() {
//        List<Double> x = List.of(0., 1., 2., 3., 4.);
//        List<Double> y = List.of(0., 1.8415, 2.9093, 3.1411, 3.2432);
//        double xStar = 1.5;

        List<Double> x = List.of(-3., -1., 1., 3., 5.);
        List<Double> y = List.of(-1.2490, -0.78540, 0.78540, 1.2490, 1.3734);
        double xStar = -0.5;

        System.out.print("\n");
        System.out.println("Сплайн-интерполяция:");

        Spline spline = new Spline(x, y);

        System.out.println("a size: " + spline.getA().size());
        System.out.println("b size: " + spline.getB().size());
        System.out.println("c size: " + spline.getC().size());
        System.out.println("d size: " + spline.getD().size());

        System.out.print("a: ");
        for (var a_i : spline.getA()) {
            System.out.printf(" %.5f |", a_i);
        }
        System.out.print("\n");

        System.out.print("b: ");
        for (var b_i : spline.getB()) {
            System.out.printf(" %.5f |", b_i);
        }
        System.out.print("\n");

        System.out.print("c: ");
        for (var c_i : spline.getC()) {
            System.out.printf(" %.5f |", c_i);
        }
        System.out.print("\n");

        System.out.print("d: ");
        for (var d_i : spline.getD()) {
            System.out.printf(" %.5f |", d_i);
        }
        System.out.print("\n");

        if (spline.check()) {
            System.out.println("Проверка выполнена успешно");
        } else {
            throw new RuntimeException();
        }

        System.out.printf("Значение (x = %.5f): %.5f", xStar, spline.value(xStar));
    }

}
