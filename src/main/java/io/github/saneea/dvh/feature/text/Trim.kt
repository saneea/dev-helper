package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.text.Trim.TextConverter
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.Reader
import java.io.Writer
import java.util.*
import java.util.function.Supplier

class Trim :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {

    private lateinit var `in`: Reader
    private lateinit var out: Writer
    private lateinit var commandLine: CommandLine

    override fun meta(context: FeatureContext) =
        Meta.from("trim leading and/or trailing whitespaces")!!

    override fun run(context: FeatureContext) {
        var converter = TextConverter.orig()
        converter = addTrimmer(LEFT_TRIM, converter, ::LeftTrimmer)
        converter = addTrimmer(RIGHT_TRIM, converter, ::RightTrimmer)
        var charCode: Int
        while (`in`.read().also { charCode = it } != -1) {
            converter.convertChar(charCode) { c: Int -> out.write(c) }
        }
    }

    private fun addTrimmer(
        optName: String,
        currentConverter: TextConverter,
        newConverterCtor: Supplier<TextConverter>
    ): TextConverter {
        val optValue = commandLine
            .getOptionValue(optName, "true")
            .toLowerCase()
        return when (optValue) {
            "true" -> currentConverter.andThen(newConverterCtor.get())
            "false" -> currentConverter
            else -> throw IllegalArgumentException("Invalid $optName value: $optValue")
        }
    }

    override fun setIn(`in`: Reader) {
        this.`in` = `in`
    }

    override fun setOut(out: Writer) {
        this.out = out
    }

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    override fun getOptions(): Array<Option> {
        return arrayOf(
            createTrimCliOption("l", LEFT_TRIM, "trim leading whitespaces"),
            createTrimCliOption("r", RIGHT_TRIM, "trim trailing whitespaces")
        )
    }

    private fun createTrimCliOption(shortName: String, longName: String, description: String): Option =
        Option
            .builder(shortName)
            .longOpt(longName)
            .hasArg(true)
            .argName("true|false")
            .required(false)
            .desc("$description (default is 'true')")
            .build()

    private class LeftTrimmer : TextConverter {
        var trimming = true

        override fun convertChar(charCode: Int, w: CharWriter) {
            if (!trimming) {
                w.write(charCode)
            } else if (!Character.isWhitespace(charCode)) {
                w.write(charCode)
                trimming = false
            }
        }
    }

    private class RightTrimmer : TextConverter {
        private val buffer: Queue<Int> = LinkedList()

        override fun convertChar(charCode: Int, w: CharWriter) {
            if (Character.isWhitespace(charCode)) {
                buffer.offer(charCode)
            } else {
                flush(w)
                w.write(charCode)
            }
        }

        private fun flush(w: CharWriter) {
            var fromBuff: Int
            while (buffer.poll().also { fromBuff = it } != null) {
                w.write(fromBuff)
            }
        }
    }

    private fun interface CharWriter {
        fun write(charCode: Int)
    }

    private fun interface TextConverter {
        fun convertChar(charCode: Int, w: CharWriter)
        fun andThen(nextConverter: TextConverter): TextConverter {
            return TextConverter { charCode: Int, w: CharWriter ->  //
                convertChar(charCode) { charCodeConverted: Int ->  //
                    nextConverter.convertChar(charCodeConverted, w)
                }
            }
        }

        companion object {
            fun orig(): TextConverter =
                TextConverter { charCode: Int, w: CharWriter -> w.write(charCode) }
        }
    }

    companion object {
        private const val LEFT_TRIM = "leftTrim"
        private const val RIGHT_TRIM = "rightTrim"
    }
}