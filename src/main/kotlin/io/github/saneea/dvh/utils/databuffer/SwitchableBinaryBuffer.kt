package io.github.saneea.dvh.utils.databuffer

import java.io.InputStream
import java.io.OutputStream

class SwitchableBinaryBuffer(
    mainBuffer: BinaryBuffer,
    private val reservedBuffer: BinaryBuffer,
    private val mainBufferLimit: Long
) : BinaryBuffer {

    private var mainBufferRef: BinaryBuffer? = mainBuffer

    private var currentBuffer = mainBuffer

    override val outputStream: OutputStream = SwitchableOutputStream()

    override val inputStream: InputStream
        get() = currentBuffer.inputStream

    private fun switchToReservedBuffer() {
        mainBufferRef?.also {
            it.outputStream.close()
            it.inputStream.use { mainBufferRefInputStream ->
                mainBufferRefInputStream.transferTo(reservedBuffer.outputStream)
            }
            currentBuffer = reservedBuffer
            it.close()
        }
        mainBufferRef = null
    }

    override fun close() = currentBuffer.close()

    private inner class SwitchableOutputStream : OutputStream() {

        private var bytesCount: Long = 0

        override fun write(b: Int) {
            if (bytesCount > mainBufferLimit) {
                switchToReservedBuffer()
            }
            currentBuffer.outputStream.write(b)
            bytesCount++
        }

        override fun close() {
            currentBuffer.outputStream.close()
        }
    }

}
