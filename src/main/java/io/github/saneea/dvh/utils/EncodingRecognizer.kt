package io.github.saneea.dvh.utils

import java.io.InputStream
import java.nio.charset.StandardCharsets

private val BOM_TREE = byteNodeFromMap(
    mapOf(
        intArrayOf(0xFE, 0xFF) to StandardCharsets.UTF_16BE,
        intArrayOf(0xFF, 0xFE) to StandardCharsets.UTF_16LE,
        intArrayOf(0xEF, 0xBB, 0xBF) to StandardCharsets.UTF_8
    )
)

fun encodingRecognizer(inBinStream: InputStream) = ByteSequenceRecognizer(inBinStream, BOM_TREE)
