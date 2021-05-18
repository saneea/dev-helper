package io.github.saneea.dvh.feature.time

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
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

    override lateinit var inTextString: String
    override lateinit var outTextString: StringConsumer
    override lateinit var commandLine: CommandLine
    private val formatFactory = FormatFactory()

    override val meta = Meta("convert time from original format to another one")

    override fun run() {
        val inFormat = getFormatFromCLI(IN_FORMAT)
        val outFormat = getFormatFromCLI(OUT_FORMAT)
        val timeAsZoned = inFormat.parse(inTextString)
        val converted = outFormat.render(timeAsZoned)
        outTextString(converted)
    }

    private fun getFormatFromCLI(cliOptionName: String) =
        formatFactory.createFormat(commandLine.getOptionValue(cliOptionName))

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