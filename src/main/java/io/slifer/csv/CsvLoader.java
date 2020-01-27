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

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The CsvLoader performs the sole task of reading a CSV file from the classpath and loading a CsvFile instance that
 * represents the contents of the file.
 *
 * @author Tim Slifer
 */
public class CsvLoader {
    
    /**
     * Loads the CSV file from resources and imports CSV content.
     *
     * @param fileName The path to and name of the CSV file to be loaded, relative to the classpath.
     * @param preserveSpaces Boolean indicating whether or not each value of each row, including the header, is to be
     * stripped of leading and trailing spaces.
     *
     * @return An instance of CsvFile containing the imported data.
     */
    public static CsvFile load(String fileName, boolean preserveSpaces) {
        return importCsvData(fileName, preserveSpaces);
    }
    
    /**
     * Loads the CSV file from resources and imports CSV content. Overloaded method omits the `toStrip` argument for
     * convenience, thus retaining the original CSV data.
     *
     * @param fileName The path to and name of the CSV file to be loaded, relative to the classpath.
     *
     * @return An instance of CsvFile containing the imported data.
     */
    public static CsvFile load(String fileName) {
        return importCsvData(fileName, false);
    }
    
    private static CsvFile importCsvData(String fileName, boolean preserveSpaces) {
        List<String> header;
        List<CsvRow> rows = new ArrayList<>();
        
        try {
            InputStream inputStream = CsvFile.class.getClassLoader().getResourceAsStream(fileName);
            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));
            List<String[]> csvRowData = csvReader.readAll();
            
            header = Arrays.asList(cleanRowValues(csvRowData.get(0)));
            
            for (int i = 1; i < csvRowData.size(); i++) {
                if (csvRowData.get(i).length != 1) {
                    Map<String, String> row = new HashMap<>();
                    
                    String[] preppedRowData = (preserveSpaces) ? csvRowData.get(i) : cleanRowValues(csvRowData.get(i));
                    List<String> finalRowData = Arrays.asList(preppedRowData);
                    for (int j = 0; j < header.size(); j++) {
                        row.put(header.get(j), finalRowData.get(j));
                    }
                    
                    rows.add(new CsvRow(row));
                }
            }
            csvReader.close();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error while loading file [" + fileName + "].");
        }
        
        return new CsvFile(rows);
    }
    
    private static String[] cleanRowValues(String[] row) {
        for (int i = 0; i < row.length; i++) {
            row[i] = row[i].strip();
        }
        
        return row;
    }
}
