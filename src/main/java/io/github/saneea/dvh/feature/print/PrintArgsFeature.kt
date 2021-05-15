package io.github.saneea.dvh.feature.print

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.PrintStream

class PrintArgsFeature :
    Feature,
    Feature.Out.Text.PrintStream {

    override lateinit var context: FeatureContext
    override lateinit var outTextPrintStream: PrintStream

    override val meta get() = Meta("print CLI args")

    override fun run() =
        context.args.forEach(outTextPrintStream::println)
}