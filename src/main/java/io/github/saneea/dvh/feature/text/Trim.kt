package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.Reader
import java.io.Writer
import java.util.*

private const val LEFT_TRIM = "leftTrim"
private const val RIGHT_TRIM = "rightTrim"

private typealias WriteCharFunc = (Int) -> Unit

private typealias ConvertFunc = (Int, WriteCharFunc) -> Unit

private val NO_CONVERSION: ConvertFunc = { charCode, writeFun -> writeFun(charCode) }

private infix fun ConvertFunc.and(nextConverter: ConvertFunc): ConvertFunc =
    { charCode, writeFun -> this(charCode) { nextConverter(it, writeFun) } }

class Trim :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {

    private lateinit var `in`: Reader
    private lateinit var out: Writer
    private lateinit var commandLine: CommandLine

    override fun meta(context: FeatureContext) = Meta("trim leading and/or trailing whitespaces")

    override fun run(context: FeatureContext) {
        val convertFunc = NO_CONVERSION
            .and(LEFT_TRIM, ::leftTrimmer)
            .and(RIGHT_TRIM, ::rightTrimmer)

        var charCode: Int
        while (`in`.read().also { charCode = it } != -1) {
            convertFunc(charCode, out::write)
        }
    }

    private fun ConvertFunc.and(
        optName: String,
        newTrimmer: () -> ConvertFunc
    ): ConvertFunc {
        val optValue = commandLine
            .getOptionValue(optName, "true")
            .toLowerCase()
        return when (optValue) {
            "false" -> this
            "true" -> this and newTrimmer()
            else -> throw IllegalArgumentException("Invalid $optName value: $optValue")
        }
    }

    override fun setInTextReader(`in`: Reader) {
        this.`in` = `in`
    }

    override fun setOutTextWriter(out: Writer) {
        this.out = out
    }

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    override val options: Array<Option>
        get() {
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

    private fun leftTrimmer(): ConvertFunc {
        var trimming = true

        return { charCode, writeFun ->
            if (!trimming) {
                writeFun(charCode)
            } else if (!Character.isWhitespace(charCode)) {
                writeFun(charCode)
                trimming = false
            }
        }
    }

    private fun rightTrimmer(): ConvertFunc {
        val buffer: Queue<Int> = LinkedList()

        fun flush(writeFun: WriteCharFunc) {
            while (true) {
                val fromBuff = buffer.poll() ?: break
                writeFun(fromBuff)
            }
        }

        return { charCode, writeFun ->
            if (Character.isWhitespace(charCode)) {
                buffer.offer(charCode)
            } else {
                flush(writeFun)
                writeFun(charCode)
            }
        }
    }
}