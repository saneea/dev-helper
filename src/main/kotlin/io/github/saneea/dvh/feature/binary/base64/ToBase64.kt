package io.github.saneea.dvh.feature.binary.base64

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class ToBase64 :
    Feature,
    Feature.In.Bin.Stream,
    Feature.Out.Bin.Stream {

    override lateinit var inBinStream: InputStream
    override lateinit var outBinStream: OutputStream

    override val meta = Meta("convert input binary sequence to Base64")

    override fun run() {
        Base64.getEncoder().wrap(outBinStream).use(inBinStream::transferTo)
    }
}