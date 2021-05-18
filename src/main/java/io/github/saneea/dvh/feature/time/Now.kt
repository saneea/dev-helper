package io.github.saneea.dvh.feature.time

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.StringConsumer
import io.github.saneea.dvh.feature.time.format.FORMAT_HUMAN
import io.github.saneea.dvh.feature.time.format.FormatFactory
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.time.ZonedDateTime

class Now :
    Feature,
    Feature.ContextAware,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.Out.Text.String {

    override lateinit var context: FeatureContext
    override lateinit var outTextString: StringConsumer
    override lateinit var commandLine: CommandLine
    private val formatFactory = FormatFactory()

    override val meta: Meta
        get() {
            val featuresChain = context.featuresChainString
            return Meta(
                Meta.Description(
                    "print current time",
                    "print current date and/or time in one of format"
                ),
                listOf(
                    Meta.Example(
                        "seconds from 1970-01-01 (known as Unix-time or Epoch-time)",
                        "$featuresChain -$FORMAT_SHORT unix",
                        "1606339702"
                    ),
                    Meta.Example(
                        "custom date/time pattern",
                        "$featuresChain -$FORMAT_SHORT yyyy-MM-dd--HH:mm:ss",
                        "2020-11-26--00:36:55"
                    )
                )
            )
        }

    override fun run() {
        val formatNameOrPattern = commandLine.getOptionValue(FORMAT, FORMAT_HUMAN)
        val format = formatFactory.createFormat(formatNameOrPattern)
        val formattedTime = format.render(ZonedDateTime.now())
        outTextString(formattedTime)
    }

    override val options: Array<Option>
        get() = arrayOf(
            Option
                .builder(FORMAT_SHORT)
                .longOpt(FORMAT)
                .hasArg(true)
                .argName(formatFactory.availableFormatsString)
                .required(false)
                .desc("output time format")
                .build()
        )

    companion object {
        private const val FORMAT = "format"
        private const val FORMAT_SHORT = "f"
    }
}