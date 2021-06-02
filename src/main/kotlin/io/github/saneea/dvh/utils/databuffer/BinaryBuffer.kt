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

class WriteOnCloseOutputStream(
    private val createTargetStream: () -> OutputStream,
    createBuffer: () -> BinaryBuffer = ::createDefaultBuffer
) : OutputStream() {
    private val buffer = createBuffer()

    private var cancelled = false

    fun cancel() {
        cancelled = true
    }

    override fun write(b: Int) {
        if (!cancelled) {
            buffer.outputStream.write(b)
        }
    }

    override fun close() {
        buffer.use { buffer ->
            buffer.outputStream.close()
            if (!cancelled) {
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