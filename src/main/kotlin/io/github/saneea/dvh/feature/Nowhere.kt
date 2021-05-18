package io.github.saneea.dvh.feature

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import java.io.InputStream
import java.io.OutputStream

class Nowhere :
    Feature,
    Feature.In.Bin.Stream {

    override lateinit var inBinStream: InputStream

    override val meta = Meta("read all input data and do nothing")

    override fun run() {
        inBinStream.transferTo(OutputStream.nullOutputStream())
    }
}