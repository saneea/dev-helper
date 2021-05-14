package io.github.saneea.dvh.feature.time

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.StringConsumer
import io.github.saneea.dvh.feature.time.format.FormatFactory
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option

private const val IN_FORMAT = "inputFormat"
private const val OUT_FORMAT = "outputFormat"

class Converter :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.In.Text.String,
    Feature.Out.Text.String {

    private lateinit var `in`: String
    private lateinit var out: StringConsumer
    private lateinit var commandLine: CommandLine
    private val formatFactory = FormatFactory()

    override fun meta(context: FeatureContext) = Meta("convert time from original format to another one")

    override fun run(context: FeatureContext) {
        val inFormat = getFormatFromCLI(IN_FORMAT)
        val outFormat = getFormatFromCLI(OUT_FORMAT)
        val timeAsZoned = inFormat.parse(`in`)
        val converted = outFormat.render(timeAsZoned)
        out(converted)
    }

    private fun getFormatFromCLI(cliOptionName: String) =
        formatFactory.createFormat(commandLine.getOptionValue(cliOptionName))

    override fun setInTextString(`in`: String) {
        this.`in` = `in`
    }

    override fun setOutTextString(out: StringConsumer) {
        this.out = out
    }

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    override val options
        get() = arrayOf(
            createFormatCliOption("if", IN_FORMAT, "input time format"),
            createFormatCliOption("of", OUT_FORMAT, "output time format")
        )

    private fun createFormatCliOption(shortName: String, longName: String, description: String) =
        Option
            .builder(shortName)
            .longOpt(longName)
            .hasArg(true)
            .argName(formatFactory.availableFormatsString)
            .required(true)
            .desc(description)
            .build()
}