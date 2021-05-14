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

    private lateinit var `in`: InputStream
    private lateinit var out: OutputStream

    override fun meta(context: FeatureContext) = Meta("extract from GZIP")

    override fun run(context: FeatureContext) {
        GZIPInputStream(`in`).use(out::transferFrom)
    }

    override fun setIn(`in`: InputStream) {
        this.`in` = `in`
    }

    override fun setOutBinStream(out: OutputStream) {
        this.out = out
    }
}