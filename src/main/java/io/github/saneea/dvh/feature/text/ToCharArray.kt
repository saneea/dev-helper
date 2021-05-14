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

    private lateinit var `in`: Reader
    private lateinit var out: Writer

    override fun meta(context: FeatureContext) = Meta("convert input string to char array")

    override fun run(context: FeatureContext) {
        var charCode: Int
        var first = true
        while (`in`.read().also { charCode = it } != -1) {
            if (first) {
                first = false
            } else {
                out.append(", ")
            }
            out.append(
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

    override fun setIn(`in`: Reader) {
        this.`in` = `in`
    }

    override fun setOutTextWriter(out: Writer) {
        this.out = out
    }
}