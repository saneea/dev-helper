package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.StringConsumer
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option

class SlashReplacer :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.In.Text.String,
    Feature.Out.Text.String {

    override lateinit var inTextString: String
    override lateinit var outTextString: StringConsumer
    private lateinit var commandLine: CommandLine

    override fun meta(context: FeatureContext): Meta {

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

    override fun run(context: FeatureContext) {
        val slash = commandLine.getOptionValue(NEW_SLASH, "/")
        val regex = """([\\/])+""".toRegex()
        outTextString(inTextString.replace(regex, "\\$slash"))
    }

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
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