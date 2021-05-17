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
    Feature.Out.Text.PrintStream {

    override lateinit var context: FeatureContext
    override lateinit var outTextPrintStream: PrintStream
    override lateinit var commandLine: CommandLine

    override val meta = Meta("print text to output")

    override fun run() {
        val text = commandLine.getOptionValue(TEXT)

        val printFunc: (String) -> Unit =
            if (commandLine.hasOption(NO_NEW_LINE))
                outTextPrintStream::print
            else
                outTextPrintStream::println

        printFunc(text)
    }

    override val options
        get() = arrayOf(
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

    companion object {
        private const val TEXT = "text"
        private const val NO_NEW_LINE = "noNewLine"
    }
}