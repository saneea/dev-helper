package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.Reader
import java.io.Writer

class ToCharArray :
    Feature,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {

    override lateinit var context: FeatureContext
    override lateinit var inTextReader: Reader
    override lateinit var outTextWriter: Writer

    override val meta = Meta("convert input string to char array")

    override fun run() {
        var charCode: Int
        var first = true
        while (inTextReader.read().also { charCode = it } != -1) {
            if (first) {
                first = false
            } else {
                outTextWriter.append(", ")
            }
            outTextWriter.append(
                wrapInQuotes(
                    wrapIfEscaped(
                        charCode.toChar()
                    )
                )
            )
        }
    }

    private fun wrapInQuotes(s: String): String {
        return "'$s'"
    }

    private fun wrapIfEscaped(c: Char) =
        when (c) {
            '\u0000' -> "\\0"
            '\r' -> "\\r"
            '\n' -> "\\n"
            '\t' -> "\\t"
            '\b' -> "\\b"
            '\'', '\\' -> "\\" + c
            else -> "" + c
        }
}