package io.github.saneea.dvh.feature.random

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.feature.multi.FeaturesCreators
import io.github.saneea.dvh.feature.multi.MultiFeatureBase

class Random : MultiFeatureBase() {
    override val meta = Meta("generate random data")

    override fun getFeatureAliases(): FeaturesCreators =
        AliasesBuilder()
            .feature("bytes", ::RandomBytes)
            .feature("uuid", ::UUID)
            .build()
}