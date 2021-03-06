import java.io.File
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.assertEquals

class ConvertOptions(val separator: Char, val includeNameRow: Boolean, val includeTypeRow: Boolean, val allowEscape: Boolean)

class InternalDbfColumn(val name: String, val type: Char, val length: Int)

fun convert(inputPath: String, outputPath: String, options: ConvertOptions) {
	val input = ByteBufferWrapper(
		ByteBuffer.wrap(File(inputPath).readBytes())
	)

	input.order = ByteOrder.LITTLE_ENDIAN

	val flags = input.readByte()
	assertEquals(flags, 3)

//	val lastUpdateDate = LocalDate.of(
//		input.readByte() + 1900,
//		input.readByte(),
//		input.readByte())
//	println("Last update date: $lastUpdateDate")
	input.position += 3

	val numRecords = input.readInt()
//	println("Number of records: $numRecords")

	val byteArray = ByteArray(256)
	val columns = ArrayList<InternalDbfColumn>()

	File(outputPath).writer().use { stream ->
		for (i in 1 .. 256) {
			input.position = 32 * i
			if (input.readByte() == 0x0d) break

			input.position = 32 * i
			input.readBytes(byteArray, 0, 11)
			val len = byteArray.indexOf(0)
			val fieldName = String(byteArray, 0, len, Charsets.UTF_8)
			val fieldType = input.readChar()
			input.position = 32 * i + 16
			val fieldLength = input.readByte()

			columns.add(InternalDbfColumn(fieldName, fieldType, fieldLength))
		}

		val sepStr = options.separator.toString()

		if (options.includeNameRow)
			stream.appendLine(columns.joinToString(sepStr) { it.name })

		if (options.includeTypeRow)
			stream.appendLine(columns.joinToString(sepStr) { it.type.toString() })

		for (i in 0 until numRecords) {
			val deletionFlag = input.readChar()
			assertEquals(deletionFlag, ' ') // deletion not supported

			stream.appendLine(columns.joinToString(sepStr) {
				input.readBytes(byteArray, 0, it.length)
				byteArray[it.length] = 0
				val size = byteArray.indexOf(0)
				var s = String(byteArray, 0, size, Charsets.UTF_8).trim()

				if (s == "Lop\rNur") s = "Lop Nur"

				if (s.contains("???????")) s = s.replace("???????", "???")

				fun transform(c: Char) = when (c) {
					'???' -> '\u0080'
					'???' -> '\u0082'
					'??' -> '\u0083'
					'???' -> '\u0084'
					'???' -> '\u0085'
					'???' -> '\u0086'
					'???' -> '\u0087'
					'??' -> '\u0088'
					'???' -> '\u0089'
					'??' -> '\u008a'
					'???' -> '\u008b'
					'??' -> '\u008c'
					'??' -> '\u008e'
					'???' -> '\u0091'
					'???' -> '\u0092'
					'???' -> '\u0093'
					'???' -> '\u0094'
					'???' -> '\u0095'
					'???' -> '\u0096'
					'???' -> '\u0097'
					'??' -> '\u0098'
					'???' -> '\u0099'
					'??' -> '\u009a'
					'???' -> '\u009b'
					'??' -> '\u009c'
					'??' -> '\u009e'
					'??' -> '\u009f'
					else -> c
				}

				if (s.zipWithNext().any { (a, b) -> a in '\u00c0' .. '\u00df' && transform(b) in '\u0080' .. '\u00bf' }) {
					if (s.any { transform(it) != it })
						s = s.map { transform(it) } .joinToString("")
					assertEquals(s.all { it <= '\u00ff' }, true)
					s = String(s.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
				}

				// seems in the Mac OS Roman encoding (CP10000)
				if (s.contains('\u009d')) s = s.replace('\u009d', '??')

				assertEquals(s.contains('\n'), false)
				assertEquals(s.contains('\r'), false)
				assertEquals(s.contains('???'), false)
				assertEquals(s.any { it.isISOControl() }, false)

				if (options.allowEscape) {
					if (s.contains('"')) '"' + s.replace("\"", "\"\"") + '"'
					else if (s.contains(options.separator)) '"' + s + '"'
					else s
				}
				else {
					if (s.contains(options.separator))
						throw RuntimeException("separator character found")
					else s
				}
			})
		}
	}
}
