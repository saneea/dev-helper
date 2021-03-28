package io.github.saneea.dvh.feature.binary.gzip

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPOutputStream

class ToGzip :
    Feature,
    Feature.In.Bin.Stream,
    Feature.Out.Bin.Stream {

    private lateinit var `in`: InputStream
    private lateinit var out: OutputStream

    override fun meta(context: FeatureContext) =
        Meta.from("compress to GZIP")!!

    override fun run(context: FeatureContext) {
        GZIPOutputStream(out).use(`in`::transferTo)
    }

    override fun setIn(`in`: InputStream) {
        this.`in` = `in`
    }

    override fun setOut(out: OutputStream) {
        this.out = out
    }
}