package com.health.operations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.health.Column;
import com.health.Record;
import com.health.Table;
import com.health.ValueType;

/**
 * A class to get a new column added to the table which has days.
 *
 */
public class TableWithDays {

  /**
   * A function to add days to a table as a column.
   *
   * @param table
   *          Gets the present table.
   * @return returns the table with extra column.
   */
  public static Table TableDays(final Table table) {
    Table origTable = table;
    List<Record> recList = origTable.getRecords();
    List<Column> colList = new ArrayList<Column>(origTable.getColumns());

    colList.add(new Column("day_of_week", colList.size(), ValueType.String));
    String dateColumn = table.getDateColumn().getName();

    Table result = new Table(colList);

    for (int i = 0; i < recList.size(); i++) {

      Record record = new Record(result);

      for (int j = 0; j < colList.size() - 1; j++) {

        String name = colList.get(j).getName();
        String newName = name;

        record.setValue(name, recList.get(i).getValue(newName));
      }
      record.setValue("day_of_week", dayOfWeek(recList.get(i).getDateValue(dateColumn)));
    }

    return result;
  }

  public static String dayOfWeek(LocalDate value) {
    return value.getDayOfWeek().toString();
  }
}