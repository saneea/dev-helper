package io.github.saneea.dvh.utils.databuffer

import java.io.InputStream
import java.io.OutputStream

interface BinaryBuffer : AutoCloseable {
    val outputStream: OutputStream
    val inputStream: InputStream
}