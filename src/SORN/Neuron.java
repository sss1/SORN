package SORN;

/**
 * An individual neuron in the SORN. Note that neurons are 'memoryless', in
 * that they only have information about their current state.
 *
 * TODO: Implement a "driving force" that can fix some inputs of some neurons.
 */
public class Neuron {

  private int ID; // Unique identifier for this neuron
  private double[] weightsIn;

  public Neuron(int numNeurons) {
    weightsIn = new double[numNeurons];
    for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
      weightsIn[neuronIdx] = 0.0;
    }
  }

  public int getID() { return ID; }

  public double getWeightIn(int idx) {
    return weightsIn[idx];
  }

  public double[] normalizeWeightsL1(double[] weights, double total) {
    throw new IllegalArgumentException("This method has not been implemented yet.");
  }

}
