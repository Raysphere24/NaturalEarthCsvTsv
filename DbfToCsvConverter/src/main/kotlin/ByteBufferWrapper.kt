import java.nio.ByteBuffer
import java.nio.ByteOrder

@JvmInline
value class ByteBufferWrapper(private val buffer: ByteBuffer) {
	fun readByte() = buffer.get().toInt().and(0xff)
	fun readChar() = readByte().toChar()
	fun readShort() = buffer.short.toInt()
	fun readInt() = buffer.int
	fun readDouble() = buffer.double

	fun readBytes(dst: ByteArray, offset: Int, length: Int): ByteBuffer =
		buffer.get(dst, offset, length)

	var order: ByteOrder
		get() = buffer.order()
		set(value) { buffer.order(value) }

	var position: Int
		get() = buffer.position()
		set(value) { buffer.position(value) }

	var limit: Int
		get() = buffer.limit()
		set(value) { buffer.limit(value) }

	val hasRemaining: Boolean
		get() = buffer.hasRemaining()
}
