package io.slifer.csv;

import java.util.Map;

public class CsvRow {
    
    private Map<String, String> row;
    
    public CsvRow(Map<String, String> row) {
        this.row = row;
    }
    
    public String valueOf(String column) {
        return row.get(column);
    }
}
