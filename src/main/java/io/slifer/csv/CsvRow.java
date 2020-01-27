package io.slifer.csv;

import java.util.Map;

public class CsvRow {
    
    private Map<String, String> row;
    
    public CsvRow(Map<String, String> row) {
        this.row = row;
    }
    
    public String valueOf(String column) {
        if (row.containsKey(column)) {
            return row.get(column);
        }
        else {
            throw new IllegalArgumentException("The column [" + column + "] does not exist.");
        }
    }
    
    public String firstKey() {
        return row.keySet().toArray(new String[row.size()])[0];
    }
    
    public String[] values() {
        return row.values().toArray(new String[row.size()]);
    }
}
