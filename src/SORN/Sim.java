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
  private static final boolean makePlot = false;
  private static final boolean makeMATFile = true;
  private static final String plotFilePath = "/home/sss1/Desktop/SORN/basic_" + numNeurons + "neurons_" + duration + "timesteps.png";
  private static final String MATFileRoot = "/home/sss1/Desktop/SORN/resultsMemory" + Neuron.MEMORY;

  public static void main(String[] args) {

    Plotter plotter = new Plotter("Network density over time.", 0.05);
    ArrayList<YIntervalSeriesCollection> averagedResultsByExperiment = new ArrayList<>();

    // Run trials of particular experiment type
    String experimentLabel = "Basic";
    averagedResultsByExperiment.add(runTrials(experimentLabel));

    if (makePlot) {
      plotter.plotMultiple(averagedResultsByExperiment, plotFilePath);
    }

  }

  private static YIntervalSeriesCollection runTrials(String label) {
    ArrayList<XYSeries> resultsByTrial = new ArrayList<>();

    for (int trialIdx = 0; trialIdx < numTrials; trialIdx++) {
      System.out.println("Starting trial " + (trialIdx + 1) + "/" + numTrials + " of " + label + " experiment.");
      resultsByTrial.add(runTrial("Trial" + trialIdx));
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
  private static XYSeries runTrial(String trialLabel) {

    // for s < t, fired[s][i] is true if and only if neuron i fired at time s
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
    double[][][] weights = new double[duration][numNeurons][numNeurons];
    for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
      weights[0][neuronIdx] = neurons[neuronIdx].getWeightsIn(); // record initial weights
    }

    for (int t = 0; t < duration - 1; t++) {
      if (t % 500 == 0) System.out.println("t: " + t);
      for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
//        if (false) { // (neuronIdx < 0.1 * numNeurons) { // The first 10% of neurons always fire
//          fired[t + 1][neuronIdx] = true;
//        } else {
          fired[t + 1][neuronIdx] = neurons[neuronIdx].shouldFire(fired[t]); // Fire neuron
//        }
      }

      for (int neuronIdx = 0; neuronIdx < numNeurons; neuronIdx++) {
        // Apply each update rule, based on current and previous firing patterns
        neurons[neuronIdx].excitatorySTDP(fired, t + 1);
        neurons[neuronIdx].synapticNormalization();
        neurons[neuronIdx].intrisicPlasticity(fired[t + 1][neuronIdx]);
        neurons[neuronIdx].structuralPlasticity();
        for (int i = 0; i < numNeurons; i++) {
          weights[t + 1][neuronIdx][i] = neurons[neuronIdx].getWeightIn(i);
        }
      }

    }

    // Prints some basic diagnostic connectivity statistics from the simulation
//    int numZeroPairs = 0;
//    int numPartZeroPairs = 0;
//    int numTotalPairs = 0;
//    for (int destNeuronIdx = 0; destNeuronIdx < numNeurons; destNeuronIdx++) {
//      for (int sourceNeuronIdx = 0; sourceNeuronIdx < destNeuronIdx; sourceNeuronIdx++) {
//        output.add(neurons[destNeuronIdx].getWeightIn(sourceNeuronIdx), neurons[sourceNeuronIdx].getWeightIn(destNeuronIdx));
//        numTotalPairs++;
//        if (neurons[destNeuronIdx].getWeightIn(sourceNeuronIdx) < Double.MIN_VALUE || neurons[sourceNeuronIdx].getWeightIn(destNeuronIdx) < Double.MIN_VALUE) {
//          numPartZeroPairs++;
//        }
//        if (neurons[destNeuronIdx].getWeightIn(sourceNeuronIdx) < Double.MIN_VALUE && neurons[sourceNeuronIdx].getWeightIn(destNeuronIdx) < Double.MIN_VALUE) {
//          numZeroPairs++;
//        }
//      }
//    }
//    System.out.println("Fraction of edges that are zero in both directions: " + ((double) numZeroPairs) / numTotalPairs);
//    System.out.println("Fraction of pairs that are connected in at least one direction: " + ((double) (numTotalPairs - numZeroPairs)) / numTotalPairs);
//    System.out.println("Fraction of pairs that are zero in at least one direction: " + ((double) numPartZeroPairs) / numTotalPairs);
//    System.out.println("Fraction of pairs that are connected in both directions: " + ((double) (numTotalPairs - numPartZeroPairs)) / numTotalPairs);

    if (makeMATFile) { // output simulation results to .mat file
      String MATFilePath = MATFileRoot + trialLabel + ".mat";
      System.out.println("Writing MATLAB output to " + MATFilePath);
      MatPlotter matPlotter = new MatPlotter();
      matPlotter.writeToMAT(MATFilePath, fired, weights, numNeurons, duration);
    }

    return output;

  }
}