package com.health.operations;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.health.AggregateFunctions;
import com.health.Column;
import com.health.Record;
import com.health.Table;
import com.health.ValueType;
import com.health.output.Output;

/**
 * A class for all chunking operations.
 *
 * @author daan
 *
 */
public final class Chunk {

	/**
	 * now used to set the columnsName of the count columns should be changed
	 */
	private static final String countColumnNameTemplate = "count_";


	/**
	 * A function to chunk a dataSet by time.
	 *
	 * @param table
	 *            the Table to be chunked.
	 * @param column
	 *            the column on which to chunk should be a column of type Date.
	 * @param operations
	 *            a map of columns and their aggreagate operation.
	 * @param period
	 *            the period between chunk, could be days, months, years.
	 * @return a chunked Table.
	 */
	public static Table chunkByTime(Table table, final String column,
			final Map<String, AggregateFunctions> operations,
			final Period period) {
		String countColumnName = countColumnNameTemplate + column;
		Table chunkedTable = new Table(createChunkTableColumns(table, column,
				countColumnName));
		
		List<Column> chunkTableCols = chunkedTable.getColumns();

		LocalDate beginPer = getFirstDate(table, column);
		LocalDate lastDate = getLastDate(table, column);
		LocalDate endOfPer = LocalDate.MIN;

		while (!lastDate.isBefore(endOfPer)) {
			endOfPer = beginPer.plus(period);

			List<Record> chunk = makeTimeChunk(table, column, beginPer,
					endOfPer);
			if (!chunk.isEmpty()) {
			Record chunkedRecord = new Record(chunkedTable);
			
				for (int j = 0; j < chunkTableCols.size(); j++) {
					String columnName = chunkTableCols.get(j).getName();

					// if in operations do aggregate and set value
					if (operations != null
							&& operations.containsKey(columnName)) {
						double tmpValue = aggregate(chunk, columnName,
								operations.get(columnName));
						chunkedRecord.setValue(j, tmpValue);

					} else {
						switch (chunkTableCols.get(j).getType()) {
						case String:
							chunkedRecord.setValue(columnName, chunk.get(0)
									.getStringValue(columnName));
							break;
						case Number:
							if (columnName.equals(countColumnName)) {
								chunkedRecord.setValue(countColumnName,
										(double) chunk.size());
							} else {
								chunkedRecord.setValue(columnName, chunk.get(0)
										.getNumberValue(columnName));
							}
							break;
						case Date:
							chunkedRecord.setValue(columnName, chunk.get(0)
									.getDateValue(columnName));
							break;
						default:
							// error
						}
					}
				}
			}

			beginPer = endOfPer;
		}

		return chunkedTable;
	}

	public static String getCountcolumnnametemplate() {
		return countColumnNameTemplate;
	}

	/**
	 * chunks the data on same string, in the given column.
	 * 
	 * @param table
	 *            the table to be chunked.
	 * @param operations
	 *            a map of columns and their aggreagate operation.
	 * @param column
	 *            on which the data is chunked with the same string.
	 * @return a chunked Table.
	 */
	public static Table chunkByString(final Table table, final String column,
			final Map<String, AggregateFunctions> operations) {
		// make new list because of read only and addition of count

		String countColumnName = countColumnNameTemplate + column;
		List<Column> chunkedTableColumns = createChunkTableColumns(table,
				column, countColumnName);

		Table chunkedTable = new Table(chunkedTableColumns);

		ArrayList<String> found = new ArrayList<String>();
		List<Record> records = table.getRecords();
		List<Column> columns = table.getColumns();

		for (int i = 0; i < records.size(); i++) {
			String tmp = records.get(i).getStringValue(column);

			// if not allready found
			if (!found.contains(tmp)) {
				found.add(tmp);
				// list to aggregate
				List<Record> chunk = new ArrayList<Record>();
				for (int j = 0; j < records.size(); j++) {

					// if same value add record to list
					if (records.get(j).getStringValue(column).equals(tmp)) {
						chunk.add(records.get(j));
					}
				}

				Record chunkedRecord = new Record(chunkedTable);
				for (int k = 0; k < columns.size(); k++) {
					if (operations != null && operations.containsKey(columns.get(k).getName())) {
						double tmpValue = aggregate(chunk, columns.get(k)
								.getName(), operations.get(columns.get(k)
								.getName()));
						chunkedRecord.setValue(k, tmpValue);
					} else {
						switch (columns.get(k).getType()) {
						case String:
							chunkedRecord.setValue(k, records.get(i)
									.getStringValue(columns.get(k).getName()));
							break;
						case Number:
							chunkedRecord.setValue(k, records.get(i)
									.getNumberValue(columns.get(k).getName()));
							break;
						case Date:
							chunkedRecord.setValue(k, records.get(i)
									.getDateValue(columns.get(k).getName()));
							break;
						default:
							// error
						}
					}
					// set the count
					chunkedRecord.setValue(countColumnName,
							(double) chunk.size());
				}

			}
		}

		return chunkedTable;
	}

	private static double aggregate(final List<Record> chunk,
			final String column, final AggregateFunctions function) {
		double[] values = new double[chunk.size()];

		for (int i = 0; i < chunk.size(); i++) {
			values[i] = chunk.get(i).getNumberValue(column);
		}

		return Aggregator.aggregate(values, function);
	}

	private static List<Column> createChunkTableColumns(final Table table,
			final String column, final String countColumnName) {
		// make new list because of read only and addition of count
		List<Column> chunkedTableColumns = new ArrayList<Column>();

		Iterator<Column> it = table.getColumns().iterator();
		while (it.hasNext()) {
			chunkedTableColumns.add(it.next());
		}

		Column countColumn = new Column(countColumnName, table.getColumns()
				.size(), ValueType.Number);
		countColumn.setIsFrequencyColumn(true);
		chunkedTableColumns.add(countColumn);

		return chunkedTableColumns;
	}

	private static LocalDate getFirstDate(final Table table, final String column) {
		LocalDate res = LocalDate.MAX;
		List<Record> records = table.getRecords();
		Iterator<Record> it = records.iterator();

		while (it.hasNext()) {
			LocalDate tmp = it.next().getDateValue(column);
			if (tmp.isBefore(res)) {
				res = tmp;
			}
		}

		return res;
	}

	private static LocalDate getLastDate(final Table table, final String column) {
		LocalDate res = LocalDate.MIN;
		List<Record> records = table.getRecords();
		Iterator<Record> it = records.iterator();

		while (it.hasNext()) {
			LocalDate tmp = it.next().getDateValue(column);
			if (tmp.isAfter(res)) {
				res = tmp;
			}
		}

		return res;
	}

	private static List<Record> makeTimeChunk(final Table table,
			final String column, final LocalDate beginOfPer,
			final LocalDate endOfPer) {
		List<Record> chunk = new ArrayList<Record>();

		List<Record> records = table.getRecords();
		Iterator<Record> it = records.iterator();

		while (it.hasNext()) {
			Record tmpRec = it.next();
			LocalDate tmpDate = tmpRec.getDateValue(column);
			if ((tmpDate.isAfter(beginOfPer) || tmpDate.isEqual(beginOfPer))
					&& tmpDate.isBefore(endOfPer)) {
				chunk.add(tmpRec);
			}
		}
		return chunk;
	}
}
