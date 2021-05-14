package io.github.saneea.dvh.utils

import io.github.saneea.dvh.AppExitException
import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.CLI.CommonOptions
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.utils.const.BOM_CHAR
import io.github.saneea.dvh.utils.const.END_OF_STREAM
import org.apache.commons.cli.*
import java.io.PushbackReader
import java.io.Reader
import java.util.*

typealias PrintHelpFunc = (commandLine: CommandLine?) -> Unit

object Utils {
    val NO_ARGS = arrayOf<String>()

    fun toMap(properties: Properties) =
        properties
            .stringPropertyNames()
            .associateWith(properties::getProperty)

    fun skipBom(originalReader: Reader): Reader {
        val ret = PushbackReader(originalReader)
        val firstChar = ret.read()
        if (firstChar != BOM_CHAR.toInt() &&
            firstChar != END_OF_STREAM
        ) {
            ret.unread(firstChar)
        }
        return ret
    }

    fun parseCli(args: Array<String>, cliOptions: Options, feature: Feature, context: FeatureContext) =
        parseCli(args, cliOptions, DefaultHelpPrinter(cliOptions, feature, context))

    fun parseCli(args: Array<String>, cliOptions: Options, printHelp: PrintHelpFunc): CommandLine {
        val commandLineParser: CommandLineParser = DefaultParser()
        val commandLine = try {
            commandLineParser.parse(cliOptions, args)
        } catch (e: ParseException) {
            System.err.println(e.localizedMessage)
            printHelp(null)
            throw AppExitException(AppExitException.ExitCode.ERROR, e)
        }
        if (commandLine.hasOption(CommonOptions.HELP)) {
            printHelp(commandLine)
            throw AppExitException(AppExitException.ExitCode.OK)
        }
        return commandLine
    }

    open class DefaultHelpPrinter(
        protected val options: Options,
        protected val feature: Feature,
        protected val context: FeatureContext
    ) : PrintHelpFunc {
        protected val cmdLineSyntax = context.featuresChainString
        override fun invoke(commandLine: CommandLine?) {
            printDescription()
            printCLI()
            printExamples()
        }

        protected open fun printExamples() {
            val examples = feature.meta(context).examples
            val examplesCount = examples.size
            if (examplesCount > 0) {
                println()
                println("Examples ($examplesCount):")
                for (i in 0 until examplesCount) {
                    val example = examples[i]
                    println("" + (i + 1) + ". " + example.name)
                    println("run command:")
                    println("\t" + example.body)
                    example.result?.let { result: String ->
                        println("output:")
                        println("\t" + result)
                    }
                    println()
                }
            }
        }

        protected fun printCLI() = HelpFormatter().printHelp(cmdLineSyntax, options, true)

        protected fun printDescription() {
            println(feature.meta(context).description.detailed)
            println()
        }

    }
}

val Iterable<String>.maxStringLength
    get() = this.map(String::length).maxOrNull() ?: 0