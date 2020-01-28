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
package io.slifer.csvdata.test;

import io.slifer.csv.CsvFile;
import io.slifer.csv.CsvLoader;
import io.slifer.csv.CsvRow;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tim Slifer
 */
public class CsvFileTest {
    
    private static final String FOO = "foo";
    private static final String BAR = "bar";
    private static final String BAZ = "baz";
    private static final String FOO_WTS = "foo  ";
    
    private static final String TEST_FILE = "test1.csv";
    private static final String TEST2_FILE = "test2.csv";
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Test
    public void testFilterFirstColumn() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        csv.filter(FOO);
        
        List<String> expected = Arrays.asList(FOO, FOO, FOO, FOO);
        
        Assert.assertEquals(expected, csv.columnValues("a"));
    }
    
    @Test
    public void testFilterFirstColumnWithColumnsInReverseAlphabeticalOrder() {
        CsvFile csv = CsvLoader.load(TEST2_FILE);
        csv.filter(FOO);
        
        List<String> expected = Arrays.asList(FOO, FOO, FOO, FOO);
        
        Assert.assertEquals(expected, csv.columnValues("c"));
    }
    
    @Test
    public void testFilterByColumn() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        csv.filter("b", FOO);
        
        List<String> expected = Arrays.asList(FOO, FOO);
        
        Assert.assertEquals(expected, csv.columnValues("b"));
    }
    
    @Test
    public void testExcludeFirstColumn() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        csv.exclude(FOO);
        
        List<String> expected = Arrays.asList(BAR, "", BAR, BAZ, BAZ);
        
        Assert.assertEquals(expected, csv.columnValues("a"));
    }
    
    @Test
    public void testExcludeByColumn() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        csv.exclude("b", FOO);
        
        List<String> expected = Arrays.asList(BAR, BAZ, "", "", BAZ, BAR, BAR);
        
        Assert.assertEquals(expected, csv.columnValues("b"));
    }
    
    @Test
    public void testSingleRowValue() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        
        Assert.assertEquals(FOO, csv.valueOf("a"));
    }
    
    @Test
    public void testSingleRowValueInvalidColumn() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        
        exception.expect(IllegalArgumentException.class);
        String value = csv.valueOf("d");
    }
    
    @Test
    public void testDefaultRow() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        
        String[] expected = {FOO, BAR, BAZ};
        
        Assert.assertArrayEquals(expected, csv.currentRowValues());
    }
    
    @Test
    public void tesSetNextRow() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        csv.nextRow();
        
        String[] expected = {FOO, BAZ, BAR};
        
        Assert.assertArrayEquals(expected, csv.currentRowValues());
    }
    
    @Test
    public void tesSetPreviousRow() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        csv.setCurrentRow(2);
        csv.previousRow();
        
        String[] expected = {FOO, BAZ, BAR};
        
        Assert.assertArrayEquals(expected, csv.currentRowValues());
    }
    
    @Test
    public void tesSetCurrentRow() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        csv.setCurrentRow(2);
        
        String[] expected = {BAR, FOO, BAZ};
        
        Assert.assertArrayEquals(expected, csv.currentRowValues());
    }
    
    @Test
    public void testSetNextRowInvalid() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        csv.setCurrentRow(8);
        
        exception.expect(IndexOutOfBoundsException.class);
        csv.nextRow();
    }
    
    @Test
    public void testSetPreviousRowInvalid() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        
        exception.expect(IndexOutOfBoundsException.class);
        csv.previousRow();
    }
    
    @Test
    public void testSetCurrentRowInvalid() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        
        exception.expect(IndexOutOfBoundsException.class);
        csv.setCurrentRow(9);
    }
    
    @Test
    public void testIgnoreInvalidRows() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        
        int rowCount = csv.columnValues("a").size();
        Assert.assertEquals(9, rowCount);
    }
    
    @Test
    public void testCloneCsv() {
        CsvFile csv1 = CsvLoader.load(TEST_FILE);
        csv1.filter(BAZ);
        List<String> csv1ColA = csv1.columnValues("a");
        
        CsvFile csv2 = csv1.clone();
        List<String> csv2ColA = csv2.columnValues("a");
        
        Assert.assertEquals(csv1ColA, csv2ColA);
    }
    
    @Test
    public void testPreserveTrailingSpaces() {
        CsvFile csv = CsvLoader.load(TEST_FILE, true);
        csv.filter(FOO_WTS);
        
        List<String> expected = Arrays.asList(FOO_WTS);
        
        Assert.assertEquals(expected, csv.columnValues("a"));
    }
    
    @Test
    public void testLoopingOverRows() {
        CsvFile csv = CsvLoader.load(TEST_FILE);
        csv.filter(FOO);
        
        List<String> expected = Arrays.asList(FOO, FOO, FOO, FOO);
        List<String> actual = new ArrayList<>();
        
        for (CsvRow row : csv.getRows()) {
            actual.add(row.valueOf("a"));
        }
        
        Assert.assertEquals(expected, actual);
    }
}
