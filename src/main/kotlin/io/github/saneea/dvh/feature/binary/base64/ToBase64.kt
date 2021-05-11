package io.github.saneea.dvh.feature.binary.base64

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class ToBase64 :
    Feature,
    Feature.In.Bin.Stream,
    Feature.Out.Bin.Stream {

    private lateinit var `in`: InputStream
    private lateinit var out: OutputStream

    override fun meta(context: FeatureContext) =
        Meta.from("convert input binary sequence to Base64")


    override fun run(context: FeatureContext) {
        Base64.getEncoder().wrap(out).use(`in`::transferTo)
    }

    override fun setOut(out: OutputStream) {
        this.out = out
    }

    override fun setIn(`in`: InputStream) {
        this.`in` = `in`
    }
}