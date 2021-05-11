package io.github.saneea.dvh.feature.print

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.PrintStream

class PrintArgsFeature :
    Feature,
    Feature.Out.Text.PrintStream {

    private lateinit var out: PrintStream

    override fun meta(context: FeatureContext) =
        Meta.from("print CLI args")

    override fun run(context: FeatureContext) =
        context.args.forEach(out::println)

    override fun setOut(out: PrintStream) {
        this.out = out
    }
}