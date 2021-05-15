package io.github.saneea.dvh.feature.binary.gzip

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.utils.transferFrom
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream

class FromGzip :
    Feature,
    Feature.In.Bin.Stream,
    Feature.Out.Bin.Stream {

    override lateinit var inBinStream: InputStream
    override lateinit var outBinStream: OutputStream

    override fun meta(context: FeatureContext) = Meta("extract from GZIP")

    override fun run(context: FeatureContext) {
        GZIPInputStream(inBinStream).use(outBinStream::transferFrom)
    }
}