import java.io.File

fun convertAll(inputRoot: String, outputRoot: String, outputExtension: String, options: ConvertOptions) {
	val allDirs = File(inputRoot).listFiles()
	if (allDirs == null) {
		println("The input directory does not exist")
		return
	}

	File(outputRoot).mkdirs()

	for (dir in allDirs) {
		val files = dir.listFiles() ?: continue
		val dirName = dir.name
//		println(dirName)
		val outputDir = outputRoot + dirName
		File(outputDir).mkdir()
		for (file in files) {
			if (file.extension == "dbf") {
//				println(file.name)
				convert(
					inputPath = file.path,
					outputPath = "$outputDir/${file.nameWithoutExtension}$outputExtension",
					options = options
				)
			}
		}
	}
}

fun main(args: Array<String>) {
	convertAll("../input/", "../csv_with_names/", ".csv", ConvertOptions(
		separator = ',',
		includeNameRow = true,
		includeTypeRow = false,
		allowEscape = true
	))

	convertAll("../input/", "../csv_with_names_and_types/", ".csv", ConvertOptions(
		separator = ',',
		includeNameRow = true,
		includeTypeRow = true,
		allowEscape = true
	))

	convertAll("../input/", "../tsv_with_names/", ".tsv", ConvertOptions(
		separator = '\t',
		includeNameRow = true,
		includeTypeRow = false,
		allowEscape = false
	))

	convertAll("../input/", "../tsv_with_names_and_types/", ".tsv", ConvertOptions(
		separator = '\t',
		includeNameRow = true,
		includeTypeRow = true,
		allowEscape = false
	))

	convertAll("../input/", "../tsv_without_header/", ".tsv", ConvertOptions(
		separator = '\t',
		includeNameRow = false,
		includeTypeRow = false,
		allowEscape = false
	))
}
