package SORN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * This class contains some basic mathematical utility functions used elsewhere.
 *
 * Author: sss1@andrew.cmu.edu
 */
class Util {

  /**
   * Returns the sum of the (double) numbers in the input array.
   * @param list numbers to be summed
   * @return sum of numbers in array
   */
  private static double sum(double[] list) {
    double sum = 0.0;
    for (double weight : list) {
      sum += weight;
    }
    return sum;
  }

  /**
   * Returns the sum of the (double) numbers in the input collection.
   * @param list numbers to be summed
   * @return sum of numbers in array
   */
  private static double sum(Collection<Double> list) {
    double sum = 0.0;
    for (double weight : list) {
      sum += weight;
    }
    return sum;
  }

  /**
   * Returns the mean of the (double) numbers in the input array.
   * @param array numbers to be averaged
   * @return mean of numbers in array
   */
  static double mean(Collection<Double> array) {
    return sum(array) / array.size();
  }

  /**
   * @param M rectangular matrix to transpose
   * @return transpose of M
   */
  static boolean[][] transpose(boolean[][] M) {
    if (M.length < 1 || M[0].length < 0) {
      throw new IllegalArgumentException("Input matrix must not be empty.");
    }
    int height = M.length;
    int width = M[0].length;
    boolean[][] M2 = new boolean[width][height];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        M2[j][i] = M[i][j];
      }
    }
    return M2;
  }

  /**
   * @param M rectangular matrix to transpose
   * @return transpose of M
   */
  static double[][] transpose(double[][] M) {
    if (M.length < 1 || M[0].length < 0) {
      throw new IllegalArgumentException("Input matrix must not be empty.");
    }
    int height = M.length;
    int width = M[0].length;
    double[][] M2 = new double[width][height];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        M2[j][i] = M[i][j];
      }
    }
    return M2;
  }

  static double fracPositive(Collection<Double> list) {
    int numPositive = 0;
    for (double x : list) {
      numPositive += (x > 0.0) ? 1 : 0;
    }
    return ((double) numPositive) / list.size();
  }

  static double fracPositive(double[] list) {
    int numPositive = 0;
    for (double x : list) {
      numPositive += (x > 0.0) ? 1 : 0;
    }
    return ((double) numPositive) / list.length;
  }

  static double fracTrue(boolean[] array) {
    int numTrue = 0;
    for (boolean bool : array) {
      numTrue += bool ? 1 : 0;
    }
    return ((double) numTrue) / array.length;
  }

  /**
   * Linearly normalize the input array onto the L1 ball of radius targetL1Norm >= 0.0
   */
  static double[] normalizeWeightsInL1(double[] array, double targetL1Norm) {
    double sum = Util.sum(array);
    for (int neuronIdx = 0; neuronIdx < array.length; neuronIdx++) {
      array[neuronIdx] = targetL1Norm * array[neuronIdx] / sum;
    }
    return array;
  }

  /**
   * (Nonlinearly) projects the input weight vector onto the L1 ball of radius TARGET_L1_NORM,
   * via soft-thresholding. This is a Java adaptation of John Duchi's Matlab code
   * (see http://stanford.edu/~jduchi/projects/DuchiShSiCh08.html).
   *
   * THIS IMPLEMENTATION ASSUMES THE INPUT ARRAY CONTAINS ONLY NON-NEGATIVE ELEMENTS!
   */
  static void projectWeightsInL1(double[] array, double targetL1Norm) {
    if (targetL1Norm < 0.0) {
      throw new IllegalArgumentException("Radius of L1 ball is negative: " + targetL1Norm);
    }
    if (sum(array) <= targetL1Norm) { return; } // L1 constraint already satisfied

    // Make a (descending) sorted copy of the input array
    ArrayList<Double> copy = new ArrayList<>();
    for (double x : array) { copy.add(x); }
    Collections.sort(copy);
    Collections.reverse(copy);

    // Find the number of components that will be non-zero (i.e., the 'sparsity') after projection
    int rho = 0; // sparsity after projection
    double cumSum = 0.0;
    while (copy.get(rho) > (cumSum + copy.get(rho) - targetL1Norm) / (rho + 1)) {
      // Note that this loop always enters at least once
      cumSum += copy.get(rho);
      rho++;
      if (rho >= array.length) break;
    }

    double theta = Math.max(0.0, (cumSum - targetL1Norm) / rho); // Soft threshold cut-off
    for (int i = 0; i < array.length; i++) {
      array[i] = Math.max(0.0, array[i] - theta);
    }
  }

}
