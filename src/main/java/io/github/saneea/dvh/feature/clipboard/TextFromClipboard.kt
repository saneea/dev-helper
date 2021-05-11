package io.github.saneea.dvh.feature.clipboard

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.StringConsumer
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class TextFromClipboard :
    Feature,
    Feature.Out.Text.String {

    private lateinit var out: StringConsumer

    override fun meta(context: FeatureContext) =
        Meta.from("read text from clipboard")

    override fun run(context: FeatureContext) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val clipboardText = clipboard.getData(DataFlavor.stringFlavor) as String
        out(clipboardText)
    }

    override fun setOut(out: StringConsumer) {
        this.out = out
    }
}