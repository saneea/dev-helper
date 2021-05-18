package io.github.saneea.dvh.feature.multi

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.CLI.CommonOptions
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.FeatureProvider
import io.github.saneea.dvh.FeatureRunner
import io.github.saneea.dvh.utils.Utils
import io.github.saneea.dvh.utils.Utils.DefaultHelpPrinter
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

abstract class MultiFeature :
    Feature,
    Feature.ContextAware {

    override lateinit var context: FeatureContext
    private val out = System.out
    abstract val featureProvider: FeatureProvider

    override fun run() {
        val args = context.args
        if (args.isEmpty() || args[0].startsWith("-")) {
            runFeatureWithOptions(context, args)
        } else {
            runChildFeature(context, args)
        }
    }

    private fun runChildFeature(context: FeatureContext, args: List<String>) {
        val featureName = args[0]
        val featureArgs = withoutFeatureName(args)
        val featureRunner = FeatureRunner(featureProvider)
        featureRunner.run(context, featureName, featureArgs)
    }

    private fun runFeatureWithOptions(context: FeatureContext, args: List<String>) {
        val cliOptions = Options()
            .addOption(CommonOptions.HELP_OPTION)
            .addOption(
                Option
                    .builder("c")
                    .longOpt(CATALOG)
                    .hasArg(true)
                    .argName("$CATALOG_LIST|$CATALOG_TREE")
                    .required(false)
                    .desc("features catalog show mode")
                    .build()
            )
        val printHelp = MultiFeatureHelpPrinter(cliOptions, context)
        val commandLine = Utils.parseCli(args, cliOptions, printHelp)
        printHelp(commandLine)
    }

    private inner class MultiFeatureHelpPrinter(options: Options, context: FeatureContext) :
        DefaultHelpPrinter(options, this@MultiFeature, context) {
        override fun invoke(commandLine: CommandLine?) {
            printDescription()
            out.println(
                "usage: "
                        + cmdLineSyntax
                        + " <featureName> [feature args]"
            )
            out.println("--- or ---")
            printCLI()
            printFeaturesCatalog(context, commandLine)
        }
    }

    private fun printFeaturesCatalog(context: FeatureContext, commandLine: CommandLine?) {
        val catalogMode = commandLine?.getOptionValue(CATALOG) ?: CATALOG_LIST

        out.println()
        out.println("available features:")
        getCatalogPrinter(catalogMode)
            .print(featureProvider, context)
    }

    private fun getCatalogPrinter(catalogMode: String): FeatureCatalogPrinter {
        return when (catalogMode) {
            CATALOG_LIST -> ListPrinter(out)
            CATALOG_TREE -> TreePrinter(out)
            else -> throw IllegalArgumentException("Invalid $CATALOG mode: $catalogMode")
        }
    }

    companion object {
        const val CATALOG = "catalog"
        const val CATALOG_LIST = "list"
        const val CATALOG_TREE = "tree"

        private fun withoutFeatureName(args: List<String>) = if (args.isEmpty()) {
            args
        } else {
            args.drop(1)
        }
    }
}