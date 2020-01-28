# CSV-DATA

The Superfly-CSV project was born from a need to work with small data sets within an automated testing project.  Excel 
sheets were cumbersome to import due to less than ideal libraries available, and more importantly, are nearly 
impossible to work with effectively in a source control system such as Git.  CSV, on the other hand, provides the 
ability to organize data in a meaningful way, while not suffering the same challenges with source control and 
collaboration as Excel.  

# Usage

## Loading CSV Files

Loading a CSV file is as simple as calling the `CsvLoader` object, passing the path and name of the file as the 
argument.

```java
CsvFile csv = CsvLoader.load("testCsv.csv");
```

The path to the CSV file is relative to the classpath on the consuming project.  For Maven, this simply means the 
file would be stored in the `resources` folder.  For non-Maven projects, the directory storing CSV files would need 
to be added to the classpath.

For example, if the CSV file resides in a directory beneath the standard `resources` folder in a Maven project, the 
constructor would be expressed as follows:

```java
CsvFile csv = CsvLoader.load("test-data/data.csv");
```

### CSV File Structure

This tool employs two constraints on CSV formatting.  First, the first row is always assumed to be a header row.  
Failure to add headers will result in data being offset by one row, and values being interpreted as header 
information.  Second, a separator is required for each row.  Rows with no comma separator are ignored and will not be
parsed onto the `CsvFile` object.  For instances where only a single column is represented on a CSV file, each line 
must end with a separator.
 
### Leading and Trailing Spaces

Due to the way some tools may present CSV data to the user, there may be some extraneous leading and/or trailing 
spaces within segments of the CSV lines.  This tool provides some flexibility in handling this scenario.

The header row is always stripped of leading and trailing spaces.  The result of this is easier to use inputs 
when calling out specific column headers throughout the use of the `CsvFile` object.

Row data can be stripped (or not) at the time the CSV file is loaded, by including the `preserveSpaces` argument to 
the `CsvLoader`.

```java
CsvFile csv = CsvLoader.load("data.csv", true);
```

In the example above, the loader will refrain from stripping the CSV segments of any leading or trailing spaces.  
This method is overloaded, so the `preserveSpaces` option is only required if the CSV data is to be presented in it's
original state.  By not using the option, leading and trailing spaces will be removed by default.

## Managing CSV Data

There are currently two ways to reduce the data set to a more focused set of data, `filter()`, and `exclude()`.  

This example CSV file will be used in the descriptions below.

```csv
a,b,c
foo,bar,baz
foo,baz,bar
bar,foo,baz
bar,baz,foo
baz,foo,bar
baz,bar,foo
```

### Filtering Data

Filtering data will return only rows that match the given criteria in a column.  To accomplish this, the target 
column value, and the filter value must be given in the method call.

```java
CsvFile csv = CsvLoader.load("test.csv");
csv.filter("b", "foo");
```

... will reduce the example CSV above to:

```csv
a,b,c
bar,foo,baz
baz,foo,bar
```

CSV Data can be filtered repeatedly as long as rows continue to exist.  Adding to the example above...

```java
csv.filter("c", "bar");
```

... will reduce the data set to:

```csv
a,b,c
baz,foo,bar
```

Finally, a convenience method exists to automatically set the filter target to the first column.

- `.filter(String column, String filterBy)` will filter the data set based on the values in the given column.
- `.filter(String filterBy)` will filter data based on the values in the first column.

### Excluding Data

Excluding data will remove all the rows that match the given criteria in a column.  To accomplish this, the target 
column value, and the exclusion value must be given in the method call.

```java
CsvFile csv = CsvLoader.load("test.csv");
csv.exclude("c", "baz");
```

... will reduce the example CSV above to:

```csv
a,b,c
foo,baz,bar
bar,baz,foo
baz,foo,bar
baz,bar,foo
```

CSV Data can be excluded repeatedly as long as rows continue to exist.  Adding to the example above...

```java
csv.exclude("b", "foo");
```

... will reduce the data set to:

```csv
a,b,c
foo,baz,bar
bar,baz,foo
baz,bar,foo
```

Finally, a convenience method exists to automatically set the exclusion target to the first column.

- `.exclude(String column, String excludeBy)` will exclude data based on the values in the given column.
- `.exclude(String excludeBy)` will exclude data based on the values in the first column.

## Retrieving Data

### Navigating the CSV

By default, focus is given to the first row of the CSV data when a CSV file is loaded.  Filtering and excluding rows 
will also reset focus to the first row.  Focus is reassigned by one of several methods.

The `nextRow()` method will advance focus to the next row below the current row, if one exists.  If no next row 
exists, an exception is thrown.

The `previousRow()` method will change focus to the row above the current row, if one exists.  If no previous row 
exists, an exception is thrown.

The `setCurrentRow(Int row)` method will change focus directly to the given row.  A constraint check is performed to 
ensure the newly assigned row is within bounds of the rows on the current data set.  An exception is thrown if the 
new value is out of bounds.

### Single CSV Data Value

Single values can be retrieved from the row with focus be referencing the column header.  With the following CSV...

```csv
a,b,c
foo,bar,baz
```

... when `csv.valueOf("a")` is called, the value `foo` is returned.

### List of Values by Column

A list of values can be retrieved from all rows based on a referenced column header value.  With the following CSV...

```csv
a,b,c
foo,bar,baz
bar,baz,foo
baz,foo,bar
```

... when `csv.getColumnValues("a")` is called, the values `foo`, `bar`, and `baz` will be returned as a `List<String>`.

### List of Values by Row

A list of values can be retrieved from the current row.  With the following CSV...

```csv
a,b,c
baz,bar,foo
```

... when `csv.getCurrentRowValues()` is called, the values `baz`, `bar`, and `foo` will be returned as a `String[]`.

## Managing Instances

Multiple instances of a `CsvFile` can be created by using the `.clone()` method.  This method allows the current 
state of a CSV file to be replicated to a separate instance of the object.  Cloning a `CsvFile` instance will 
enable more sophisticated data filtering and exclusion, as well as creating a "save point" of sorts with a 
virtualized CSV file.

## Iterating over CSV rows

The recommended approach to iterating over rows on a CSV is to create an enhanced `for` loop on `getRows
()`, then performing any desired functions or calls to the `CsvRow` object.

```java
for (CsvRow row : csv.getRows()) {
    // add tasks to be completed for each currently stored row.
}
```

The `CsvFile` is not an iterator, and does not behave as such. Thus, `while` loops that conclude with calls to
`nextRow()` will result in the final row being skipped, or in the case of single-row CSV, the loop exiting without
running any of the iterative commands. The `getRows()` method does return an `ArrayList`, which in turn makes an
`Iterator` available, if one is required.
