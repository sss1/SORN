package SORN;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import java.util.ArrayList;
import java.util.Random;

/**
 * This file is the backbone of the simulation, including the main() method.
 *
 * Author: sss1@andrew.cmu.edu
 */
public class Sim {

  private static final int numTrials = 10; // Number of IID trials to run
  private static final int numNeurons = 200; // Number of neurons
  private static final int duration = 1000; // Number of time steps to simulate

  // Parameters specifying where and what the simulation should output
  private static final String plotFilePath = "/home/painkiller/Desktop/SORN/basic_" + numNeurons + "neurons_" + duration + "timesteps.png";

  public static void main(String[] args) {

    Plotter plotter = new Plotter("Network density over time.", duration);
    ArrayList<YIntervalSeriesCollection> averagedResultsByExperiment = new ArrayList<>();

    // Run trials of particular experiment type
    String experimentLabel = "Basic";
    averagedResultsByExperiment.add(runTrials(experimentLabel));

    plotter.plotMultiple(averagedResultsByExperiment, plotFilePath);

  }

  private static YIntervalSeriesCollection runTrials(String label) {
    ArrayList<XYSeries> resultsByTrial = new ArrayList<>();

    for (int trialIdx = 0; trialIdx < numTrials; trialIdx++) {
      resultsByTrial.add(runTrial());
      System.out.println("Finished trial " + (trialIdx + 1) + "/" + numTrials + " of " + label + " experiment.");
    }
    return Plotter.averageTrials(resultsByTrial, label);
  }


  /**
   * Runs a single self-contained simulation and outputs some results of interest
   *
   * @return XYSeries each X-value is a time between 0.0 and simDuration and each Y-value is some
   *         quantity of interest
   */
  private static XYSeries runTrial() {

    // (for s < t), fired[s][i] is true if and only if neuron i fired at time s
    boolean[][] fired = new boolean[duration][numNeurons];
    Neuron[] neurons = new Neuron[numNeurons];

    Random rand = new Random();
    for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
      // Initialize each neuron
      neurons[neuronIdx] = new Neuron(numNeurons, neuronIdx);
      // In the first time step, each neuron fires independently with probability 1/2
      fired[0][neuronIdx] = rand.nextBoolean();
    }

    XYSeries output = new XYSeries("Stuff over time");

    for (int t = 0; t < duration - 1; t++) {
      for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
        fired[t + 1][neuronIdx] = neurons[neuronIdx].shouldFire(fired[t]); // Fire neuron
      }
      for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
        // Apply each update rule, based on current firing patterns
        neurons[neuronIdx].excitatorySTDP(fired[t], fired[t + 1]);
        neurons[neuronIdx].synapticNormalization();
        neurons[neuronIdx].intrisicPlasticity(fired[t + 1][neuronIdx]);
        neurons[neuronIdx].structuralPlasticity();
      }
      if (t > 3 && t % 20 == 0) { // First few time steps are anomalous
        output.add(t, Util.fracTrue(fired[t]));
      }
    }

//    double[] numInputs = new double[numNeurons];
//    for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
//      numInputs[neuronIdx] = neurons[neuronIdx].numInputs();
//    }
//    System.out.println("Average final number of inputs per neuron: " + Util.mean(numInputs));

    return output;

  }
}