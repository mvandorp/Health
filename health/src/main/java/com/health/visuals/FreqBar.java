package com.health.visuals;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import com.health.Column;
import com.health.Record;
import com.health.Table;
import com.health.ValueType;
import com.itextpdf.text.PageSize;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;

import de.erichseifert.vectorgraphics2d.PDFGraphics2D;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;

/**
 * Generates a Frequency Bar Diagram based on a Table object.
 *
 * @author Bjorn van der Laan &amp; Lizzy Scholten
 *
 */
public final class FreqBar {
	/**
	 * Private constructor to prevent instantiation.
	 */
	private FreqBar() {
		// Does nothing.
	}

	/**
	 * Generates a Frequency bar diagram. This variant has no column specified.
	 * It chooses the last date column and last frequency column in the Table
	 * object.
	 *
	 * @param table
	 *            Table to use
	 * @return Chart
	 */
	public static Chart frequencyBar(final Table table) {
		// Check if the Table contains a frequency and a date column
		Column freqColumn = null;
		Column dateColumn = null;
		for (Column c : table.getColumns()) {
			if (c.isFrequencyColumn()) {
				freqColumn = c;
			} else if (c.getType() == ValueType.Date) {
				dateColumn = c;
			}
		}
		// If both exist, format the frequency map based on these columns.
		if (freqColumn != null && dateColumn != null) {
			Map<String, Integer> freqMap = formatFrequencyMap(table,
					freqColumn.getName(), dateColumn.getName());
			return makeBarChart(freqMap, dateColumn.getName());
		} else {
			// Not good.
			throw new RuntimeException(
					"Table contains either no frequency column or no date column.");
		}
	}

	/**
	 * Generates a Frequency Bar diagram.
	 *
	 * @param table
	 *            Table to use
	 * @param column
	 *            Column to display frequency of
	 * @return Chart
	 */
	public static Chart frequencyBar(final Table table, final String column) {
		// Check if the Table contains a frequency column
		Column freqColumn = null;
		for (Column c : table.getColumns()) {
			if (c.isFrequencyColumn()) {
				freqColumn = c;
			}
		}
		// If the Table contains a frequency column, use it to format the
		// frequency map
		// Else if no frequency column exists, count occurrences of values in
		// the specified column
		if (freqColumn != null) {
			Map<String, Integer> freqMap = formatFrequencyMap(table,
					freqColumn.getName(), column);
			return makeBarChart(freqMap, column);
		} else {
			Map<String, Integer> freqMap = createFrequencyMap(table, column);
			return makeBarChart(freqMap, column);
		}
	}

	/**
	 * Creates a frequency map from the input Table to serve as input for
	 * makeBarChart.
	 *
	 * @param table
	 *            Table to use
	 * @param freqColumn
	 *            the frequency column
	 * @param column
	 *            the column
	 * @return frequency map
	 */
	private static Map<String, Integer> formatFrequencyMap(final Table table,
			final String freqColumn, final String column) {
		// Create map to save frequencies
		Map<String, Integer> freqMap = new HashMap<String, Integer>();

		for (Record r : table) {
			String value = r.getValue(column).toString();
			double frequency = (Double) r.getValue(freqColumn);
			freqMap.put(value, (int) frequency);
		}

		return freqMap;
	}

	/**
	 * Counts the occurrences of each value of column and creates a frequency
	 * map. Used when no the table contains no frequency column
	 *
	 * @param table
	 *            Table to use
	 * @param column
	 *            Column to count
	 * @return frequency map
	 */
	private static Map<String, Integer> createFrequencyMap(final Table table,
			final String column) {
		// Create map to save frequencies
		Map<String, Integer> freqMap = new HashMap<String, Integer>();

		for (Record r : table) {
			// Get value of record
			String value = r.getValue(column).toString();
			if (!freqMap.containsKey(value)) {
				freqMap.put(value, 1);
			} else {
				int currentFrequency = freqMap.get(value);
				freqMap.replace(value, ++currentFrequency);
			}
		}

		return freqMap;
	}

	/**
	 * Creates a frequency bar diagram based on the frequency map.
	 *
	 * @param freqMap
	 *            frequency map
	 * @param seriesName
	 *            name of the series
	 * @return chart
	 */
	private static Chart makeBarChart(final Map<String, Integer> freqMap,
			final String seriesName) {
		final int frameWidth = 800;
		final int frameHeight = 600;
		// Convert input data for processing
		ArrayList<String> labels = new ArrayList<String>(freqMap.keySet());
		ArrayList<Integer> frequency = new ArrayList<Integer>(freqMap.values());

		// Create Chart
		Chart chart = new ChartBuilder().chartType(ChartType.Bar)
				.width(frameWidth).height(frameHeight).title("Score Histogram")
				.xAxisTitle(seriesName).yAxisTitle("Frequency").build();

		chart.addSeries(seriesName, new ArrayList<String>(labels),
				new ArrayList<Integer>(frequency));

		// Customize Chart
		chart.getStyleManager().setLegendPosition(LegendPosition.InsideNW);

		return chart;
	}

	/**
	 * Container for chart.
	 *
	 * @param chart
	 *            chart
	 * @return Container
	 */
	public static Container getContainer(final Chart chart) {
		// Wrap the chart in a JFrame and hide the frame
		JFrame frame = new SwingWrapper(chart).displayChart();
		frame.addWindowListener(new HideWindowAdapter());

		return frame.getContentPane();
	}

	/**
	 * Save the chart as pdf.
	 *
	 * @param chart
	 *            chart that should be saved.
	 * @param fileName
	 *            file name under which the chart should be saved.
	 * @throws IOException
	 *             i/o exception
	 */
	public static void saveGraph(final Chart chart, final String fileName)
			throws IOException {
		final int width = (int) PageSize.A4.getWidth();
		final int height = (int) PageSize.A4.getHeight();

		VectorGraphics2D g = new PDFGraphics2D(0.0, 0.0, width, height);

		chart.paint(g, width, height);

		// Write the vector graphic output to a file
		FileOutputStream file = new FileOutputStream(fileName + ".pdf");

		try {
			file.write(g.getBytes());
		} finally {
			file.close();
		}
	}

	/**
	 * Hides window.
	 *
	 * @author Martijn
	 *
	 */
	private static class HideWindowAdapter extends WindowAdapter {
		@Override
		public void windowActivated(final WindowEvent e) {
			e.getWindow().setVisible(false);

		}
	}
}
