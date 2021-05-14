package io.github.saneea.dvh.feature.process

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.FeaturesCreators
import io.github.saneea.dvh.feature.multi.MultiFeatureBase

class Proc : MultiFeatureBase() {
    override fun meta(context: FeatureContext) = Meta("fork process")

    override fun getFeatureAliases(): FeaturesCreators =
        AliasesBuilder()
            .feature("inspect", ::InspectProcess)
            .feature("toFile", ::ToFile)
            .build()
}