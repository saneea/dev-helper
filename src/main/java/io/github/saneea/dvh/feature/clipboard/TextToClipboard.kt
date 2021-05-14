package io.github.saneea.dvh.feature.clipboard

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class TextToClipboard :
    Feature,
    Feature.In.Text.String {

    private lateinit var `in`: String

    override fun meta(context: FeatureContext) = Meta("write text to clipboard")

    override fun run(context: FeatureContext) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(`in`)
        clipboard.setContents(selection, selection)
    }

    override fun setIn(`in`: String) {
        this.`in` = `in`
    }
}