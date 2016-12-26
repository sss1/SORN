package SORN;

/**
 * This class contains some basic mathematical utility functions used elsewhere.
 */
class Util {

  /**
   * Returns the sum of the (double) numbers in the input array.
   * @param array numbers to be summed
   * @return sum of numbers in array
   */
  private static double sum(double[] array) {
    double sum = 0.0;
    for (double weight : array) {
      sum += weight;
    }
    return sum;
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
   * via soft-thresholding
   */
  static double[] projectWeightsInL1(double[] array, double targetL1Norm) {
    throw new IllegalArgumentException("This method has not been implemented yet.");
  }

}
