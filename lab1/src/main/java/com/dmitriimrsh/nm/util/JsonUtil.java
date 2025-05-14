package com.dmitriimrsh.nm.util;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtil {

    //TODO: normal non-squared matrix parsing

    public static double[][] parseMatrixToSquared(JsonNode matrixNode) {
        int rows = matrixNode.size();
        int cols = 0;

        for (int i = 0; i < rows; ++i) {
            cols = Math.max(matrixNode.get(i).size(), cols);
        }

        double[][] matrix = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JsonNode value = matrixNode.get(i).get(j);
                if (value != null) {
                    matrix[i][j] = matrixNode.get(i).get(j).asDouble();
                    continue;
                }
                matrix[i][j] = 0d;
            }
        }

        return matrix;
    }

    public static double[][] parseSquaredMatrix(JsonNode matrixNode) {
        int rows = matrixNode.size();

        for (int i = 0; i < rows; ++i) {
            if (matrixNode.get(i).size() != rows)
                throw new RuntimeException("Matrix is not squared");
        }

        double[][] matrix = new double[rows][rows];

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < rows; ++j) {
                JsonNode value = matrixNode.get(i).get(j);

                if (value != null) {
                    matrix[i][j] = matrixNode.get(i).get(j).asDouble();
                    continue;
                }

                throw new RuntimeException("Matrix is not squared");
            }
        }

        return matrix;
    }

    public static double[] parseVector(JsonNode vectorNode) {
        int size = vectorNode.size();
        double[] vector = new double[size];

        for (int i = 0; i < size; i++) {
            vector[i] = vectorNode.get(i).asDouble();
        }

        return vector;
    }

    public static double parseValue(JsonNode valueNode) {
        return valueNode.asDouble();
    }

}
