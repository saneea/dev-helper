package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.Reader
import java.io.Writer

class AddNewLine :
    Feature,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {

    override lateinit var inTextReader: Reader
    override lateinit var outTextWriter: Writer

    override fun meta(context: FeatureContext) =
        Meta("add platform-dependent 'newline' sequence (e.g. '\\n', '\\r\\n') at the end")

    override fun run(context: FeatureContext) {
        inTextReader.transferTo(outTextWriter)
        outTextWriter.append(System.lineSeparator())
    }
}