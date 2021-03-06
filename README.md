This repository consists of CSV and TSV files converted from the DBF files in the [Natural Earth dataset](https://www.naturalearthdata.com), and a tool to convert them.

# Converted files
Considering various use-cases, we provide following five formats of the converted files, corresponding to each directory.

* `csv_with_names`: comma-separated, with field names
	```
	featurecla,scalerank,min_zoom
	Land,1,1.0
	Land,1,1.0
	Land,1,0.0
	```
* `csv_with_names_and_types`: comma-separated, with field names and types
	```
	featurecla,scalerank,min_zoom
	C,N,N
	Land,1,1.0
	Land,1,1.0
	Land,1,0.0
	```
* `tsv_with_names`: tab-separated, with field names
	```
	featurecla	scalerank	min_zoom
	Land	1	1.0
	Land	1	1.0
	Land	1	0.0
	```
* `tsv_with_names_and_types`: tab-separated, with field names and types
	```
	featurecla	scalerank	min_zoom
	C	N	N
	Land	1	1.0
	Land	1	1.0
	Land	1	0.0
	```
* `tsv_without_header`: tab-separated, without field names or types
	```
	Land	1	1.0
	Land	1	1.0
	Land	1	0.0
	```

All files are encoded in UTF-8.

If you want to use these files in Pandas or like that, files under `csv_with_names` are convenient. Also, on Mac OS, they can be seen by Quicklook.

If you are writing your own parser, reading the TSV files is rather straightforward: for each string `s` representing a row, `s.split('\t')` will suffice. On the other hand, in the CSV files, any field containing a comma (`,`) or a double quotation (`"`) is escaped by enclosing the entire field by `""` and replacing each `"` with `""`, thus if you want to use the CSV files, you need a sufficiently smart parser that can handle escaping.

Each file in `csv_with_names_and_types` and `tsv_with_names_and_types` contains a row indicating field types. For each column, its field type is expressed as a character:

* `C` indicates each field of the column is a string,
* `N` indicates each field of the column is a number (either integral or floating-point).

# Converter
The `DbfToCsvConverter` folder contains the Intellij project to perform the conversion in Kotlin. To use this, a directory named `input` must be created in the root of the repository, and it should contains directories `10m_cultural`, `10m_physical`, `50m_cultural`, `50m_physical`, `110m_cultural` and/or `110m_physical` which you want to convert, provided from the Natural Earth dataset. You can configure the format of generated CSV/TSV files by the `ConvertOptions` class:

* `separator: Char` - the character which separates each field,
* `includeNameRow: Boolean` - whether the field name must be included as a row,
* `includeTypeRow: Boolean` - whether the field type must be included as a row,
* `allowEscape: Boolean` - whether escape should be done if necessary.

If `allowEscape` is set to `false` and at least one field contains the `separator` character, the program fails. Please use this when you want to ensure that the output files can be read using `s.split(separator)`.
