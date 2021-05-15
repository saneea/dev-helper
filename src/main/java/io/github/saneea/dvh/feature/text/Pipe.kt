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

    override lateinit var context: FeatureContext
    override lateinit var inTextReader: Reader
    override lateinit var outTextWriter: Writer

    override val meta get() = Meta("just transfer text from std_in to std_out")

    override fun run() {
        inTextReader.transferTo(outTextWriter)
    }
}