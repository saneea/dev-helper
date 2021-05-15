package io.github.saneea.dvh.feature.print

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.PrintStream

class PrintArgsFeature :
    Feature,
    Feature.Out.Text.PrintStream {

    override lateinit var outTextPrintStream: PrintStream

    override fun meta(context: FeatureContext) = Meta("print CLI args")

    override fun run(context: FeatureContext) =
        context.args.forEach(outTextPrintStream::println)
}