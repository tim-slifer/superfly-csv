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
import java.util.stream.Collectors;

/**
 * Virtualizes a CSV file for fast and simple use in referencing and retrieving values.
 *
 * @author Tim Slifer
 */
public class CsvFile {
    
    private List<CsvRow> rows;
    private int currentRowIndex;
    private CsvRow currentRow;
    
    public CsvFile(List<CsvRow> rows) {
        this.rows = rows;
        this.currentRowIndex = 0;
        this.currentRow = this.rows.get(0);
    }
    
    /**
     * Reduces the rows to those matching a specific value in the first column.
     *
     * @param filterBy The value by which matching rows will be retained.
     *
     * @return A self reference with the reduced data set.
     */
    public CsvFile filter(String filterBy) {
        doFilter(currentRow.firstColumnName(), filterBy);
        
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
        doFilter(column, filterBy);
        
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
        doExclude(currentRow.firstColumnName(), excludeBy);
        
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
        doExclude(column, excludeBy);
        
        return this;
    }
    
    /**
     * Retrieves the value of the given column from the current row.
     *
     * @param column The name of the column.
     *
     * @return The value in the given column.
     */
    public String valueOf(String column) {
        return currentRow.valueOf(column);
    }
    
    /**
     * Retrieves a list of all row values in a given column.
     *
     * @param column The name of the column.
     *
     * @return A list of CSV values from the specified column.
     */
    public List<String> columnValues(String column) {
        List<String> columnValues = new ArrayList<>();
        for (CsvRow row : rows) {
            columnValues.add(row.valueOf(column));
        }
        
        return columnValues;
    }
    
    /**
     * Retrieves all values from the current row.
     *
     * @return An array.
     */
    public String[] currentRowValues() {
        return currentRow.values();
    }
    
    /**
     * Creates a clone of the current instance of the object, allowing "save points" between filter/exclude operations,
     * or multiple filter/exclude paths for a single file.
     *
     * @return A new instance of CsvFile, with the current Row data.
     */
    public CsvFile clone() {
        return new CsvFile(rows);
    }
    
    /**
     * Changes focus to a specific row.
     *
     * @param row The new row to receive focus.
     */
    public void setCurrentRow(int row) {
        this.currentRowIndex = row;
        currentRow = rows.get(currentRowIndex);
    }
    
    /**
     * Updates focus to the next row beneath the current row.
     */
    public void nextRow() {
        currentRowIndex++;
        currentRow = rows.get(currentRowIndex);
    }
    
    /**
     * Updates focus to the previous row above the current row.
     */
    public void previousRow() {
        currentRowIndex--;
        currentRow = rows.get(currentRowIndex);
    }
    
    /**
     * @return The number of CSV rows currently stored.
     */
    public int length() {
        return rows.size();
    }
    
    /**
     * @return The list of currently stored CSV rows.
     */
    public List<CsvRow> getRows() {
        return rows;
    }
    
    private void doFilter(String column, String filterBy) {
        rows = rows.stream()
                   .filter(row -> row.valueOf(column).equals(filterBy))
                   .collect(Collectors.toList());
        currentRowIndex = 0;
    }
    
    private void doExclude(String column, String excludeBy) {
        rows = rows.stream()
                   .filter(row -> !row.valueOf(column).equals(excludeBy))
                   .collect(Collectors.toList());
        currentRowIndex = 0;
    }
}
