package io.github.saneea.dvh.feature.time

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.FeatureCreator
import io.github.saneea.dvh.feature.multi.MultiFeatureBase

class Time : MultiFeatureBase() {
    override val meta = Meta("time utils")

    override fun getFeatureAliases(): Map<String, FeatureCreator> =
        AliasesBuilder()
            .feature("now", ::Now)
            .feature("convert", ::Converter)
            .feature("sleep", ::Sleep)
            .build()
}