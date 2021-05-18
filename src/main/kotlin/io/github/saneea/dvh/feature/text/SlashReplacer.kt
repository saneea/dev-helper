package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.StringConsumer
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option

class SlashReplacer :
    Feature,
    Feature.ContextAware,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.In.Text.String,
    Feature.Out.Text.String {

    override lateinit var context: FeatureContext
    override lateinit var inTextString: String
    override lateinit var outTextString: StringConsumer
    override lateinit var commandLine: CommandLine

    override val meta: Meta
        get() {

            val echoPipeFeature = "echo some////path\\\\to\\\\file.txt | " + context.featuresChainString

            return Meta(
                Meta.Description(
                    "replace slashes (/) or backslashes (\\)",
                    "replace slashes (/) or backslashes (\\) to specified string"
                ),
                listOf(
                    Meta.Example(
                        "replace to slashes",
                        echoPipeFeature,
                        "some/path/to/file.txt"
                    ),
                    Meta.Example(
                        "replace to custom string",
                        "$echoPipeFeature -$NEW_SLASH_SHORT \\",
                        "some\\path\\to\\file.txt"
                    )
                )
            )
        }

    override fun run() {
        val slash = commandLine.getOptionValue(NEW_SLASH, "/")
        val regex = """([\\/])+""".toRegex()
        outTextString(inTextString.replace(regex, "\\$slash"))
    }

    override val options: Array<Option>
        get() {
            return arrayOf(
                Option
                    .builder(NEW_SLASH_SHORT)
                    .longOpt(NEW_SLASH)
                    .hasArg(true)
                    .argName("slash string")
                    .required(false)
                    .desc("set new slash string")
                    .build()
            )
        }

    companion object {
        private const val NEW_SLASH = "newSlash"
        private const val NEW_SLASH_SHORT = "n"
    }
}