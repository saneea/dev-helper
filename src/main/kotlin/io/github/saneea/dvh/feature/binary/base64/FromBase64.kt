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

    private var outStream: OutputStream? = null;
    private var inStream: InputStream? = null;

    override fun meta(context: FeatureContext?): Feature.Meta {
        return Meta.from("convert input Base64 sequence to binary")
    }

    override fun run(context: FeatureContext?) {
        Base64.getDecoder().wrap(inStream)
                .use { base64stream ->
                    base64stream.transferTo(outStream)
                }
    }

    override fun setOut(outStream: OutputStream?) {
        this.outStream = outStream
    }

    override fun setIn(inStream: InputStream?) {
        this.inStream = inStream
    }
}