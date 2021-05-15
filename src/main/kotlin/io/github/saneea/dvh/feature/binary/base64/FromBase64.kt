package io.github.saneea.dvh.feature.binary.base64

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class FromBase64 :
    Feature,
    Feature.In.Bin.Stream,
    Feature.Out.Bin.Stream {

    override lateinit var inBinStream: InputStream
    override lateinit var outBinStream: OutputStream

    override fun meta(context: FeatureContext) = Meta("convert input Base64 sequence to binary")

    override fun run(context: FeatureContext) {
        Base64.getDecoder().wrap(inBinStream).use { it.transferTo(outBinStream) }
    }
}