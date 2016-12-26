package SORN;

import java.util.Random;

/**
 * This file is the backbone of the simulation and includes the main() method.
 * Currently, only excitatory neurons are supported.
 */
public class Sim {

  private static final int numNeurons = 200; // Number of neurons
  private static final int duration = 100; // Number of time steps to simulate

  private static Neuron[] neurons; // All neurons, indexed by ID

  public static void main(String[] args) {

    // (for s < t), fired[s][i] is true if and only if neuron i fired at time s
    boolean[][] fired = new boolean[duration][numNeurons];
    neurons = new Neuron[numNeurons];

    Random rand = new Random();
    for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
      // Initialize each neuron
      neurons[neuronIdx] = new Neuron(numNeurons, neuronIdx);
      // In the first time step, each neuron fires independently with probability 1/2
      fired[0][neuronIdx] = rand.nextBoolean();
    }

    for (int t = 0; t < duration - 1; t++) {
      for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
        fired[t + 1][neuronIdx] = neurons[neuronIdx].shouldFire(fired[t]); // Fire neurons
      }
      for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
        neurons[neuronIdx].excitatorySTDP(fired[t], fired[t + 1]);
        neurons[neuronIdx].synapticNormalization();
        neurons[neuronIdx].intrisicPlasticity(fired[t + 1][neuronIdx]);
        neurons[neuronIdx].structuralPlasticity();
      }
    }

  }
}
