package io.github.saneea.dvh.utils.databuffer

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path

class FileBinaryBuffer : BinaryBuffer {

    private val tempFileDelegate = lazy {
        Files.createTempFile("dvh-buffer", null)
            .also { it.toFile().deleteOnExit() }
    }

    private val tempFile: Path by tempFileDelegate

    override val outputStream: OutputStream by lazy {
        FileOutputStream(tempFile.toFile()).buffered()
    }

    override val inputStream: InputStream
        get() = FileInputStream(tempFile.toFile()).buffered()

    override fun close() {
        if (tempFileDelegate.isInitialized()) {
            Files.delete(tempFileDelegate.value)
        }
    }
}