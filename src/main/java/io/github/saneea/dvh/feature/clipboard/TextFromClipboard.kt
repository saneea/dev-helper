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

    override lateinit var context: FeatureContext
    override lateinit var outTextString: StringConsumer

    override val meta = Meta("read text from clipboard")

    override fun run() {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val clipboardText = clipboard.getData(DataFlavor.stringFlavor) as String
        outTextString(clipboardText)
    }
}