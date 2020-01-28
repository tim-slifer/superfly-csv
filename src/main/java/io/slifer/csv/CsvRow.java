package io.slifer.csv;

import java.util.Map;

/**
 * Represents a single row of data on a CSV file.
 *
 * @author Tim Slifer
 */
public class CsvRow {
    
    private Map<String, String> row;
    
    public CsvRow(Map<String, String> row) {
        this.row = row;
    }
    
    /**
     * Retrieves the value of the given column from the row.
     *
     * @param column The name of the column.
     *
     * @return The value of the CSV segment.
     */
    public String valueOf(String column) {
        if (row.containsKey(column)) {
            return row.get(column);
        }
        else {
            throw new IllegalArgumentException("The column [" + column + "] does not exist.");
        }
    }
    
    /**
     * Retrieves all values from the row.
     *
     * @return An array.
     */
    public String[] values() {
        return row.values().toArray(new String[row.size()]);
    }
    
    /**
     * Retrieves the first column defined on the CSV for use with filtering and exclusions.
     *
     * @return The first column name.
     */
    public String firstColumnName() {
        return row.keySet().toArray(new String[row.size()])[0];
    }
}
