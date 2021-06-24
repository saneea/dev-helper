package io.github.saneea.dvh.utils.databuffer

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ArrayBinaryBuffer : BinaryBuffer {

    override val outputStream by lazy {
        ByteArrayOutputStream()
    }

    override val inputStream: InputStream
        get() = ByteArrayInputStream(outputStream.toByteArray())

    override fun close() = Unit
}