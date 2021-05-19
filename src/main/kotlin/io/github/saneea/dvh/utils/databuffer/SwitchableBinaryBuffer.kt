package io.github.saneea.dvh.utils.databuffer

import java.io.InputStream
import java.io.OutputStream

class SwitchableBinaryBuffer(
    private val mainBuffer: BinaryBuffer,
    private val reservedBuffer: BinaryBuffer,
    private val mainBufferLimit: Long
) : BinaryBuffer {

    private var currentBuffer = mainBuffer

    override val outputStream: OutputStream = SwitchableOutputStream()

    override val inputStream: InputStream
        get() = currentBuffer.inputStream

    private fun switchToReservedBuffer() {
        mainBuffer.inputStream.transferTo(reservedBuffer.outputStream)
        currentBuffer = reservedBuffer
        mainBuffer.close()
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

    }

}
