package io.github.saneea.dvh.utils.databuffer

import io.github.saneea.dvh.feature.BYTES_FULL_SET_FILE
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path

class BinaryBufferTest {

    @Test
    fun testBytesFullSetInMemoryBuffer() = testBytesFullSet(::ArrayBinaryBuffer)

    @Test
    fun testBytesFullSetFileBuffer() = testBytesFullSet(::FileBinaryBuffer)

    @Test
    fun testBytesFullSetSwitchableBufferMinusOne() = testBytesFullSetSwitchableBuffer(-1)

    @Test
    fun testBytesFullSetSwitchableBufferZero() = testBytesFullSetSwitchableBuffer(0)

    @Test
    fun testBytesFullSetSwitchableBufferOne() = testBytesFullSetSwitchableBuffer(1)

    @Test
    fun testBytesFullSetSwitchableBuffer255() = testBytesFullSetSwitchableBuffer(255)

    @Test
    fun testBytesFullSetSwitchableBuffer256() = testBytesFullSetSwitchableBuffer(256)

    @Test
    fun testBytesFullSetSwitchableBuffer257() = testBytesFullSetSwitchableBuffer(257)

    @Test
    fun testBytesFullSetSwitchableBuffer1000() = testBytesFullSetSwitchableBuffer(1000)

    private fun testBytesFullSetSwitchableBuffer(mainBufferLimit: Long) = testBytesFullSet {
        SwitchableBinaryBuffer(
            ArrayBinaryBuffer(),
            ArrayBinaryBuffer(),
            mainBufferLimit
        )
    }

    private fun testBytesFullSet(createBuffer: () -> BinaryBuffer) {
        testBuffer(createBuffer, BYTES_FULL_SET_FILE)
    }

    private fun testBuffer(createBuffer: () -> BinaryBuffer, binDataFile: Path) {
        createBuffer().use { buffer ->

            BufferedInputStream(Files.newInputStream(binDataFile)).use { bytesFullSetStream ->
                buffer.outputStream.use { bufferOutputStream ->
                    bytesFullSetStream.transferTo(bufferOutputStream)
                }
            }

            val readFromBufferStream = ByteArrayOutputStream()

            readFromBufferStream.use {
                buffer.inputStream.use { bufferInputStream ->
                    bufferInputStream.transferTo(readFromBufferStream)
                }
            }

            val actual = readFromBufferStream.toByteArray()
            val expected = Files.readAllBytes(binDataFile)

            assertArrayEquals(expected, actual)
        }
    }
}