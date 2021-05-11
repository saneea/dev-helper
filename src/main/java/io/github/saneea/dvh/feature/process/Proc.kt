package io.github.saneea.dvh.feature.process

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import java.util.function.Supplier

class Proc : MultiFeatureBase() {
    override fun meta(context: FeatureContext) =
        Meta.from("fork process")

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> =
        AliasesBuilder()
            .feature("inspect", ::InspectProcess)
            .feature("toFile", ::ToFile)
            .build()
}