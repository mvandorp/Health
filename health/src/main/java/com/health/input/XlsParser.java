package com.health.input;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.health.Record;
import com.health.Table;

/**
 * Implements a parser for xls input files.
 *
 */
public final class XlsParser implements Parser {
  public String type = "xls";

  /**
   * Given a path to a xls file and an input descriptor, parses the input file into a {@link Table}.
   *
   * @param path
   *          the path of the input file.
   * @param config
   *          the input descriptor.
   * @return a table representing the parsed input file.
   * @throws IOException
   *           if any IO errors occur.
   * @throws InputException
   *           if any input errors occur.
   */

  @Override
  public Table parse(final String path, final InputDescriptor config) throws InputException,
      IOException {
    Objects.requireNonNull(path);
    Objects.requireNonNull(config);

    Table table = config.buildTable();
    Workbook wb;

    FileInputStream io = new FileInputStream(path);
    if (type == "xls") {
      wb = new XSSFWorkbook(io);
    } else {
      wb = new HSSFWorkbook(io);
    }

    StartCell startCell = config.getStartCell();
    int rowCount = 0;
    int columnsCount = config.getColumns().size();

    Sheet sheet = wb.getSheetAt(0);
    for (Row row : sheet) {
      // if at start row or beyond
      if (rowCount >= startCell.getStartRow()) {
        Record tableRow = new Record(table);

        int columnCountTableRow = 0;
        for (int i = startCell.getStartColumn() - 1; i < columnsCount + startCell.getStartColumn()
            - 1; i++) {
          switch (table.getColumn(columnCountTableRow).getType()) {
          case String:
            tableRow.setValue(columnCountTableRow, row.getCell(i).toString());
            break;
          case Number:
            tableRow.setValue(columnCountTableRow, Double.parseDouble(row.getCell(i).toString()));
            break;
          case Date:
            if (config.getDateFormat() != null) {
              try {
                DateTimeFormatter formatter = DateTimeFormatter

                .ofPattern(config.getDateFormat());
                LocalDate dateValue = LocalDate.parse(row.getCell(i).toString(), formatter);
                tableRow.setValue(columnCountTableRow, dateValue);
              } catch (DateTimeParseException e) {
                break;
              }
            }
            break;
          default:
            // The type was null, this should never happen
            assert false;
            throw new InputException("Internal error.", new Exception(
                "Column.getType() returned null."));
          }

          columnCountTableRow++;
        }

      }

      rowCount++;
    }

    wb.close();
    return table;

  }

  public String getType() {
    return type;
  }
}
