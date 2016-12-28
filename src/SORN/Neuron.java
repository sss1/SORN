package SORN;

import java.util.Random;

/**
 * An individual neuron in the SORN. Note that neurons are 'memoryless', in
 * that they only have information about their current state.
 *
 * TODO: Implement a "driving force" that can fix some inputs of some neurons.
 *
 * Author: sss1@andrew.cmu.edu
 */
class Neuron {

  // Global simulation constants (not static because they are granted by the simulation)
  private final int numNeurons;

  // Global neuron-related constants
  private static final double ETA_STDP = 0.004; // Excitatory STDP step size
  private static final double ETA_IP = 0.01; // Intrinsic plasticity step size
  private static final double WEIGHT_INITIAL_MAX = 1.0; // Max of uniform dist of initial weights
  private static final double SIGMA2_MIN = 0.01; // Min of uniform dist of sigma^2
  private static final double SIGMA2_MAX = 0.05; // Max of uniform dist of sigma^2
  // Max of uniform dist of initial firingThreshold
  private static final double FIRING_THRESHOLD_INITIAL_MAX = 1.0;
  private static final double TARGET_L1_NORM = 0.1; // Target L1 norm for synaptic normalization
  private static final double STRUCTURAL_CONNECTION_PROBABILITY = 0.1; // Prob. of new connections
  private static final double NEW_STRUCTURAL_CONNECTION_WEIGHT = 0.001; // Strength of new connections

  // Neuron-specific fixed parameters
  private final int ID; // Unique identifier for this neuron
  private final Random rand; // This neuron's internal source of randomness
  private final double sigma; // Standard deviation of this neuron's firing threshold
  private final double targetFiringRate;

  // Neuron-specific time-varying parameters
  private double[] weightsIn; // weights of each input connection
  private double firingThreshold;

  Neuron(int numNeurons, int ID) {
    this.numNeurons = numNeurons;
    this.ID = ID;
    weightsIn = new double[numNeurons];
    rand = new Random();
    sigma = Math.sqrt(SIGMA2_MIN + (SIGMA2_MAX - SIGMA2_MIN) * rand.nextDouble());
    targetFiringRate = 0.1; // This is how Zhang et al. use N(0.1, 0), for some reason

    for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
      // Uniformly random initial weights, except weightsIn[ID] == 0.0
      weightsIn[neuronIdx] = (neuronIdx == ID) ? 0.0 : WEIGHT_INITIAL_MAX * rand.nextDouble();
    }
    firingThreshold = FIRING_THRESHOLD_INITIAL_MAX * rand.nextDouble();
  }

  int getID() { return ID; }

  double getWeightIn(int idx) {
    return weightsIn[idx];
  }

  /**
   * @param fired array with fired[i] true if and only if neuron i fired in the previous time step
   * @return true if and only if the total weighted input surpasses the firing threshold
   */
  boolean shouldFire(boolean[] fired) {
    double weightedSum = 0.0;
    for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
      if (fired[neuronIdx]) {
        weightedSum += weightsIn[neuronIdx];
      }
    }
    return weightedSum + sigma * rand.nextGaussian() > firingThreshold;
  }

  /**
   * Updates this neuron's input weights according to excitatory STDP rules, using firing activity
   * from the current and previous time steps
   *
   * @param firedPreviously firedPreviously[i] true if and only if neuron i fired in the previous
   *                        time step
   * @param firedNow firedNow[i] true if and only if neuron i fired in the current time step
   */
  void excitatorySTDP(boolean[] firedPreviously, boolean[] firedNow) {
    for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
      if (neuronIdx == ID) { continue; } // No self-loops
      if (firedPreviously[neuronIdx] && firedNow[ID]) {
        weightsIn[neuronIdx] += ETA_STDP; // Additive increase
      }
      if (firedPreviously[ID] && firedNow[neuronIdx]) {
        // Additive decrease, with minimum value 0.0
        weightsIn[neuronIdx] = Math.max(weightsIn[neuronIdx] - ETA_STDP, 0.0);
      }
    }
  }

  /**
   * Normalizes the weight vector of the neuron to have L1 norm TARGET_L1_NORM.
   */
  void synapticNormalization() {
//    weightsIn = Util.normalizeWeightsInL1(weightsIn, TARGET_L1_NORM);
    Util.projectWeightsInL1(weightsIn, TARGET_L1_NORM);
  }

  /**
   * @param fired true if and only if this neuron fired this time step
   */
  void intrisicPlasticity(boolean fired) {
    if (fired) {
      firingThreshold += ETA_IP * (1.0 - targetFiringRate);
    } else {
      firingThreshold -= ETA_IP * targetFiringRate;
    }
  }

  /**
   * Randomly (independently, with identical probabilities) reconnects zero-weight edges, setting
   * the connection strength to a prescribed value
   */
  void structuralPlasticity() {
    for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
      if (neuronIdx == ID) { continue; } // No self-loops
      if (weightsIn[neuronIdx] < Double.MIN_VALUE) {
        weightsIn[neuronIdx] = (rand.nextDouble() > STRUCTURAL_CONNECTION_PROBABILITY) ? 0.0 : NEW_STRUCTURAL_CONNECTION_WEIGHT;
      }
    }
  }

  int numInputs() {
    int numInputs = 0;
    for (double weight : weightsIn) {
      numInputs += (weight > 0.0) ? 1 : 0;
    }
    return numInputs;
  }

}