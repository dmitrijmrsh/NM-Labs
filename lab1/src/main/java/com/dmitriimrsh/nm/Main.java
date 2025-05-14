package com.dmitriimrsh.nm;

import com.dmitriimrsh.nm.iterations.IterationsSolver;
import com.dmitriimrsh.nm.jacobi.JacobiSolver;
import com.dmitriimrsh.nm.lu.LUSolver;
import com.dmitriimrsh.nm.qr.QRSolver;
import com.dmitriimrsh.nm.seidel.SeidelSolver;
import com.dmitriimrsh.nm.threediag.ThreeDiagSolver;
import com.dmitriimrsh.nm.util.FilesUtil;
import com.dmitriimrsh.nm.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        System.out.println("Chose a task number: \n");

        System.out.print(
                String.join(
                        "\n",
                        "1 - LU decomposition",
                        "2 - run-through method",
                        "3 - iterations, Seidel",
                        "4 - Jacobi rotation method",
                        "5 - QR-decompose method"
                )
        );

        System.out.print("\n\nInput: ");

        int taskNumber = in.nextInt();

        System.out.print("\n");

        switch (taskNumber) {
            case 1 -> task1();
            case 2 -> task2();
            case 3 -> task3();
            case 4 -> task4();
            case 5 -> task5();
        }
    }

    private static void task1() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(
                new File(FilesUtil.task1InputFilePath)
        );

        double[][] matrix = JsonUtil.parseMatrixToSquared(rootNode.get(FilesUtil.matrixKeyName));
        double[] b = JsonUtil.parseVector(rootNode.get(FilesUtil.vectorKeyName));

        LUSolver luSolver = new LUSolver(matrix);

        System.out.println("L matrix:");
        luSolver.printL();

        System.out.print("\n");

        System.out.println("U matrix: ");
        luSolver.printU();

        System.out.print("\n");

        System.out.println("LUx = b solve:");
        double[] x = luSolver.solveEquation(b);
        for (int i = 0; i < x.length; ++i) {
            System.out.println("x%s = ".formatted(i) + String.format("%.4f", x[i]));
        }

        System.out.print("\n");

        System.out.println("Determinant: " + String.format("%.4f", luSolver.getDeterminant()));

        System.out.print("\n");

        System.out.println("Inverted matrix: ");
        double[][] invertedMatrix = luSolver.getInvertedMatrix();
        for (double[] row : invertedMatrix) {
            for (double number : row) {
                System.out.print(String.format("%.4f", number) + " ");
            }
            System.out.println();
        }

        System.out.print("\n");
    }

    private static void task2() throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(
                new File(FilesUtil.task2InputFilePath)
        );

        double[][] matrix = JsonUtil.parseMatrixToSquared(rootNode.get(FilesUtil.matrixKeyName));
        double[] d = JsonUtil.parseVector(rootNode.get(FilesUtil.vectorKeyName));

        ThreeDiagSolver threeDiagSolver = new ThreeDiagSolver(matrix, d);
        double[] x = threeDiagSolver.solve();

        for (int i = 0; i < x.length; ++i) {
            System.out.println("x%s = ".formatted(i + 1) + String.format("%.4f", x[i]));
        }
        System.out.println();
    }

    private static void task3() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(
                new File(FilesUtil.task3InputFilePath)
        );

        double[][] matrix = JsonUtil.parseMatrixToSquared(rootNode.get(FilesUtil.matrixKeyName));
        double[] b = JsonUtil.parseVector(rootNode.get(FilesUtil.vectorKeyName));
        double eps = JsonUtil.parseValue(rootNode.get(FilesUtil.epsilonKeyName));

        IterationsSolver iterationsSolver = new IterationsSolver(matrix, b, eps);
        Map.Entry<Integer, double[]> iterCountToAns = iterationsSolver.solve();

        System.out.println("Iterations method:\n");

        System.out.println("Iterations count: " + iterCountToAns.getKey());
        System.out.println("Apriori iterations count: " + iterationsSolver.generateAprioriEstimation());

        double[] x = iterCountToAns.getValue();

        for (int i = 0; i < x.length; ++i) {
            System.out.println("x%s = ".formatted(i + 1) + String.format("%.12f", x[i]));
        }

        System.out.print("\n");

        System.out.println("Seidel method:\n");

        SeidelSolver seidelSolver = new SeidelSolver(matrix, b, eps);
        iterCountToAns = seidelSolver.solve();

        System.out.println("Iterations count: " + iterCountToAns.getKey());
        System.out.println("Apriori iterations count: " + seidelSolver.generateAprioriEstimation());

        for (int i = 0; i < x.length; ++i) {
            System.out.println("x%s = ".formatted(i + 1) + String.format("%.12f", x[i]));
        }
    }

    private static void task4() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(
                new File(FilesUtil.task4InputFilePath)
        );

        double[][] matrix = JsonUtil.parseSquaredMatrix(rootNode.get(FilesUtil.matrixKeyName));
        double eps = JsonUtil.parseValue(rootNode.get(FilesUtil.epsilonKeyName));

        JacobiSolver jacobiSolver = new JacobiSolver(matrix, eps);
        jacobiSolver.solve();

        double[] eigenValues = jacobiSolver.getEigenValues();
        double[][] eigenMatrix = jacobiSolver.getEigenVectors();

        System.out.println("Eigen values:");

        for (int i = 0; i < eigenValues.length; ++i) {
            System.out.println("lambda%s = ".formatted(i + 1) + String.format("%.4f", eigenValues[i]));
        }

        System.out.print("\n");

        System.out.println("Eigen matrix:");

        for (double[] row : eigenMatrix) {
            for (double number : row) {
                System.out.print(String.format("%.4f", number) + " ");
            }
            System.out.println();
        }

        System.out.print("\n");

        System.out.println("Iter count: " + jacobiSolver.getIterCount());

        System.out.print("\n");

        if (jacobiSolver.check()) {
            System.out.println("Check success");
        } else {
            System.out.println("Check failure");
        }
    }

    private static void task5() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(
                new File(FilesUtil.task5InputFilePath)
        );

        double[][] matrix = JsonUtil.parseSquaredMatrix(rootNode.get(FilesUtil.matrixKeyName));
        double eps = JsonUtil.parseValue(rootNode.get(FilesUtil.epsilonKeyName));

        QRSolver qrSolver = new QRSolver(matrix, eps);
        qrSolver.solve();

        List<Complex> eigenValues = qrSolver.getEigenValues();

        System.out.println("Eigen values (my solution):");

        for (int i = 0; i < eigenValues.size(); ++i) {
            System.out.println("lambda" + i + " = "
                    + eigenValues.get(i).getReal()
                    + " + (" + eigenValues.get(i).getImaginary() + ") * i"
            );
        }

        System.out.println("Eigen values (Apache solution):");

        RealMatrix m = MatrixUtils.createRealMatrix(matrix);
        EigenDecomposition ev = new EigenDecomposition(m);

        double[] realEigenValue = ev.getRealEigenvalues();
        double[] imagEigenValue = ev.getImagEigenvalues();

        if (realEigenValue.length != imagEigenValue.length) {
            throw new RuntimeException("Bad calculation");
        }

        int size = realEigenValue.length;

        for (int i = 0; i < size; ++i) {
            System.out.println("lambda" + i + " = "
                    + realEigenValue[i]
                    + " + (" + imagEigenValue[i] + ") * i"
            );
        }
    }
}
