package io.github.saneea.dvh.feature.multi

import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.FeatureProvider
import io.github.saneea.dvh.utils.Utils
import java.io.PrintStream

class ListPrinter(out: PrintStream) : FeatureCatalogPrinter(out) {

    override fun print(featureProvider: FeatureProvider, context: FeatureContext) {
        val featuresNames = featureProvider.featuresNames()
        val maxFeatureNameSize = Utils.getMaxStringLength(featuresNames)
        for (featureName in featuresNames) {
            val featureInfo = featureProvider.featureInfo(
                FeatureContext(context, featureName, Utils.NO_ARGS)
            )
            val template = "%1$" + maxFeatureNameSize + "s - %2\$s"
            out.println(String.format(template, featureName, featureInfo.description))
        }
    }
}