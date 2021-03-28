package io.github.saneea.dvh.utils

import java.io.InputStream
import java.io.OutputStream

fun OutputStream.transferFrom(input: InputStream) =
    input.transferTo(this)
