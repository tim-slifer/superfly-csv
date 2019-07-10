/*
Copyright (c) 2019 Tim Slifer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */
package io.slifer.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * Virtualizes a CSV file for fast and simple use in referencing and retrieving values.
 *
 * @author Tim Slifer
 */
public class CsvFile {
    
    private String[] header;
    private List<String[]> rows;
    
    private int currentRow;
    
    public CsvFile(String[] header, List<String[]> rows) {
        this.header = header;
        this.rows = rows;
        
        currentRow = 0;
    }
    
    /**
     * Reduces the rows to those matching a specific value in the first column.
     *
     * @param filterBy The value by which matching rows will be retained.
     *
     * @return A self reference with the reduced data set.
     */
    public CsvFile filter(String filterBy) {
        doFilter(0, filterBy);
        
        return this;
    }
    
    /**
     * Reduces the rows to those matching a specific value in a given column.
     *
     * @param column The Column value to be used for the filter.
     * @param filterBy The value by which matching rows will be retained.
     *
     * @return A self reference with the reduced data set.
     */
    public CsvFile filter(String column, String filterBy) {
        int index = getColumnIndex(column);
        doFilter(index, filterBy);
        
        return this;
    }
    
    /**
     * Reduces the rows to those not matching a specific value in the first column.
     *
     * @param excludeBy The value by which matching rows will be discarded.
     *
     * @return A self reference with the reduced data set.
     */
    public CsvFile exclude(String excludeBy) {
        doExclude(0, excludeBy);
        
        return this;
    }
    
    /**
     * Reduces the rows to those not matching a specific value in a given column.
     *
     * @param column The Column value to be used for the exclusion.
     * @param excludeBy The value by which the matching rows will be discarded.
     *
     * @return A self reference with the reduced data set.
     */
    public CsvFile exclude(String column, String excludeBy) {
        int index = getColumnIndex(column);
        doExclude(index, excludeBy);
        
        return this;
    }
    
    /**
     * Retrieves the value of the given column from the current row.
     *
     * @param column The name of the column.
     *
     * @return The value of the CSV segment.
     */
    public String valueOf(String column) {
        int index = getColumnIndex(column);
        String[] row = rows.get(currentRow);
        
        return row[index];
    }
    
    /**
     * Retrieves a list of all row values in a given column.
     *
     * @param column The name of the column.
     *
     * @return A list of CSV values from the specified column.
     */
    public List<String> columnValues(String column) {
        int index = getColumnIndex(column);
        List<String> columnValues = new ArrayList<>();
        for (String[] row : rows) {
            columnValues.add(row[index]);
        }
        
        return columnValues;
    }
    
    /**
     * Retrieves all values from the current row.
     *
     * @return An array of values from the current row.
     */
    public String[] currentRowValues() {
        return rows.get(currentRow);
    }
    
    /**
     * Creates a clone of the current instance of the object, allowing "save points" between filter/exclude operations,
     * or multiple filter/exclude paths for a single file.
     *
     * @return A new instance of CsvFile, with the current Header and Row data.
     */
    public CsvFile clone() {
        return new CsvFile(header, rows);
    }
    
    /**
     * Changes focus to a specific row.
     *
     * @param row The new row to receive focus.
     *
     * @return A self reference.
     */
    public CsvFile setCurrentRow(int row) {
        checkRowBoundaries(row);
        this.currentRow = row;
        
        return this;
    }
    
    /**
     * Updates focus to the next row beneath the current row.
     *
     * @return A self reference.
     */
    public CsvFile setNextRow() {
        checkRowBoundaries(currentRow + 1);
        currentRow++;
        
        return this;
    }
    
    /**
     * Updates focus to the previous row above the current row.
     *
     * @return A self reference.
     */
    public CsvFile setPreviousRow() {
        checkRowBoundaries(currentRow - 1);
        currentRow--;
        
        return this;
    }
    
    /**
     * Indicates whether or not another row exists below the current row.
     *
     * @return True if a row exists, false otherwise.
     */
    public boolean hasNextRow() {
        return (currentRow < rows.size() - 1);
    }
    
    private void doFilter(int index, String filterBy) {
        List<String[]> filteredRows = new ArrayList<>();
        for (String[] row : rows) {
            if (row[index].equals(filterBy)) {
                filteredRows.add(row);
            }
        }
        rows = filteredRows;
        currentRow = 0;
    }
    
    private void doExclude(int index, String excludeBy) {
        List<String[]> excludedRows = new ArrayList<>();
        for (String[] row : rows) {
            if (!row[index].equals(excludeBy)) {
                excludedRows.add(row);
            }
            rows = excludedRows;
            currentRow = 0;
        }
    }
    
    private void checkRowBoundaries(int row) {
        if (row > (rows.size() - 1)) {
            throw new IndexOutOfBoundsException("New row index [" + row + "] exceeds the bounds of the CSV file.");
        }
        if ((row - 1) < 0) {
            throw new IndexOutOfBoundsException("New index cannot be less than zero.");
        }
    }
    
    private int getColumnIndex(String column) {
        for (int i = 0; i < header.length; i++) {
            if (header[i].equalsIgnoreCase(column)) {
                return i;
            }
        }
        throw new IllegalArgumentException("The column [" + column + "] does not exist.");
    }
}