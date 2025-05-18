package com.dmitriimrsh.nm.lab3;

import com.dmitriimrsh.nm.lab3.interpolation.Lagrange;
import com.dmitriimrsh.nm.lab3.interpolation.Newton;
import com.dmitriimrsh.nm.lab3.interpolation.Solver;
import com.dmitriimrsh.nm.lab3.interpolation.Spline;
import com.dmitriimrsh.nm.lab3.lsm.LSMSolver;
import com.dmitriimrsh.nm.lab3.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.print(
                """
                1 - Интерполяционные многочлены Лагранжа и Ньютона
                2 - Сплайн-интерполяция
                3 - Метод наименьших квадратов
                """
        );

        System.out.print("Введите номер задания: ");

        Scanner in = new Scanner(System.in);
        int number = in.nextInt();

        switch (number) {
            case 1 -> task1();
            case 2 -> task2();
            case 3 -> task3();
            default -> System.out.println("Такого номера задания нет");
        }
    }

    private static void task1() {
        try {
            System.out.print("\n");
            System.out.println("Многочлен Лагранжа:");

            Solver lagrangeSolver = new Lagrange(
                    Util.Lagrange.y,
                    Util.Lagrange.X_a
            );

            lagrangeSolver.print();

            if (lagrangeSolver.check()) {
                System.out.println("Проверка выполнена успешно");
            } else {
                throw new RuntimeException("Проверка не пройдена");
            }

            System.out.printf("Значение многочлена в X*: %.5f\n", lagrangeSolver.value(Util.Lagrange.X_star));
            System.out.printf("Значение функции в X*: %.5f\n", Util.Lagrange.y.apply(Util.Lagrange.X_star));
            System.out.printf("Абсолютная погрешность: %.5f\n", lagrangeSolver.absError(Util.Lagrange.X_star));

            lagrangeSolver.visualize();
        } catch (RuntimeException ex) {
            System.out.printf("Ошибка: %s", ex.getMessage());
        }

        try {
            System.out.print("\n");
            System.out.println("Многочлен Ньютона:");

            Solver newtonSolver = new Newton(Util.Newton.y, Util.Newton.X_b);

            newtonSolver.print();

            if (newtonSolver.check()) {
                System.out.println("Проверка выполнена успешно");
            } else {
                throw new RuntimeException("Проверка не пройдена");
            }

            System.out.printf("Значение многочлена в X*: %.5f\n", newtonSolver.value(Util.Newton.X_star));
            System.out.printf("Значение функции в X*: %.5f\n", Util.Newton.y.apply(Util.Newton.X_star));
            System.out.printf("Абсолютная погрешность: %.5f\n", newtonSolver.absError(Util.Newton.X_star));

            newtonSolver.visualize();
        } catch (RuntimeException ex) {
            System.out.printf("Ошибка: %s", ex.getMessage());
        }
    }

    private static void task2() {
        System.out.print("\n");
        System.out.println("Сплайн-интерполяция:");

        Spline spline = new Spline(Util.Spline.X, Util.Spline.Y);

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
            throw new RuntimeException("Проверка не пройдена");
        }

        System.out.printf(
                "Значение (x = %.5f): %.5f",
                Util.Spline.X_star,
                spline.value(Util.Spline.X_star)
        );

        spline.visualize();
    }

    private static void task3() {
        System.out.print("\n");
        System.out.println("Метод наименьших квадратов:");

        LSMSolver lsmSolver = new LSMSolver(
                Util.LSM.X,
                Util.LSM.Y
        );

        System.out.print("Приближающий многочлен 1-ой степени: ");

        List<Double> firstDegree = Arrays.stream(lsmSolver.getApproxPolynomial(1))
                        .boxed().toList();

        LSMSolver.print(firstDegree);

        System.out.printf(
                "Сумма квадратов ошибок (степень 1): %.4f\n",
                LSMSolver.getSumOfSquaredErrors(
                        firstDegree,
                        Util.LSM.Y,
                        Util.LSM.X
                )
        );

        LSMSolver.visualize(firstDegree, Util.LSM.Y, Util.LSM.X);

        System.out.print("\n");

        System.out.print("Приближающий многочлен 2-ой степени: ");

        List<Double> secondDegree = Arrays.stream(lsmSolver.getApproxPolynomial(2))
                .boxed().toList();

        LSMSolver.print(secondDegree);

        System.out.printf(
                "Сумма квадратов ошибок (степень 2): %.4f\n",
                LSMSolver.getSumOfSquaredErrors(
                        secondDegree,
                        Util.LSM.Y,
                        Util.LSM.X
                )
        );

        LSMSolver.visualize(secondDegree, Util.LSM.Y, Util.LSM.X);

        System.out.print("\n");

        System.out.print("Приближающий многочлен 3-ей степени: ");

        List<Double> thirdDegree = Arrays.stream(lsmSolver.getApproxPolynomial(3))
                .boxed().toList();

        LSMSolver.print(thirdDegree);

        System.out.printf(
                "Сумма квадратов ошибок (степень 3): %.4f\n",
                LSMSolver.getSumOfSquaredErrors(
                        thirdDegree,
                        Util.LSM.Y,
                        Util.LSM.X
                )
        );

        LSMSolver.visualize(thirdDegree, Util.LSM.Y, Util.LSM.X);
    }

}
