package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.Reader
import java.io.Writer

class Pipe :
    Feature,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {

    private lateinit var `in`: Reader
    private lateinit var out: Writer

    override fun meta(context: FeatureContext) = Meta("just transfer text from std_in to std_out")

    override fun run(context: FeatureContext) {
        `in`.transferTo(out)
    }

    override fun setIn(`in`: Reader) {
        this.`in` = `in`
    }

    override fun setOut(out: Writer) {
        this.out = out
    }
}