package io.github.saneea.dvh.feature.multi

import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.FeatureProvider
import java.io.PrintStream

abstract class FeatureCatalogPrinter(protected val out: PrintStream) {
    abstract fun print(featureProvider: FeatureProvider, context: FeatureContext)
}