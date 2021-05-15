package io.github.saneea.dvh.feature.clipboard

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class TextToClipboard :
    Feature,
    Feature.In.Text.String {

    override lateinit var inTextString: String

    override fun meta(context: FeatureContext) = Meta("write text to clipboard")

    override fun run(context: FeatureContext) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(inTextString)
        clipboard.setContents(selection, selection)
    }
}