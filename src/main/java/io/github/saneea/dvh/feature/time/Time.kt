package io.github.saneea.dvh.feature.time

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import java.util.function.Supplier

class Time : MultiFeatureBase() {
    override fun meta(context: FeatureContext) = Meta.from("time utils")!!

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> =
        AliasesBuilder()
            .feature("now", ::Now)
            .feature("convert", ::Converter)
            .feature("sleep", ::Sleep)
            .build()
}