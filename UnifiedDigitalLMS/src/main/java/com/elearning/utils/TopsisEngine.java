/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: TopsisEngine
// RUBRIC FOCUS: CO3 (Data Structures - 2D Arrays & Sorting)
//
// Q&A DEFENSE:
// "This handles complex Math. I used standard 1D arrays for vectors (distBest, 
// distWorst) and 2D arrays (double[][]) for the decision matrix. 
// I utilized java.util.Arrays.sort with a custom comparator lambda to sort 
// the calculated closeness scores dynamically."
// ==============================================================================
package com.elearning.utils;

public class TopsisEngine {

    public static class Result implements Comparable<Result> {
        public final String alternativeName;
        public final double score;           
        public final int    rank;            

        public Result(String alternativeName, double score, int rank) {
            this.alternativeName = alternativeName;
            this.score           = score;
            this.rank            = rank;
        }

        @Override
        public int compareTo(Result other) {
            return Integer.compare(this.rank, other.rank);
        }

        @Override
        public String toString() {
            return rank + ". " + alternativeName + "  score=" + String.format("%.4f", score);
        }
    }

    public static Result[] rank(String[] names, double[][] matrix, double[] weights, boolean[] isBenefit) {
        int m = matrix.length;      
        int n = matrix[0].length;   

        double[][] normalized = new double[m][n];
        for (int j = 0; j < n; j++) {
            double sumSq = 0.0;
            for (int i = 0; i < m; i++) sumSq += Math.pow(matrix[i][j], 2);
            double denom = Math.sqrt(sumSq);
            for (int i = 0; i < m; i++)
                normalized[i][j] = (denom == 0) ? 0 : matrix[i][j] / denom;
        }

        double[][] weighted = new double[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                weighted[i][j] = normalized[i][j] * weights[j];

        double[] idealBest  = new double[n];
        double[] idealWorst = new double[n];
        for (int j = 0; j < n; j++) {
            idealBest[j]  = weighted[0][j];
            idealWorst[j] = weighted[0][j];
            for (int i = 1; i < m; i++) {
                if (isBenefit[j]) {
                    idealBest[j]  = Math.max(idealBest[j],  weighted[i][j]);
                    idealWorst[j] = Math.min(idealWorst[j], weighted[i][j]);
                } else {
                    idealBest[j]  = Math.min(idealBest[j],  weighted[i][j]);
                    idealWorst[j] = Math.max(idealWorst[j], weighted[i][j]);
                }
            }
        }

        double[] distBest  = new double[m];
        double[] distWorst = new double[m];
        for (int i = 0; i < m; i++) {
            double sumB = 0.0, sumW = 0.0;
            for (int j = 0; j < n; j++) {
                sumB += Math.pow(weighted[i][j] - idealBest[j],  2);
                sumW += Math.pow(weighted[i][j] - idealWorst[j], 2);
            }
            distBest[i]  = Math.sqrt(sumB);
            distWorst[i] = Math.sqrt(sumW);
        }

        double[] scores = new double[m];
        for (int i = 0; i < m; i++) {
            double denom = distBest[i] + distWorst[i];
            scores[i] = (denom == 0) ? 0 : distWorst[i] / denom;
        }

        Integer[] idx = new Integer[m];
        for (int i = 0; i < m; i++) idx[i] = i;
        java.util.Arrays.sort(idx, (a, b) -> Double.compare(scores[b], scores[a]));

        Result[] results = new Result[m];
        for (int r = 0; r < m; r++)
            results[r] = new Result(names[idx[r]], scores[idx[r]], r + 1);

        return results;
    }

    public static Result[] rankEqualWeights(String[] names, double[][] matrix, boolean[] isBenefit) {
        int n = matrix[0].length;
        double[] weights = new double[n];
        java.util.Arrays.fill(weights, 1.0 / n);
        return rank(names, matrix, weights, isBenefit);
    }
}