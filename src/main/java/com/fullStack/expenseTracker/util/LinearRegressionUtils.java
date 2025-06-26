package com.fullStack.expenseTracker.util;

public class LinearRegressionUtils {
    public static double predictNext(double[] y) {
        int n = y.length;
        double[] x = new double[n];
        for (int i = 0; i < n; i++) x[i] = i;

        double xSum = 0, ySum = 0, xySum = 0, x2Sum = 0;

        for (int i = 0; i < n; i++) {
            xSum += x[i];
            ySum += y[i];
            xySum += x[i] * y[i];
            x2Sum += x[i] * x[i];
        }

        double slope = (n * xySum - xSum * ySum) / (n * x2Sum - xSum * xSum);
        double intercept = (ySum - slope * xSum) / n;

        return slope * n + intercept;
    }
}
