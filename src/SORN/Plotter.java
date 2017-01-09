package SORN;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Stores necessary information over time and produces plots.
 *
 * Created by painkiller on 8/20/16.
 */
class Plotter extends ApplicationFrame {

  private final double simDuration;

  Plotter(final String title, double simDuration) {
    super(title);
    this.simDuration = simDuration;
  }

  void plotMultiple(ArrayList<YIntervalSeriesCollection> allSeries, String plotFilePath) {
    final NumberAxis domainAxis = new NumberAxis("Time");
    final ValueAxis rangeAxis = new NumberAxis("Firing Rate");

    if (allSeries.size() < 1) {
      throw new IllegalArgumentException("allSeries contains no series to plot.");
    }
    // Plot first series
    final XYErrorRenderer renderer0 = new XYErrorRenderer();
    renderer0.setSeriesStroke(0, new BasicStroke(0.1f));
    renderer0.setSeriesLinesVisible(0, false);
    renderer0.setSeriesShapesVisible(0, true);
    XYPlot plot = new XYPlot(allSeries.get(0), domainAxis, rangeAxis, renderer0);

    // Add other series, if there is more than one series in allSeries
    for (int i = 1; i < allSeries.size(); i++) {
      plot.setDataset(i, allSeries.get(i));
      final XYErrorRenderer renderer = new XYErrorRenderer();
      renderer.setSeriesStroke(0, new BasicStroke(1.0f));
      renderer.setSeriesLinesVisible(0, true);
      renderer.setSeriesShapesVisible(0, false);
      plot.setRenderer(i, renderer);

    }
    plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

    NumberAxis domain = (NumberAxis) plot.getDomainAxis();
    domain.setTickUnit(new NumberTickUnit(100.0));
    domain.setVerticalTickLabels(true);
    domain.setRange(0.0, simDuration);

    NumberAxis range = (NumberAxis) plot.getRangeAxis();
    range.setTickUnit(new NumberTickUnit(0.1));
//    range.setRange(-0.02, 1.02);
    range.setRange(0.0, simDuration);

    final JFreeChart chart = new JFreeChart("Fraction of Agents in Room over Time", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    final ChartPanel panel = new ChartPanel(chart, true, true, true, true, true);
    panel.setPreferredSize(new java.awt.Dimension(800, 600));
    setContentPane(panel);
    try { // Try to save chart
      ChartUtilities.saveChartAsPNG(new File(plotFilePath), chart, 600, 600);
      System.out.println("Saved plot to " + plotFilePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static YIntervalSeriesCollection averageTrials(ArrayList<XYSeries> results, String label) {
    if (results.size() == 0) {
      throw new IllegalArgumentException("No trials to average.");
    }

    YIntervalSeries averagedSeries = new YIntervalSeries(label);
    for (int i = 0; i < results.get(0).getItemCount(); i++) {

      // compute point-wise means
      double sum = 0.0;
      for (XYSeries series : results) {
        sum += (double) (series.getY(i));
      }
      double mean = sum / results.size();

      // compute point-wise standard deviations
      double sumSquaredDeviations = 0.0;
      for (XYSeries series : results) {
        sumSquaredDeviations += Math.pow(((double) series.getY(i)) - mean, 2.0);
      }
      double standardDeviation = Math.sqrt(sumSquaredDeviations / results.size());

      // compute point-wise confidence intervals (CI)
      double zScore95 = 1.96; // # of standard deviations from mean for two-sided 95% normal CI
      double CIRadius = standardDeviation * zScore95; // Math.sqrt(numSamples);
      averagedSeries.add((double) results.get(0).getX(i),
          mean,
          mean - CIRadius,  // lower 95% confidence bound
          mean + CIRadius); // upper 95% confidence bound
    }
    YIntervalSeriesCollection averagedSeriesAsCollection = new YIntervalSeriesCollection();
    averagedSeriesAsCollection.addSeries(averagedSeries);
    return averagedSeriesAsCollection;
  }


}
