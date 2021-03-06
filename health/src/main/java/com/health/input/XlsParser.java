package com.health.input;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
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

    /**
     * Given a path to a xls file and an input descriptor, parses the input file
     * into a {@link Table}.
     *
     * @param path
     *            the path of the input file.
     * @param config
     *            the input descriptor.
     * @return a table representing the parsed input file.
     * @throws IOException
     *             if any IO errors occur.
     * @throws InputException
     *             if any input errors occur.
     */

    @Override
    public Table parse(final String path, final InputDescriptor config)
            throws InputException, IOException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(config);

        Table table = config.buildTable();

        FileInputStream io = new FileInputStream(path);

        // String ext = getFileExtension(path);
        String ext = config.getFormat().toLowerCase();
        Workbook wb = getWorkBook(ext, io);

        StartCell startCell = config.getStartCell();
        int rowCount = 0;
        int columnsCount = config.getColumns().size();

        Sheet sheet = wb.getSheetAt(0);
        for (Row row : sheet) {
            // if at start row or beyond
            if (rowCount >= startCell.getStartRow()) {
                Record tableRow = new Record(table);

                int columnCountTableRow = 0;
                for (int i = startCell.getStartColumn() - 1; i < columnsCount
                        + startCell.getStartColumn() - 1; i++) {
                    String value;
                    try {
                        value = row.getCell(i).toString();
                    } catch (NullPointerException e) {
                        value = "NULL";
                    }

                    switch (table.getColumn(columnCountTableRow).getType()) {
                    case String:
                        if (!value.equals("NULL")) {
                            tableRow.setValue(columnCountTableRow, value);
                        }
                        break;
                    case Number:
                        if (!value.equals("NULL")) {
                            tableRow.setValue(columnCountTableRow,
                                    Double.parseDouble(value));
                        }
                        break;
                    case Date:
                        if (config.getDateFormat() != null) {
                            try {
                                fillDateCell(config, ext, row, tableRow,
                                        columnCountTableRow, i);
                            } catch (DateTimeParseException e) {
                                break;
                            }
                        }
                        break;
                    default:
                        // The type was null, this should never happen
                        assert false;
                        throw new InputException(
                                "Internal error.",
                                new Exception("Column.getType() returned null."));
                    }

                    columnCountTableRow++;
                }

            }

            rowCount++;
        }

        wb.close();
        return table;

    }

    private void fillCell(final InputDescriptor config, Table table,
            String ext, Row row, Record tableRow, int columnCountTableRow,
            int i, String value) throws InputException {
        switch (table.getColumn(columnCountTableRow).getType()) {
        case String:
            if (!value.equals("NULL")) {
                tableRow.setValue(columnCountTableRow, value);
            }
            break;
        case Number:
            if (!value.equals("NULL")) {
                tableRow.setValue(columnCountTableRow,
                        Double.parseDouble(value));
            }
            break;
        case Date:
            if (config.getDateFormat() != null) {
                try {
                    fillDateCell(config, ext, row, tableRow,
                            columnCountTableRow, i);
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
    }

    private void fillDateCell(final InputDescriptor config, String ext,
            final Row row, final Record tableRow, final int columnCountTableRow, final int i) {
        String format = config.getDateFormat();
        LocalDateTime dateValue;
        if (ext.equals("xlsx")) {
            Date date = row.getCell(i).getDateCellValue();
            if (date == null) {
                dateValue = null;
            } else {
                dateValue = date.toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            dateValue = LocalDateTime.parse(row.getCell(i).toString(),
                    formatter);
        }
        tableRow.setValue(columnCountTableRow, dateValue);
        // LocalDateTime time = dateValue.at;
        // time.toString();
    }

    private Workbook getWorkBook(final String ext, final FileInputStream io)
            throws IOException, InputException {
        Workbook wb = null;
        if (ext.equals("xls")) {
            wb = new HSSFWorkbook(io);
        } else if (ext.equals("xlsx")) {

            wb = new XSSFWorkbook(io);
        } else {
            io.close();
            throw new InputException(
                    "Not a xls or xlsx file, so cannot parse with XLSParser");

        }
        return wb;
    }
}