package io.github.saneea.dvh.feature.clipboard

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.Feature.Util.IOConsumer
import io.github.saneea.dvh.FeatureContext
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class TextFromClipboard :
    Feature,
    Feature.Out.Text.String {

    private lateinit var out: IOConsumer<String>

    override fun meta(context: FeatureContext) =
        Meta.from("read text from clipboard")!!

    override fun run(context: FeatureContext) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val clipboardText = clipboard.getData(DataFlavor.stringFlavor) as String
        out.accept(clipboardText)
    }

    override fun setOut(out: IOConsumer<String>) {
        this.out = out
    }
}