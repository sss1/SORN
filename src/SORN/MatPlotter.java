package SORN;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLInt32;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class records agent positions at regular time increments for plotting later.
 * Eventually, it should also save these in .mat format.
 *
 * Created by sss1 on 7/27/16.
 */
class MatPlotter {

  private ArrayList<MLArray> variableList;

  MatPlotter() {
    variableList = new ArrayList<>();
  }

  private void add3DDoubleArray(String name, double[][][] array) {
    int[] dims = new int[3];
    dims[0] = array.length;
    dims[1] = (dims[0] == 0) ? 0 : array[0].length; // array[0].length only defined if dim1 > 0
    dims[2] = (dims[1] == 0) ? 0 : array[0][0].length; // array[0][0].length only defined if dim2 > 0
    double[] flattened = new double[dims[0] * dims[1] * dims[2]];
    int flattenedIdx = 0;
    for (int i = 0; i < dims[2]; i++) {
      for (int j = 0; j < dims[1]; j++) {
        for (int k = 0; k < dims[0]; k++) {
          flattened[flattenedIdx] = array[k][j][i];
          flattenedIdx++;
        }
      }
    }
    variableList.add(new MLDouble(name, flattened, 1)); // TODO: This line is exploding, for some reason!
    variableList.add(new MLInt32(name + "_dims", new int[][]{ dims }));
  }

  private static double[][] booleanMatrixToDoubleMatrix(boolean[][] matrix) {
    int height = matrix.length;
    int width = (height == 0) ? 0 : matrix[0].length;
    double[][] matrixAsDouble = new double[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        matrixAsDouble[i][j] = matrix[i][j] ? 1.0 : 0.0;
      }
    }
    return matrixAsDouble;
  }

  void writeToMAT(String filepath, boolean[][] fired, double[][][] weights, int numNeurons, int duration) {

    // reformat the positions to two numTimeSteps X numAgents double arrays, one each for x and y coordinates
    variableList.add(new MLDouble("fired", booleanMatrixToDoubleMatrix(fired)));
    add3DDoubleArray("weights", weights);

    variableList.add(new MLInt32("numNeurons", new int[]{numNeurons}, 1));
    variableList.add(new MLInt32("duration", new int[]{duration}, 1));

    try {
      new MatFileWriter(filepath, variableList);
      System.out.println("Saved simulation data to file: " + filepath);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}