package io.github.saneea.dvh.feature.print

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.PrintStream

class PrintLine :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.Out.Text.PrintStream //
{
    private lateinit var out: PrintStream
    private lateinit var commandLine: CommandLine

    override fun meta(context: FeatureContext) =
        Meta.from("print text to output")!!

    override fun run(context: FeatureContext) {
        val text = commandLine.getOptionValue(TEXT)

        val printFunc: (String) -> Unit =
            if (commandLine.hasOption(NO_NEW_LINE))
                out::print
            else
                out::println

        printFunc(text)
    }

    override fun setOut(out: PrintStream) {
        this.out = out
    }

    override fun getOptions() =
        arrayOf(
            Option
                .builder("t")
                .longOpt(TEXT)
                .hasArg(true)
                .argName("text for printing")
                .required(true)
                .desc("e.g. 'Hello World'")
                .build(),
            Option
                .builder("nnl")
                .longOpt(NO_NEW_LINE)
                .required(false)
                .desc("do not add new line at the end of output")
                .build()
        )

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    companion object {
        private const val TEXT = "text"
        private const val NO_NEW_LINE = "noNewLine"
    }
}