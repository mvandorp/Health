package com.health.visuals;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.ApplicationFrame;

import com.health.Chunk;
import com.health.Column;
import com.health.Record;
import com.health.Table;

/**
 * Generates a Histogram based on a Table object.
 *
 * @author Lizzy Scholten
 *
 */
public final class Histogram extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
     * Private constructor to prevent instantiation.
     */
	private Histogram() {
		//Nothing happens
	}

	/**
	 * Creates a histogram.
	 *
	 * @param table
	 * 			table to use
	 * @param column
	 * 			column to use
	 * @param bin
	 * 			amount of bins histogram should be divided into
	 */
	public static void createHistogram(final Table table, final String column, final int bin) {
		final Dimension frameDimension = new Dimension(500, 500);
		final String xName = "Plotted column: " + column;
        final String yName = "";
        final double margin = 0.20;

		ApplicationFrame frame = new ApplicationFrame("Vidney");
		frame.setSize(frameDimension);

		HistogramDataset dataSet = new HistogramDataset();
		// Convert table to usable form of data
		double[] data = hist(table, column);

		// Calculate maximum value that appears in data
		// Calculate minimum value that appears in data
		double max = data[0];
		double min = data[0];
		for (int i = 0; i < data.length; i++) {
			if (data[i] > max) {
				max = data[i];
			}
			if (data[i] < min) {
				min = data[i];
			}
		}

		final CategoryAxis xAxis = new CategoryAxis(xName);
        xAxis.setLowerMargin(margin);
        xAxis.setUpperMargin(margin);
        final NumberAxis yAxis = new NumberAxis(yName);
        yAxis.setAutoRangeIncludesZero(false);

		dataSet.addSeries("Hist", data, bin, 0, max);

		final XYBarRenderer xybarrenderer = new XYBarRenderer(); //(XYBarRenderer)xyplot.getRenderer();
	    xybarrenderer.setShadowVisible(false);
	    xybarrenderer.setBarPainter(new StandardXYBarPainter());

	    // Create Chart
		JFreeChart chart = ChartFactory.createHistogram(
				column + " Frequency",
				"Event",
				"Frequency",
				dataSet,
				PlotOrientation.VERTICAL,
				true,
				false,
				false
			);

		final int backSet = 230;
		final float alpha = 0.7F;
		final int gridIn = 150;

		chart.setBackgroundPaint(new Color(backSet, backSet, backSet));
	    XYPlot xyplot = (XYPlot) chart.getPlot();
	    xyplot.setForegroundAlpha(alpha);
	    xyplot.setBackgroundPaint(Color.WHITE);
	    xyplot.setDomainGridlinePaint(new Color(gridIn, gridIn, gridIn));
	    xyplot.setRangeGridlinePaint(new Color(gridIn, gridIn, gridIn));

	    final ChartPanel chartPanel = new ChartPanel(chart);

        frame.setContentPane(chartPanel);
        frame.setVisible(true);
	}

	/**
	 * Converts Table object into array which can be used as input for the histogram.
	 *
	 * @param table
	 * 			table to use
	 * @param column
	 * 			column to use
	 * @return
	 * 			array with values that should be visualized in histogram
	 */
	private static double[] hist(final Table table, final String column) {
        // Get the Column to calculate frequency of
        Column selectedColumn = table.getColumn(column);
        String columnName = selectedColumn.getName();

        int val = 0;

        // Count items in table
        for (Chunk c : table) {
            for (@SuppressWarnings("unused") Record r : c) {
            	val = val + 1;
            }
        }

        double[] data = new double[val];
        int count = 0;

        // Put all the data from table into array
        for (Chunk c : table) {
            for (Record r : c) {
                // Get value of record
                Double value = (Double) r.getValue(columnName);
                data[count] = value;
                count = count + 1;
            }
        }
        return data;
    }
}




