package com.dmitriimrsh.nm;

import com.dmitriimrsh.nm.iterations.IterationsLinearSolver;
import com.dmitriimrsh.nm.iterations.IterationsSystemSolver;
import com.dmitriimrsh.nm.newton.NewtonLinearSolver;
import com.dmitriimrsh.nm.newton.NewtonSystemSolver;

import java.util.Scanner;

public class Main {

    private static final double x0 = 0.9;
    private static final double a = 0.3;
    private static final double b = 1;
    private static final double eps = 0.00001;

    private static final double[] initialApprox = new double[] {0.5, 0.5};
    private static final double[] leftBorders = new double[] {0.1, 0.1};
    private static final double[] rightBorders = new double[] {2.1, 2.1};

    public static void main(String[] args) {
        System.out.print(
                        """
                        1 - Нелинейное уравнение
                        2 - Система нелинейных уравнений
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
        NewtonLinearSolver newtonLinearSolver = new NewtonLinearSolver(x0, a, b, eps);
        IterationsLinearSolver iterationsLinearSolver = new IterationsLinearSolver(x0, a, b, eps);

        System.out.println("Задание 1:\n");
        System.out.println("Метод простых итераций:");

        double iterationsAns = iterationsLinearSolver.solve();

        System.out.println("x = " + String.format("%.5f", iterationsAns));

        System.out.printf("Количество итераций: %s\n", iterationsLinearSolver.getIter());
        System.out.printf("q = %s\n", iterationsLinearSolver.getQ());

        if (iterationsLinearSolver.check(iterationsAns)) {
            System.out.println("Проверка выполнена успешно\n");
        } else {
            throw new RuntimeException();
        }

        System.out.println("Метод Ньютона:");

        double newtonAns = newtonLinearSolver.solve();

        System.out.println("x = " + String.format("%.5f", newtonAns));

        System.out.printf("Количество итераций: %s\n", newtonLinearSolver.getIter());

        if (newtonLinearSolver.check(newtonAns)) {
            System.out.println("Проверка выполнена успешно");
        } else {
            throw new RuntimeException();
        }
    }

    private static void task2() {
        NewtonSystemSolver newtonSystemSolver = new NewtonSystemSolver(initialApprox, eps);
        IterationsSystemSolver iterationsSystemSolver = new IterationsSystemSolver(
                initialApprox,
                leftBorders,
                rightBorders,
                eps
        );

        System.out.println("Задание 2:\n");
        System.out.println("Метод простых итераций:");

        double[] iterationsAns = iterationsSystemSolver.solve();
        for (int i = 0; i < iterationsAns.length; ++i) {
            System.out.println("x" + i + " = " + String.format("%.5f", iterationsAns[i]));
        }

        System.out.printf("Количество итераций: %s\n", iterationsSystemSolver.getIter());
        System.out.printf("q = %s\n", iterationsSystemSolver.getQ());

        if (iterationsSystemSolver.check(iterationsAns)) {
            System.out.println("Проверка выполнена успешно\n");
        } else {
            throw new RuntimeException();
        }

        System.out.println("Метод Ньютона:");

        double[] newtonAns = newtonSystemSolver.solve();
        for (int i = 0; i < newtonAns.length; ++i) {
            System.out.println("x" + i + " = " + String.format("%.5f", newtonAns[i]));
        }

        System.out.printf("Количество итераций: %s\n", newtonSystemSolver.getIter());

        if (newtonSystemSolver.check(newtonAns)) {
            System.out.println("Проверка выполнена успешно");
        } else {
            throw new RuntimeException();
        }
    }

}