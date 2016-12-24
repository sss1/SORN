package SORN;

import java.util.Random;

/**
 * This file is the backbone of the simulation and includes the main() method.
 */
public class Sim {

  private static final int numNeurons = 100;
  private static final int duration = 100;

  public static void main(String[] args) {

    // (for s < t), fired[s][i] is true if and only if neuron i fired at time s
    boolean[][] fired = new boolean[duration][numNeurons];

    // In the first time step, each neuron fires independently with probability 1/2
    Random rand = new Random();
    for (int i = 0; i < numNeurons; i++) {
      fired[0][i] = rand.nextBoolean();
    }

    for (int t = 1; t < duration; t++) {
      System.out.println(t); // TODO
    }

  }
}
