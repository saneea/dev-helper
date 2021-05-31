package io.github.saneea.dvh.utils.databuffer

import java.io.InputStream
import java.io.OutputStream

interface BinaryBuffer : AutoCloseable {
    val outputStream: OutputStream
    val inputStream: InputStream
}

private const val MiB = 1024 * 1024

fun createDefaultBuffer(): BinaryBuffer =
    SwitchableBinaryBuffer(
        ArrayBinaryBuffer(),
        FileBinaryBuffer(),
        bufferSize
    )

fun writeOnClose(
    createTargetStream: () -> OutputStream,
    createBuffer: () -> BinaryBuffer = ::createDefaultBuffer
): OutputStream {

    return object : OutputStream() {

        val buffer = createBuffer()

        override fun write(b: Int) = buffer.outputStream.write(b)

        override fun close() {
            buffer.use { buffer ->
                buffer.outputStream.close()
                createTargetStream().use { targetStream ->
                    buffer.inputStream.use { bufferInputStream ->
                        bufferInputStream.transferTo(targetStream)
                    }
                }
            }
        }
    }
}

private val bufferSize
    get() = System.getProperty("binBufferSize")
        ?.toLong()
        ?: 50L * MiB